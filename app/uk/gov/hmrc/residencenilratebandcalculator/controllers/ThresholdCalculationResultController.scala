/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter
import uk.gov.hmrc.residencenilratebandcalculator.views.html.threshold_calculation_result

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class ThresholdCalculationResultController @Inject()(cc: DefaultMessagesControllerComponents,
                                                     rnrbConnector: RnrbConnector, sessionConnector: SessionConnector,
                                                     validatedSession: ValidatedSession,
                                                     thresholdCalculationResultView: threshold_calculation_result)
                                                    (implicit ec: ExecutionContext)
  extends FrontendController(cc) with I18nSupport with Logging {

  private def fail(ex: Throwable) = {
    logger.error(ex.getMessage, ex)
    throw ex
  }

  private def getAnswers(implicit hc: HeaderCarrier) = sessionConnector.fetch().map {
    case Some(answers) => Success(answers)
    case None => Failure(new NoCacheMapException("Unable to retrieve cache map from SessionConnector"))
  }

  private def getInput(tryAnswers: Try[CacheMap]) = tryAnswers match {
    case Success(answers) => Future.successful(Try(CalculationInput(new UserAnswers(answers))))
    case Failure(ex) => Future.successful(Failure(ex))
  }

  private def getResult(tryInput: Try[CalculationInput]) (implicit hc: HeaderCarrier) = tryInput match {
    case Success(input) => rnrbConnector.send(input)
    case Failure(ex) => Future.successful(Failure(ex))
  }

  def onPageLoad = validatedSession.async {
    implicit request => {

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

      for {
        tryAnswers <- getAnswers
        tryInput <- getInput(tryAnswers)
        tryResult <- getResult(tryInput)
      } yield {
        (tryResult, tryAnswers) match {
          case (_, Failure(_)) => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
          case (Failure(ex), _) => fail(ex)
          case (Success(result), Success(_)) =>
            sessionConnector.cache[Int](Constants.thresholdCalculationResultId, result.residenceNilRateAmount)
            val residenceNilRateAmount = CurrencyFormatter.format(result.residenceNilRateAmount)
            Ok(thresholdCalculationResultView(residenceNilRateAmount))
        }
      }
    }
  }
}
