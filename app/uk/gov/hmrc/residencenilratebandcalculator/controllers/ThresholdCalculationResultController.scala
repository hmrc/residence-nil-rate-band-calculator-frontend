/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import javax.inject.{Inject, Singleton}

import com.google.inject.Provider
import play.Logger
import play.api.Application
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, CalculationInput, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.utils.{CurrencyFormatter}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.threshold_calculation_result

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class ThresholdCalculationResultController @Inject()(val messagesApi: MessagesApi,
                                                     rnrbConnector: RnrbConnector, sessionConnector: SessionConnector,
                                                     implicit val applicationProvider: Provider[Application])
  extends FrontendController with I18nSupport {

  private def fail(ex: Throwable) = {
    Logger.error(ex.getMessage, ex)
    InternalServerError(ex.getMessage)
  }

  private def getAnswers(implicit hc: HeaderCarrier) = sessionConnector.fetch().map {
    case Some(answers) => Success(answers)
    case None => Failure(new NoCacheMapException("Unable to retrieve cache map from SessionConnector"))
  }

  private def getInput(tryAnswers: Try[CacheMap]) = tryAnswers match {
    case Success(answers) => Future.successful(Try(CalculationInput(new UserAnswers(answers))))
    case Failure(ex) => Future.successful(Failure(ex))
  }

  private def getResult(tryInput: Try[CalculationInput]) = tryInput match {
    case Success(input) => rnrbConnector.send(input)
    case Failure(ex) => Future.successful(Failure(ex))
  }

  def onPageLoad = Action.async {
    implicit request => {
      for {
        tryAnswers <- getAnswers
        tryInput <- getInput(tryAnswers)
        tryResult <- getResult(tryInput)
      } yield {
        (tryResult, tryAnswers) match {
          case (_, Failure(ex)) => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
          case (Failure(ex), _) => fail(ex)
          case (Success(result), Success(answers)) =>
            sessionConnector.cache[Int](Constants.thresholdCalculationResultId, result.residenceNilRateAmount)
            val messages = messagesApi.preferred(request)
            val residenceNilRateAmount = CurrencyFormatter.format(result.residenceNilRateAmount)
            Ok(threshold_calculation_result(residenceNilRateAmount, AnswerRows(answers, messages)))
        }
      }
    }
  }
}
