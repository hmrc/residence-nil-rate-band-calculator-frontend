/*
 * Copyright 2016 HM Revenue & Customs
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

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm
import uk.gov.hmrc.residencenilratebandcalculator.models.Date

import scala.concurrent.Future

trait DateControllerBase extends FrontendController with I18nSupport {

  val appConfig: FrontendAppConfig
  def sessionConnector: SessionConnector
  val sessionCacheKey: String
  def view(form: Option[Form[Date]])(implicit request: Request[_]): HtmlFormat.Appendable

  val onPageLoad = Action.async { implicit request =>
    sessionConnector.fetchAndGetEntry[LocalDate](sessionCacheKey).map(
      cachedValue => {
        Ok(view(cachedValue.map(value => DateForm().fill(Date(value)))))
      })
  }

  val onSubmit = Action.async { implicit request =>
    val boundForm = DateForm().bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[Date]) => Future.successful(BadRequest(view(Some(formWithErrors)))),
      (value) => sessionConnector.cache[LocalDate](sessionCacheKey, value.toLocalDate).map(_ => Redirect(""))
    )
  }
}
