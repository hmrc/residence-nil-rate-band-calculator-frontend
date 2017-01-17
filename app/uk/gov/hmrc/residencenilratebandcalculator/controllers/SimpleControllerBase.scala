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


import play.api.data.{Form, FormError}
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Reads, Writes}
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.cache.client.{CacheMap, CachingException}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait SimpleControllerBase[A] extends FrontendController with I18nSupport {
  val cleanoutMap: Map[String, Seq[String]] = Map(
    Constants.anyAssetsPassingToDirectDescendantsId -> Seq(Constants.estateHasPropertyId, Constants.chargeableTransferAmountId)
  )

  val appConfig: FrontendAppConfig

  def sessionConnector: SessionConnector

  val controllerId: String

  def form: () => Form[A]

  def view(form: Option[Form[A]])(implicit request: Request[_]): HtmlFormat.Appendable

  val navigator: Navigator

  private def storeInSession(value: A)(implicit wts: Writes[A], hc: HeaderCarrier) = sessionConnector.cache[A](controllerId, value).map { cacheMap =>
    Redirect(navigator.nextPage(controllerId)(cacheMap))
  }


  def eraseThenStore(ids: Seq[String], value: A)(implicit wts: Writes[A], hc: HeaderCarrier): Future[Result] = {
    val cache: Future[Option[CacheMap]] = sessionConnector.fetch()
    cache.flatMap { optionalCache =>
      optionalCache.fold(Future.successful(BadRequest("cache not available"))) { cache =>
        val newMap = cache.data.filterKeys(s => ids.contains(s))
        Await.ready(sessionConnector.remove(), Duration.Inf)
        try {
          Future.sequence(newMap.map { zz => sessionConnector.cache(zz._1, zz._2) }).flatMap { ic => storeInSession(value) }
        } catch {
          case ce: CachingException => Future.successful(BadRequest("cache exception: " + ce))
        }
      }
    }
  }

  private def maybeCleanOutSessionThenStore(value: A)(implicit wts: Writes[A], hc: HeaderCarrier): Future[Result] = {
    // Decide here if redoing a value at controller id requires other id values to be replaced
    // If it does, clear out the other values then store, if it does not then simply store

    cleanoutMap.get(controllerId).fold(storeInSession(value)) { ids =>
      eraseThenStore(ids, value)
    }
  }

  private def store(value: A)(implicit rds: Reads[A], wts: Writes[A], hc: HeaderCarrier) = {
    sessionConnector.fetchAndGetEntry[A](controllerId).flatMap {
      case Some(_) => maybeCleanOutSessionThenStore(value)
      case None => storeInSession(value)
    }
  }

  def onPageLoad(implicit rds: Reads[A]) = Action.async { implicit request =>
    sessionConnector.fetchAndGetEntry[A](controllerId).map(
      cachedValue => {
        Ok(view(cachedValue.map(value => form().fill(value))))
      })
  }

  def onSubmit(implicit rds: Reads[A], wts: Writes[A]) = Action.async { implicit request =>
    val boundForm = form().bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[A]) => Future.successful(BadRequest(view(Some(formWithErrors)))),
      (value) => validate(value).flatMap {
        case Some(error) => Future.successful(BadRequest(view(Some(form().fill(value).withError(error)))))
        case None => store(value)
      })
  }

  def validate(value: A)(implicit hc: HeaderCarrier): Future[Option[FormError]] = Future.successful(None)
}
