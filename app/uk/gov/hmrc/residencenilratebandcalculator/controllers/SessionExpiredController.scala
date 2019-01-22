/*
 * Copyright 2019 HM Revenue & Customs
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

import com.google.inject.Provider
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import play.api.{Application, Logger}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.views.html.session_expired

import scala.concurrent.Future

@Singleton
class SessionExpiredController @Inject()(val messagesApi: MessagesApi,
                                         val sessionConnector: SessionConnector,
                                         implicit val appConfig: FrontendAppConfig,
                                         implicit val applicationProvider: Provider[Application]
                                        ) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.removeAll.flatMap( isDropped => {
        Logger.debug(s"Drop of session connector cache return status: $isDropped")
        Future.successful(Ok(session_expired()))
      }
    )
  }
}
