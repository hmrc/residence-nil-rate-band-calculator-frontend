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

import java.io.{ByteArrayOutputStream, File, FileOutputStream}
import javax.inject.{Inject, Singleton}

import org.apache.pdfbox.pdmodel.PDDocument
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.utils.Transformers
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

@Singleton
class IHT435Controller @Inject()(val appConfig: FrontendAppConfig,
                                 val sessionConnector: SessionConnector) extends FrontendController {


  private val retrieveValueToStoreFor1Field: (String, Int) => String = (v, _) => v
  private val retrieveValueToStoreForMoreThan1Field: (String, Int) => String = (v, i) => v.charAt(i).toString

  private val cacheMapIdToFieldName = Map[String, Seq[String]](
//    Constants.bla -> Seq("IHT435_01"),
//    Constants.bla -> Seq("IHT435_02"),
    Constants.dateOfDeathId -> Seq(
      "IHT435_03_01",
      "IHT435_03_02",
      "IHT435_03_03",
      "IHT435_03_04",
      "IHT435_03_05",
      "IHT435_03_06",
      "IHT435_03_07",
      "IHT435_03_08"
    ),
//    Constants.bla -> Seq("IHT435_03_04"),
    Constants.assetsPassingToDirectDescendantsId -> Seq("IHT435_05"),
    Constants.valueOfEstateId -> Seq("IHT435_06"),
    Constants.chargeableEstateValueId -> Seq("IHT435_07"),
    Constants.propertyInEstateId -> Seq("IHT435_08")
//    Constants.bla -> Seq("IHT435_09_01"),
//    Constants.bla -> Seq("IHT435_09_02"),
//    Constants.bla -> Seq("IHT435_09_03"),
//    Constants.bla -> Seq("IHT435_09_04"),
//    Constants.bla -> Seq("IHT435_10"),
//    Constants.bla -> Seq("IHT435_10_01"),
//    Constants.bla -> Seq("IHT435_10_02"),
//    Constants.bla -> Seq("IHT435_10_03"),
//    Constants.bla -> Seq("IHT435_10_04"),
//    Constants.bla -> Seq("IHT435_10_05"),
//    Constants.bla -> Seq("IHT435_10_06"),
//    Constants.bla -> Seq("IHT435_10_07"),
//    Constants.bla -> Seq("IHT435_11_01"),
//    Constants.bla -> Seq("IHT435_11_02"),
//    Constants.bla -> Seq("IHT435_11_03"),
//    Constants.bla -> Seq("IHT435_11_04"),
//    Constants.bla -> Seq("IHT435_11_05"),
//    Constants.bla -> Seq("IHT435_11_06"),
//    Constants.bla -> Seq("IHT435_12"),
//    Constants.bla -> Seq("IHT435_13"),
//    Constants.bla -> Seq("IHT435_14"),
//    Constants.bla -> Seq("IHT435_15"),
//    Constants.bla -> Seq("IHT435_16"),
//    Constants.bla -> Seq("IHT435_17"),
//    Constants.bla -> Seq("IHT435_18"),
//    Constants.bla -> Seq("IHT435_19_01"),
//    Constants.bla -> Seq("IHT435_19_02"),
//    Constants.bla -> Seq("IHT435_19_03"),
//    Constants.bla -> Seq("IHT435_19_04"),
//    Constants.bla -> Seq(
//      "IHT435_20_01",
//      "IHT435_20_02",
//      "IHT435_20_03",
//      "IHT435_20_04",
//      "IHT435_20_05",
//      "IHT435_20_06",
//      "IHT435_20_07",
//      "IHT435_20_08"
//    ),
//    Constants.bla -> Seq("IHT435_21"),
//    Constants.bla -> Seq("IHT435_22"),
//    Constants.bla -> Seq("IHT435_23"),
//    Constants.bla -> Seq("IHT435_24"),
//    Constants.bla -> Seq("IHT435_25_01"),
//    Constants.bla -> Seq("IHT435_25_02"),
//    Constants.bla -> Seq("IHT435_25_03"),
//    Constants.bla -> Seq("IHT435_25_04"),
//    Constants.bla -> Seq("IHT435_25_05"),
//    Constants.bla -> Seq("IHT435_25_06"),
//    Constants.bla -> Seq("IHT435_26"),
//    Constants.bla -> Seq("IHT435_27"),
//    Constants.bla -> Seq("IHT435_28")
  )

  private def getValueForPDF(jsVal: JsValue, cacheId: String): String = {
    val dateCacheIds = Set(Constants.dateOfDeathId)
    jsVal match {
      case n: JsNumber => n.toString
      case b: JsBoolean => if (b.value) "Yes" else "No"
      case s: JsString if dateCacheIds.contains(cacheId) =>
        Transformers.transformDateFormat(s.toString)
      case s: JsString => s.toString
      case _ => ""
    }
  }

  private def generatePDF(cacheMap: CacheMap) = {
    val pdf = PDDocument.load(new File("conf/resource/IHT435.pdf"))
    val form = pdf.getDocumentCatalog.getAcroForm

    cacheMapIdToFieldName foreach {
      case (cacheId, fieldNames) =>
        val optionalJsVal = cacheMap.data.get(cacheId)
        optionalJsVal match {
          case Some(jsVal) =>
            val valueForPDF = getValueForPDF(jsVal, cacheId)
            val retrieveValueToStore: (String, Int) => String =
              if (fieldNames.size == 1) retrieveValueToStoreFor1Field else retrieveValueToStoreForMoreThan1Field
            var i = 0
            fieldNames.foreach { currField =>
              //println("\n&&&&&&&&&&&&&&&& SETTING FIELD " + currField + " TO " + storedValue)
              form.getField(currField).setValue(retrieveValueToStore(valueForPDF, i))
              i = i + 1
            }
          case None =>
        }
    }

    pdf.setAllSecurityToBeRemoved(true)
    val baos = new ByteArrayOutputStream()
    pdf.save(baos)
    pdf.close()
    //"/Users/andy/Downloads/blat.pdf"
    val outputStream = new FileOutputStream("/home/grant/Downloads/blat.pdf")
    baos.writeTo(outputStream)
    baos
  }

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      case Some(cacheMap) =>
        Ok(generatePDF(cacheMap).toByteArray).as("application/pdf")
    }
  }
}
