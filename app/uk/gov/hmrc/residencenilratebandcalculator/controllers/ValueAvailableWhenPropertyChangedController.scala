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

import play.api.Logging

import javax.inject.{Inject, Singleton}
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, AnswerRows, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_available_when_property_changed
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValueAvailableWhenPropertyChangedController @Inject()(cc: DefaultMessagesControllerComponents,
                                                            val sessionConnector: SessionConnector,
                                                            val navigator: Navigator,
                                                            val rnrbConnector: RnrbConnector,
                                                            validatedSession: ValidatedSession,
                                                            valueAvailableWhenPropertyChangedView: value_available_when_property_changed)
                                                           (implicit ec: ExecutionContext) extends FrontendController(cc) with I18nSupport with Logging {

  val controllerId: String = Constants.valueAvailableWhenPropertyChangedId

  def form = () =>
    NonNegativeIntForm("value_available_when_property_changed.error.blank", "error.whole_pounds", "error.non_numeric", "error.value_too_large")

  private def getCacheMap(implicit hc: HeaderCarrier): Future[CacheMap] = sessionConnector.fetch().map {
    case Some(cacheMap) => cacheMap
    case None => throw new NoCacheMapException("CacheMap not available")
  }

  def microserviceValues(implicit hc: HeaderCarrier): Future[(HttpResponse, CacheMap)] = for {
    dateOfDisposal <- getCacheMap.map(_.data(Constants.datePropertyWasChangedId).toString().replaceAll("\"", ""))
    nilRateValueJson <- rnrbConnector.getNilRateBand(dateOfDisposal)
    cacheMap <- getCacheMap
  } yield (nilRateValueJson, cacheMap)

  def answerRows(cacheMap: CacheMap, request: Request[_]): Seq[AnswerRow] = AnswerRows.constructAnswerRows(
    AnswerRows.truncateAndLocateInCacheMap(controllerId, cacheMap),
    AnswerRows.answerRowFns,
    AnswerRows.rowOrder,
    messagesApi.preferred(request)
  )

  def onPageLoad(implicit rds: Reads[Int]) = Action.async {
    implicit request => {
      microserviceValues.map {
        case (nilRateValueJson, cacheMap) => {
          val nilRateBand = CurrencyFormatter.format(nilRateValueJson.json.toString())

          Ok(valueAvailableWhenPropertyChangedView(
            nilRateBand,
            cacheMap.getEntry(controllerId).fold(form())(value => form().fill(value))))
        }
      } recover {
        case _: NoCacheMapException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
        case r: RuntimeException =>
          logger.error(r.getMessage, r)
          throw r
      }
    }
  }

  def onSubmit(implicit wts: Writes[Int]) = validatedSession.async {
    implicit request => {
      microserviceValues.flatMap {
        case (nilRateValueJson, cacheMap) => {
          val boundForm = form().bindFromRequest()
          val nilRateBand = nilRateValueJson.json.toString()
          val formattedNilRateBand = CurrencyFormatter.format(nilRateBand)

          boundForm.fold(
            formWithErrors => Future.successful(BadRequest(valueAvailableWhenPropertyChangedView(
              formattedNilRateBand, formWithErrors))),
            value => {
              validate(value, nilRateBand).flatMap {
                case Some(error) => Future.successful(BadRequest(valueAvailableWhenPropertyChangedView(
                  formattedNilRateBand,
                  form().fill(value).withError(error))))
                case None => sessionConnector.cache[Int](controllerId, value).map(cacheMap => Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
              }
            }
          )
        }
      } recover {
        case _: NoCacheMapException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
        case r: RuntimeException =>
          logger.error(r.getMessage, r)
          throw r
      }
    }
  }


  def validate(value: Int, nilRateBandStr: String): Future[Option[FormError]] = {
    val nrb = try {
      Integer.parseInt(nilRateBandStr)
    } catch {
      case e: NumberFormatException => {
        logger.error(e.getMessage, e)
        throw new NumberFormatException("Bad value in nil rate band")
      }
    }

    if (value <= nrb) {
      Future.successful(None)
    } else {
      Future.successful(Some(FormError("value", "value_available_when_property_changed.error", Seq(nrb))))
    }
  }
}
