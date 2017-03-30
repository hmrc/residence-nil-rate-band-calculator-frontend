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
import javax.inject.{Inject, Singleton}

import org.apache.pdfbox.pdmodel.{PDDocument, PDDocumentInformation}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.utils.Transformers
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}


@Singleton
class IHT435Controller @Inject()(val appConfig: FrontendAppConfig,
                                 val messagesApi: MessagesApi,
                                 val sessionConnector: SessionConnector) extends FrontendController  with I18nSupport {

  private val retrieveValueToStoreFor1Field: (String, Int) => String = (v, _) => v
  private val retrieveValueToStoreForMoreThan1Field: (String, Int) => String = (v, i) => v.charAt(i).toString

  private val cacheMapIdToFieldName = Map[String, Seq[String]](
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
    Constants.assetsPassingToDirectDescendantsId -> Seq("IHT435_05"),
    Constants.valueOfEstateId -> Seq("IHT435_06"),
    Constants.chargeableEstateValueId -> Seq("IHT435_07"),
    Constants.propertyInEstateId -> Seq("IHT435_08"),
    Constants.propertyValueId -> Seq("IHT435_10"),
    Constants.percentagePassedToDirectDescendantsId -> Seq(
      "IHT435_10_01",
      "IHT435_10_02",
      "IHT435_10_03",
      "IHT435_10_04",
      "IHT435_10_05",
      "IHT435_10_06",
      "IHT435_10_07"
    ),
    Constants.exemptionsAndReliefClaimedId -> Seq("IHT435_12"),
    Constants.grossingUpOnEstatePropertyId -> Seq("IHT435_13"),
    Constants.chargeablePropertyValueId -> Seq("IHT435_14"),
    Constants.chargeableInheritedPropertyValueId -> Seq("IHT435_15"),
    Constants.transferAnyUnusedThresholdId -> Seq("IHT435_16"),
    Constants.valueBeingTransferredId -> Seq("IHT435_17"),
    Constants.claimDownsizingThresholdId -> Seq("IHT435_18"),
    Constants.datePropertyWasChangedId -> Seq(
      "IHT435_20_01",
      "IHT435_20_02",
      "IHT435_20_03",
      "IHT435_20_04",
      "IHT435_20_05",
      "IHT435_20_06",
      "IHT435_20_07",
      "IHT435_20_08"
    ),
    Constants.valueOfChangedPropertyId -> Seq("IHT435_21"),
    Constants.partOfEstatePassingToDirectDescendantsId -> Seq("IHT435_22"),
    Constants.grossingUpOnEstateAssetsId -> Seq("IHT435_23"),
    Constants.valueOfAssetsPassingId -> Seq("IHT435_24"),
    Constants.transferAvailableWhenPropertyChangedId -> Seq("IHT435_26"),
    Constants.valueAvailableWhenPropertyChangedId -> Seq("IHT435_27"),
    Constants.thresholdCalculationResultId -> Seq("IHT435_28")
  )

  private def setupPDFDocument(pdf:PDDocument) = {
    val pdDocumentInformation: PDDocumentInformation = pdf.getDocumentInformation
    pdDocumentInformation.setTitle(Messages("threshold_calculation_result.pdf.title"))
    pdf.setDocumentInformation(pdDocumentInformation)
  }

  private def getValueForPDF(jsVal: JsValue, cacheId: String): String = {
    val dateCacheIds = Set(Constants.dateOfDeathId, Constants.datePropertyWasChangedId)
    val decimalCacheIds = Set(Constants.percentagePassedToDirectDescendantsId)
    jsVal match {
      case n: JsNumber => n.toString
      case b: JsBoolean => if (b.value) "Yes" else "No"
      case s: JsString if dateCacheIds.contains(cacheId) =>
        Transformers.transformDateFormat(s.toString)
      case s: JsString if decimalCacheIds.contains(cacheId) =>
        (" " * 7 + Transformers.stripOffQuotesIfPresent(s.toString).replace(".", "")) takeRight 7
      case s: JsString => s.toString
      case _ => ""
    }
  }

  private def generatePDF(cacheMap: CacheMap) = {
    val pdf = PDDocument.load(new File("conf/resource/IHT435.pdf"))
    setupPDFDocument(pdf)
    val baos = new ByteArrayOutputStream()
    try {
      val form = pdf.getDocumentCatalog.getAcroForm

      cacheMapIdToFieldName foreach {
        case (cacheId, fieldNames) =>
          val optionalJsVal = cacheMap.data.get(cacheId)
          optionalJsVal match {
            case Some(jsVal) =>
              val valueForPDF = getValueForPDF(jsVal, cacheId)
              val retrieveValueToStore: (String, Int) => String =
                if (fieldNames.size == 1) retrieveValueToStoreFor1Field else retrieveValueToStoreForMoreThan1Field
              fieldNames.indices foreach { i =>
                form.getField(fieldNames(i)).setValue(retrieveValueToStore(valueForPDF, i))
              }
            case None =>
          }
      }

      pdf.save(baos)
    } finally {
      pdf.close()
    }
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
