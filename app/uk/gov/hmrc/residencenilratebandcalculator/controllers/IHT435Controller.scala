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

import java.io.{ByteArrayOutputStream, File, FileOutputStream, OutputStream}
import javassist.bytecode.ByteArray
import javax.inject.{Inject, Singleton}

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.{PDCheckBox, PDField, PDRadioButton}
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers

import scala.concurrent.Future

@Singleton
class IHT435Controller @Inject()(val appConfig: FrontendAppConfig,
                                 val sessionConnector: SessionConnector) extends FrontendController {

  val fieldNameMap = Map[String, String](
    "IHT435_01" -> "IHT435_01",
    "IHT435_02" -> "IHT435_02",
    "IHT435_03_01" -> "IHT435_03_01",
    "IHT435_03_02" -> "IHT435_03_02",
    "IHT435_03_03" -> "IHT435_03_03",
    "IHT435_03_04" -> "IHT435_03_04",
    "IHT435_03_05" -> "IHT435_03_05",
    "IHT435_03_06" -> "IHT435_03_06",
    "IHT435_03_07" -> "IHT435_03_07",
    "IHT435_03_08" -> "IHT435_03_08",
    "IHT435_04" -> "IHT435_04",
    "IHT435_05" -> "Yes",
    "IHT435_06" -> "IHT435_06",
    "IHT435_07" -> "IHT435_07",
    "IHT435_08" -> "No",
    "IHT435_09_01" -> "IHT435_09_01",
    "IHT435_09_02" -> "IHT435_09_02",
    "IHT435_09_03" -> "IHT435_09_03",
    "IHT435_09_04" -> "IHT435_09_04",
    "IHT435_10" -> "IHT435_10",
    "IHT435_10_01" -> "IHT435_10_01",
    "IHT435_10_02" -> "IHT435_10_02",
    "IHT435_10_03" -> "IHT435_10_03",
    "IHT435_10_04" -> "IHT435_10_04",
    "IHT435_10_05" -> "IHT435_10_05",
    "IHT435_10_06" -> "IHT435_10_06",
    "IHT435_10_07" -> "IHT435_10_07",
    "IHT435_11_01" -> "IHT435_11_01",
    "IHT435_11_02" -> "IHT435_11_02",
    "IHT435_11_03" -> "IHT435_11_03",
    "IHT435_11_04" -> "IHT435_11_04",
    "IHT435_11_05" -> "IHT435_11_05",
    "IHT435_11_06" -> "IHT435_11_06",
    "IHT435_12" -> "Yes",
    "IHT435_13" -> "No",
    "IHT435_14" -> "IHT435_14",
    "IHT435_15" -> "IHT435_15",
    "IHT435_16" -> "Yes",
    "IHT435_17" -> "IHT435_17",
    "IHT435_18" -> "No",
    "IHT435_19_01" -> "IHT435_19_01",
    "IHT435_19_02" -> "IHT435_19_02",
    "IHT435_19_03" -> "IHT435_19_03",
    "IHT435_19_04" -> "IHT435_19_04",
    "IHT435_20_01" -> "IHT435_20_01",
    "IHT435_20_02" -> "IHT435_20_02",
    "IHT435_20_03" -> "IHT435_20_03",
    "IHT435_20_04" -> "IHT435_20_04",
    "IHT435_20_05" -> "IHT435_20_05",
    "IHT435_20_06" -> "IHT435_20_06",
    "IHT435_20_07" -> "IHT435_20_07",
    "IHT435_20_08" -> "IHT435_20_08",
    "IHT435_21" -> "IHT435_21",
    "IHT435_22" -> "Yes",
    "IHT435_23" -> "No",
    "IHT435_24" -> "IHT435_24",
    "IHT435_25_01" -> "IHT435_25_01",
    "IHT435_25_02" -> "IHT435_25_02",
    "IHT435_25_03" -> "IHT435_25_03",
    "IHT435_25_04" -> "IHT435_25_04",
    "IHT435_25_05" -> "IHT435_25_05",
    "IHT435_25_06" -> "IHT435_25_06",
    "IHT435_26" -> "Yes",
    "IHT435_27" -> "IHT435_27",
    "IHT435_28" -> "IHT435_28"
  )

  private def generatePDF() = {
    val pdf = PDDocument.load(new File("conf/resource/IHT435.pdf"))
    val form = pdf.getDocumentCatalog.getAcroForm

    for (f <- fieldNameMap) {
      val field = form.getField(f._1)
      field.setValue(f._2)
    }

//    val cb5 = form.getField("IHT435_05")
//
//    //cb5.asInstanceOf[PDCheckBox].check()
//    cb5.setValue("Yes")
//
//    val cb8: PDField = form.getField("IHT435_08")
//    cb8.asInstanceOf[PDCheckBox].check()

    pdf.setAllSecurityToBeRemoved(true)
    val baos = new ByteArrayOutputStream()
    pdf.save(baos)
    pdf.close
//"/Users/andy/Downloads/wibble.pdf"
    val outputStream = new FileOutputStream("/home/grant/Downloads/wibble.pdf")
    baos.writeTo(outputStream);

    baos
  }

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      case Some(cacheMap) =>
        Ok(generatePDF().toByteArray).as("application/pdf")
    }
  }
}
