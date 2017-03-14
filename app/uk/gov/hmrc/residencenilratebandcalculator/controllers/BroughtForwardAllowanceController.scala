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
    case Some(cacheMap) => cacheMap.data(Constants.dateOfDeathId).toString()
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
      microserviceValues.map { res => {
        val nilRateBand = res._1.json.toString()
        val cacheMap = res._2
        Ok(view(cacheMap.getEntry(controllerId).map(value => form().fill(value)), navigator.lastPage(controllerId).toString(), nilRateBand))
      }
      } recover {
        case n: NoCacheMapException => ???
        case r: RuntimeException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      }
    }
  }

  //  def onSubmit(implicit wts: Writes[Int]) = Action.async { implicit request => {
  //
  //    val result = for {
  //      dateOfDeath <- getDateOfDeath
  //      nilRateValueJson <- rnrbConnector.getNilRateBand(dateOfDeath)
  //    } yield nilRateValueJson
  //
  //    sessionConnector.fetch().flatMap {
  //      case None => Future.successful(Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad()))
  //      case Some(cacheMap) => {
  //        val boundForm = form().bindFromRequest()
  //        boundForm.fold(
  //          (formWithErrors: Form[Int]) => {
  //            result.map { res => {
  //              val nilRateBand = res.json.toString()
  //              BadRequest(view(Some(formWithErrors), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url, nilRateBand))}
  //            } recover {
  //              case _ => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
  //            }
  //          },
  //          (value) => validate(value).flatMap {
  //            case Some(error) => {
  //              result.map { res =>
  //                val nilRateBand = res.json.toString()
  //                BadRequest(view(Some(form().fill(value).withError(error)), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url, nilRateBand))
  //              } recover {
  //                case _ => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
  //              }
  //            }
  //            case None =>
  //              sessionConnector.cache[Int](controllerId, value).map(cacheMap =>
  //                Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
  //          }
  //        )
  //      }
  //    }
  //  }
  //  }

  def onSubmit(implicit wts: Writes[Int]) = Action.async {
    implicit request => {
      microserviceValues.flatMap {
        res => {
          val boundForm = form().bindFromRequest()
          val nilRateBand = res._1.json.toString()
          val cacheMap = res._2
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
          case n: NoCacheMapException => ???
          case r: RuntimeException => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
        }
      }
    }
  }

  def validate(value: Int)(implicit hc: HeaderCarrier): Future[Option[FormError]] = Future.successful(None)
  }
