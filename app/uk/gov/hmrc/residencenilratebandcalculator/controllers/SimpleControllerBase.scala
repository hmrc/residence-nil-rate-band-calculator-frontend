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
import play.api.libs.json.{Reads, Writes}
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

import scala.concurrent.Future

trait SimpleControllerBase[A] extends FrontendController with I18nSupport {

  val appConfig: FrontendAppConfig

  def sessionConnector: SessionConnector

  val controllerId: String

  def form: () => Form[A]

  def view(form: Option[Form[A]])(implicit request: Request[_]): HtmlFormat.Appendable

  val navigator: Navigator

  def onPageLoad(implicit rds: Reads[A]) = Action.async { implicit request =>
    sessionConnector.fetchAndGetEntry[A](controllerId).map(
      cachedValue => {
        Ok(view(cachedValue.map(value => form().fill(value))))
      })
  }

  def onSubmit(implicit wts: Writes[A]) = Action.async { implicit request =>
    val boundForm = form().bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[A]) => Future.successful(BadRequest(view(Some(formWithErrors)))),
      (value) => validate(value).flatMap {
        case Some(error) => Future.successful(BadRequest(view(Some(form().fill(value).withError(error)))))
        case None => sessionConnector.cache[A](controllerId, value).map(cacheMap =>
          Redirect(navigator.nextPage(controllerId)(cacheMap)))
      })
  }

  def validate(value: A)(implicit hc: HeaderCarrier): Future[Option[FormError]] = Future.successful(None)
}
