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

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.data.{Form, FormError}
import play.api.i18n.MessagesApi
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, Request, Result}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.views.html.brought_forward_allowance
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class BroughtForwardAllowanceController @Inject()(val appConfig: FrontendAppConfig,
                                                  val messagesApi: MessagesApi,
                                                  val sessionConnector: SessionConnector,
                                                  val navigator: Navigator, val rnrbConnector: RnrbConnector) extends FrontendController {

  val controllerId = Constants.broughtForwardAllowanceId

  def form = () => NonNegativeIntForm()

  def view(form: Option[Form[Int]], backUrl: String, nilRateBand: String)(implicit request: Request[_]) = {
    implicit val messages = messagesApi.preferred(request)
    brought_forward_allowance(appConfig, backUrl, nilRateBand, form)
  }

  private def getDateOfDeath(implicit hc: HeaderCarrier): Future[String] = sessionConnector.fetch().map {
    case Some(cacheMap) => cacheMap.data(Constants.dateOfDeathId).toString().replaceAll("\"", "")
    case None => throw new RuntimeException("Dod problem")
  }

  private def getCacheMap(implicit hc: HeaderCarrier): Future[CacheMap] = sessionConnector.fetch().map {
    case Some(cacheMap) => cacheMap
    case None => throw new NoCacheMapException("CacheMap")
  }

  def microserviceValues(implicit hc: HeaderCarrier): Future[(HttpResponse, CacheMap)] = for {
    dateOfDeath <- getDateOfDeath
    nilRateValueJson <- rnrbConnector.getNilRateBand(dateOfDeath)
    cacheMap <- getCacheMap
  } yield (nilRateValueJson, cacheMap)

  def onPageLoad(implicit rds: Reads[Int]) = Action.async {
    implicit request => {
      microserviceValues.map {
        case (nilRateValueJson, cacheMap) => {
          val nilRateBand = nilRateValueJson.json.toString()
          Ok(view(cacheMap.getEntry(controllerId).map(value => form().fill(value)), navigator.lastPage(controllerId).toString(), nilRateBand))
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
          boundForm.fold(
            formWithErrors => Future.successful(BadRequest(view(Some(formWithErrors), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url, nilRateBand))),
            (value) => {
              validate(value).flatMap {
                case Some(error) => Future.successful(BadRequest(view(Some(form().fill(value).withError(error)), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url, nilRateBand)))
                case None => sessionConnector.cache[Int](controllerId, value).map(cacheMap => Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
              }
            }
          )
        } recover {
          case n: NoCacheMapException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
          case r: RuntimeException => {
            Logger.error(r.getMessage, r)
            throw r
          }
        }
      }
    }
  }

  def validate(value: Int)(implicit hc: HeaderCarrier): Future[Option[FormError]] = Future.successful(None)
}
