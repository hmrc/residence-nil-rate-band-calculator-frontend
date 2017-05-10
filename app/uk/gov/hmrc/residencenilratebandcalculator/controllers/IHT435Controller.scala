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

import play.Logger
import play.api.Environment
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.utils.PDFHelper

@Singleton
class IHT435Controller @Inject()(val appConfig: FrontendAppConfig,
                                 val env: Environment,
                                 val messagesApi: MessagesApi,
                                 val sessionConnector: SessionConnector,
                                 val pdfHelper: PDFHelper) extends FrontendController with I18nSupport {
  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      case Some(cacheMap) =>
        def fail(msg: String) = {
          Logger.error(msg)
          throw new RuntimeException(msg)
        }
        env.resourceAsStream("resource/IHT435.pdf").map ( is =>
            Ok(pdfHelper.generatePDF(is, cacheMap).toByteArray).as("application/pdf")
        ).fold(fail("Unable to locate PDF resource"))(identity)
    }
  }
}
