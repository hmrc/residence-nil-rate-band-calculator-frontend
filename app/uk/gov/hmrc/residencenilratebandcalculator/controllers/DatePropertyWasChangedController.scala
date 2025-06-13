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
import play.api.data.Form
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, AnyContent, DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.forms.DatePropertyWasChangedForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.{Date, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_property_was_changed
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DatePropertyWasChangedController @Inject() (
    cc: DefaultMessagesControllerComponents,
    val sessionConnector: SessionConnector,
    val navigator: Navigator,
    validatedSession: ValidatedSession,
    datePropertyWasChangedView: date_property_was_changed
)(implicit val ec: ExecutionContext)
    extends FrontendController(cc)
    with ControllerBase[Date] {

  val controllerId: String = Constants.datePropertyWasChangedId

  def view(form: Form[Date])(implicit request: Request[?]) =
    datePropertyWasChangedView(form)

  def onPageLoad(implicit rds: Reads[Date]): Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None =>
        Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
      case Some(cacheMap) =>
        val datePropertyWasChanged = cacheMap.getEntry[Date](controllerId)
        Ok(
          view(datePropertyWasChanged.fold(datePropertyWasChangedForm)(value => datePropertyWasChangedForm.fill(value)))
        )
    }
  }

  def onSubmit(implicit wts: Writes[Date]): Action[AnyContent] = validatedSession.async { implicit request =>
    sessionConnector.fetch().flatMap {
      case None =>
        Future.successful(
          Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
        )
      case Some(_) =>
        val boundForm = datePropertyWasChangedForm.bindFromRequest()
        boundForm.fold(
          (formWithErrors: Form[Date]) => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            sessionConnector
              .cache[Date](controllerId, value)
              .map(cacheMap => Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
        )
    }
  }

}
