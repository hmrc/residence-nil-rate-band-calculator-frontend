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

import java.util.UUID
import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.GrossEstateValueForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value

import scala.concurrent.Future

class GrossEstateValueController @Inject()(appConfig: FrontendAppConfig, val messagesApi: MessagesApi, sessionConnector: SessionConnector)
  extends FrontendController with I18nSupport {

  // TODO: This action should be replaced by a landing page in the correct controller, and the route in app.routes updated accordingly
  val firstTime = Action.async { implicit request =>
    Future.successful(Ok(gross_estate_value(appConfig))
      .withSession(request.session + (SessionKeys.sessionId -> s"session-${UUID.randomUUID}")))
  }

  val onPageLoad = Action.async { implicit request =>
    sessionConnector.fetchAndGetEntry[Int]("GrossEstateValue").map(
      cachedValue => {
        val form = cachedValue.map(value => GrossEstateValueForm().fill(value))
        Ok(gross_estate_value(appConfig, form))
      })
    }

  val onSubmit = Action.async { implicit request =>
    val boundForm = GrossEstateValueForm().bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[Int]) => Future.successful(BadRequest(gross_estate_value(appConfig, Some(formWithErrors)))),
      (value) => sessionConnector.cache[Int]("GrossEstateValue", value).map(_ => Redirect(""))
    )
  }
}
