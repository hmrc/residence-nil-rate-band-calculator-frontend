/*
 * Copyright 2017 HM Revenue & Customs
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

import java.text.NumberFormat
import java.util.Locale
import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.data.FormError
import play.api.i18n.MessagesApi
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, AnswerRows, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_available_when_property_changed
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import scala.concurrent.Future

@Singleton
class ValueAvailableWhenPropertyChangedController @Inject()(val appConfig: FrontendAppConfig,
                                                            val messagesApi: MessagesApi,
                                                            val sessionConnector: SessionConnector,
                                                            val navigator: Navigator, val rnrbConnector: RnrbConnector) extends FrontendController {

  val controllerId = Constants.valueAvailableWhenPropertyChangedId

  def form = () => NonNegativeIntForm()

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

  def formatJsonNumber(numberStr: String): String = {
    val number = Integer.parseInt(numberStr)
    NumberFormat.getCurrencyInstance(Locale.UK).format(number)
  }

  def onPageLoad(implicit rds: Reads[Int]) = Action.async {
    implicit request => {
      microserviceValues.map {
        case (nilRateValueJson, cacheMap) => {
          val previousAnswers = answerRows(cacheMap, request)
          val userAnswers = new UserAnswers(cacheMap)
          val nilRateBand = formatJsonNumber(nilRateValueJson.json.toString())
          implicit val messages = messagesApi.preferred(request)
          Ok(value_available_when_property_changed(appConfig, navigator.lastPage(controllerId)(userAnswers).url,
            nilRateBand,
            cacheMap.getEntry(controllerId).map(value => form().fill(value)),
            previousAnswers))
        }
      } recover {
        case n: NoCacheMapException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
        case r: RuntimeException => {
          Logger.error(r.getMessage, r)
          throw r
        }
      }
    }
  }

  def onSubmit(implicit wts: Writes[Int]) = Action.async {
    implicit request => {
      microserviceValues.flatMap {
        case (nilRateValueJson, cacheMap) => {
          val boundForm = form().bindFromRequest()
          val nilRateBand = nilRateValueJson.json.toString()
          val formattedNilRateBand = formatJsonNumber(nilRateBand)
          val previousAnswers = answerRows(cacheMap, request)
          val userAnswers = new UserAnswers(cacheMap)
          implicit val messages = messagesApi.preferred(request)
          boundForm.fold(
            formWithErrors => Future.successful(BadRequest(value_available_when_property_changed(appConfig,
              navigator.lastPage(controllerId)(userAnswers).url, formattedNilRateBand, Some(formWithErrors), previousAnswers))),
            (value) => {
              validate(value, nilRateBand).flatMap {
                case Some(error) => Future.successful(BadRequest(value_available_when_property_changed(appConfig,
                  navigator.lastPage(controllerId)(userAnswers).url,
                  formattedNilRateBand,
                  Some(form().fill(value).withError(error)),
                  previousAnswers)))
                case None => sessionConnector.cache[Int](controllerId, value).map(cacheMap => Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
              }
            }
          )
        }
      } recover {
        case n: NoCacheMapException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
        case r: RuntimeException => {
          Logger.error(r.getMessage, r)
          throw r
        }
      }
    }
  }


  def validate(value: Int, nilRateBandStr: String)(implicit hc: HeaderCarrier): Future[Option[FormError]] = {
    val nrb = try {
      Integer.parseInt(nilRateBandStr)
    } catch {
      case e: NumberFormatException => {
        Logger.error(e.getMessage, e)
        throw new NumberFormatException("Bad value in nil rate band")
      }
    }

    if (value <= nrb) {
      Future.successful(None)
    } else {
      Future.successful(Some(FormError("value", "value_available_when_property_changed.error")))
    }

  }
}
