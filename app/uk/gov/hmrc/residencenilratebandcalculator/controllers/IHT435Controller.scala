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
import play.api.{Environment, Logging}
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, DefaultMessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.utils.PDFHelperImpl

import scala.concurrent.ExecutionContext

@Singleton
class IHT435Controller @Inject() (
    val env: Environment,
    val cc: DefaultMessagesControllerComponents,
    val sessionConnector: SessionConnector,
    val pdfHelper: PDFHelperImpl
)(implicit ex: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None =>
        Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
      case Some(cacheMap) =>
        def fail(msg: String) = {
          logger.error(msg)
          throw new RuntimeException(msg)
        }

        implicit val currentLang: Lang = request.lang
        val generateWelshPDF           = messagesApi.preferred(request).lang.code == "cy"

        pdfHelper
          .generatePDF(cacheMap = cacheMap, generateWelshPDF = generateWelshPDF)
          .map { baos =>
            val result = Ok(baos.toByteArray).as("application/pdf")
            baos.close()
            result
          }
          .fold(fail("Unable to locate PDF template resource"))(identity)
    }
  }

}
