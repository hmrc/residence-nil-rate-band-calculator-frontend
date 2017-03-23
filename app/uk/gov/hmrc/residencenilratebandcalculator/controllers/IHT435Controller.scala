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

import java.io.{ByteArrayOutputStream, File}
import javassist.bytecode.ByteArray
import javax.inject.{Inject, Singleton}

import org.apache.pdfbox.pdmodel.PDDocument
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers

import scala.concurrent.Future

@Singleton
class IHT435Controller @Inject()(val appConfig: FrontendAppConfig,
                                 val sessionConnector: SessionConnector) extends FrontendController {

  def generatePDF() = {
    val pdf = PDDocument.load(new File("conf/resource/IHT435.pdf"))
    val form = pdf.getDocumentCatalog.getAcroForm
    val field = form.getField("IHT435_01")
    field.setValue("IHT435_01")

    pdf.setAllSecurityToBeRemoved(true)
    val baos = new ByteArrayOutputStream()
    pdf.save(baos)
    pdf.close

    baos
  }

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      case Some(cacheMap) => {
        //Ok(/*generatePDF*/).as("application/pdf")
        Ok("")
      }
    }
  }
}
