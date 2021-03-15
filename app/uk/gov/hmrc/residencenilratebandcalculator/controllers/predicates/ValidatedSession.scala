/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates

import javax.inject.Inject
import play.api.Logger.logger
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.Future
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class ValidatedSession @Inject()(mcc: MessagesControllerComponents) extends FrontendController(mcc) with I18nSupport{

  private type PlayRequest = Request[AnyContent] => Result
  private type AsyncRequest = Request[AnyContent] => Future[Result]

  def async(action: AsyncRequest): Action[AnyContent] = {
    Action.async { implicit request =>
      if(request.session.get(SessionKeys.sessionId).isEmpty) {
        logger.warn("No session ID found; timing out")
        Future.successful(Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad()))
      } else {
        action(request)
      }
    }
  }
}