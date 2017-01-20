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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.forms.ExitQuestionnaireForm
import uk.gov.hmrc.residencenilratebandcalculator.models.ExitQuestionnaireEvent
import uk.gov.hmrc.residencenilratebandcalculator.views.html.exit_questionnaire
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, FrontendAuditConnector, Navigator}

import scala.concurrent.Future

@Singleton
class ExitQuestionnaireController @Inject()(val appConfig: FrontendAppConfig,
                                            val messagesApi: MessagesApi,
                                            val auditConnector: FrontendAuditConnector) extends FrontendController with I18nSupport {


  def onPageLoad = Action.async { implicit request =>
    Future.successful(Ok(exit_questionnaire(appConfig)))
  }

  def onSubmit = Action.async { implicit request =>
    val boundForm = ExitQuestionnaireForm().bindFromRequest()

    boundForm.fold(
      formWithErrors => Future.successful(BadRequest(exit_questionnaire(appConfig, Some(formWithErrors)))),
      value => {

        val questionnaireResult = auditConnector.sendEvent(new ExitQuestionnaireEvent(value.serviceDifficulty.getOrElse(""),
                                                                                      value.serviceFeel.getOrElse(""),
                                                                                      value.comments.getOrElse(""),
                                                                                      value.fullName.getOrElse(""),
                                                                                      value.email.getOrElse(""),
                                                                                      value.phoneNumber.getOrElse("")))
        questionnaireResult.onFailure {
          case e: Throwable => Logger.error(s"[ExitQuestionnaireController][post] ${e.getMessage}", e)
        }

        Future.successful(Redirect(routes.ThankYouController.onPageLoad()))
      }
    )
  }
}
