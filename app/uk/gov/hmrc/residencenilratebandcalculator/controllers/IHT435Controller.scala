/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.{Inject, Provider, Singleton}
import play.Logger
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, DefaultMessagesControllerComponents}
import play.api.{Application, Environment}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.utils.PDFHelperImpl

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class IHT435Controller @Inject()(val env: Environment,
                                 val cc: DefaultMessagesControllerComponents,
                                 val sessionConnector: SessionConnector,
                                 val pdfHelper: PDFHelperImpl) extends FrontendController(cc) with I18nSupport {
  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      case Some(cacheMap) =>
        def fail(msg: String) = {
          Logger.error(msg)
          throw new RuntimeException(msg)
        }

        implicit val currentLang: Lang = request.lang
        val generateWelshPDF = messagesApi.preferred(request).lang.code == "cy"

        pdfHelper.generatePDF(cacheMap = cacheMap, generateWelshPDF = generateWelshPDF).map(baos => {
          val result = Ok(baos.toByteArray).as("application/pdf")
          baos.close()
          result
        }).fold(fail("Unable to locate PDF template resource"))(identity)
    }
  }
}
