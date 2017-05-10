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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import java.io.{ByteArrayOutputStream, InputStream}
import javax.inject.Singleton

import org.apache.pdfbox.pdmodel.{PDDocument, PDDocumentInformation}
import play.api.i18n.Messages
import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers

@Singleton
class PDFHelper {
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
    Constants.partOfEstatePassingToDirectDescendantsId -> Seq("IHT435_05"),
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
    Constants.assetsPassingToDirectDescendantsId -> Seq("IHT435_22"),
    Constants.grossingUpOnEstateAssetsId -> Seq("IHT435_23"),
    Constants.valueOfAssetsPassingId -> Seq("IHT435_24"),
    Constants.transferAvailableWhenPropertyChangedId -> Seq("IHT435_26"),
    Constants.valueAvailableWhenPropertyChangedId -> Seq("IHT435_27"),
    Constants.thresholdCalculationResultId -> Seq("IHT435_28")
  )

  private def setupPDFDocument(pdf: PDDocument) = {
    val pdDocumentInformation: PDDocumentInformation = pdf.getDocumentInformation
    pdDocumentInformation.setTitle(Messages("threshold_calculation_result.pdf.title"))
    pdf.setDocumentInformation(pdDocumentInformation)
  }

  private def booleanValueForPDF(b: Boolean) = if (b) "Yes" else "No"

  private def jsValueToString(jsVal: JsValue): String = {
    jsVal match {
      case b: JsBoolean => booleanValueForPDF(b.value)
      case s: JsString => s.toString
      case _ => jsVal.toString
    }
  }

  private def getValueForPDF(jsVal: String, cacheId: String): String = {
    val dateCacheIds = Set(Constants.dateOfDeathId, Constants.datePropertyWasChangedId)
    val decimalCacheIds = Set(Constants.percentagePassedToDirectDescendantsId)
    jsVal match {
      case s if dateCacheIds.contains(cacheId) => Transformers.transformDateFormat(s)
      case s if decimalCacheIds.contains(cacheId) => Transformers.transformDecimalFormat(s)
      case s => s
    }
  }

  def generatePDF(is: InputStream, cacheMap: CacheMap): ByteArrayOutputStream = {
    val pdf = PDDocument.load(is)
    setupPDFDocument(pdf)
    val baos = new ByteArrayOutputStream()
    try {
      val form = pdf.getDocumentCatalog.getAcroForm

      def ua = new UserAnswers(cacheMap)

      def storeValuesInPDF(fieldNames: Seq[String], valueForPDF: String) = {
        val retrieveValueToStore: (String, Int) => String =
          if (fieldNames.size == 1) retrieveValueToStoreFor1Field else retrieveValueToStoreForMoreThan1Field
        fieldNames.indices foreach { i =>
          form.getField(fieldNames(i)).setValue(retrieveValueToStore(valueForPDF, i))
        }
      }

      cacheMapIdToFieldName foreach {
        case (cacheId, fieldNames) =>
          val optionalJsVal = cacheMap.data.get(cacheId)
          (optionalJsVal, cacheId) match {
            case (_, Constants.percentagePassedToDirectDescendantsId) =>
              storeValuesInPDF(fieldNames, getValueForPDF(ua.getPercentagePassedToDirectDescendants.toString, cacheId))
            case (_, Constants.transferAvailableWhenPropertyChangedId) =>
              ua.isTransferAvailableWhenPropertyChanged.foreach { isAvailable =>
                storeValuesInPDF(fieldNames, getValueForPDF(booleanValueForPDF(isAvailable), cacheId))
              }
            case (Some(jsVal), _) => storeValuesInPDF(fieldNames, getValueForPDF(jsValueToString(jsVal), cacheId))
            case _ =>
          }
      }
      pdf.save(baos)
    } finally {
      pdf.close()
      is.close()
    }
    baos
  }
}
