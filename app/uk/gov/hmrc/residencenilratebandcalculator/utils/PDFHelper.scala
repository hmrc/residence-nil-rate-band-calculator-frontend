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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import java.io.ByteArrayOutputStream

import javax.inject.{Inject, Singleton}
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.{PDDocument, PDDocumentInformation}
import play.api.Environment
import play.api.i18n.Lang
import play.api.libs.json.{JsBoolean, JsString, JsValue}
import play.api.mvc.{DefaultMessagesControllerComponents, MessagesControllerComponents}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap

@Singleton
class PDFHelperImpl @Inject() (val env: Environment, val cc: DefaultMessagesControllerComponents) extends PDFHelper

trait PDFHelper {
  val env: Environment
  val cc: MessagesControllerComponents

  private val retrieveValueToStoreFor1Field: (String, Int) => String         = (v, _) => v
  private val retrieveValueToStoreForMoreThan1Field: (String, Int) => String = (v, i) => v.charAt(i).toString
  private val cacheMapIdToEnglishYesNo: String => (String, String)           = _ => ("Yes", "No")
  private val welshYesNo1                                                    = ("Oes", "Nac oes")
  private val welshYesNo2                                                    = ("Ydw", "Nac ydw")
  private val welshYesNo3                                                    = ("Ydy", "Nac ydy")
  private val welshYesNo4                                                    = ("Byddai", "Na fyddai")

  private val cacheMapIdToWelshYesNo: String => (String, String) = {
    case Constants.propertyInEstateId                     => welshYesNo3
    case Constants.transferAnyUnusedThresholdId           => welshYesNo2
    case Constants.transferAvailableWhenPropertyChangedId => welshYesNo4
    case Constants.claimDownsizingThresholdId             => welshYesNo2
    case Constants.grossingUpOnEstateAssetsId             => welshYesNo3
    case Constants.grossingUpOnEstatePropertyId           => welshYesNo3
    case _                                                => welshYesNo1
  }

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
    Constants.valueOfEstateId                          -> Seq("IHT435_06"),
    Constants.chargeableEstateValueId                  -> Seq("IHT435_07"),
    Constants.propertyInEstateId                       -> Seq("IHT435_08"),
    Constants.propertyValueId                          -> Seq("IHT435_10"),
    Constants.percentagePassedToDirectDescendantsId -> Seq(
      "IHT435_10_01",
      "IHT435_10_02",
      "IHT435_10_03",
      "IHT435_10_04",
      "IHT435_10_05",
      "IHT435_10_06",
      "IHT435_10_07"
    ),
    Constants.exemptionsAndReliefClaimedId       -> Seq("IHT435_12"),
    Constants.grossingUpOnEstatePropertyId       -> Seq("IHT435_13"),
    Constants.chargeablePropertyValueId          -> Seq("IHT435_14"),
    Constants.chargeableInheritedPropertyValueId -> Seq("IHT435_15"),
    Constants.transferAnyUnusedThresholdId       -> Seq("IHT435_16"),
    Constants.valueBeingTransferredId            -> Seq("IHT435_17"),
    Constants.claimDownsizingThresholdId         -> Seq("IHT435_18"),
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
    Constants.valueOfChangedPropertyId               -> Seq("IHT435_21"),
    Constants.assetsPassingToDirectDescendantsId     -> Seq("IHT435_22"),
    Constants.grossingUpOnEstateAssetsId             -> Seq("IHT435_23"),
    Constants.valueOfAssetsPassingId                 -> Seq("IHT435_24"),
    Constants.transferAvailableWhenPropertyChangedId -> Seq("IHT435_26"),
    Constants.valueAvailableWhenPropertyChangedId    -> Seq("IHT435_27"),
    Constants.thresholdCalculationResultId           -> Seq("IHT435_28")
  )

  private def setupPDFDocument(pdf: PDDocument)(implicit lang: Lang): Unit = {
    val pdDocumentInformation: PDDocumentInformation = pdf.getDocumentInformation
    pdDocumentInformation.setTitle(cc.messagesApi("threshold_calculation_result.pdf.title"))
    pdf.setDocumentInformation(pdDocumentInformation)
  }

  private def booleanValueForPDF(b: Boolean, generateWelshValue: Boolean, cacheId: String) = {
    val yesNos = if (generateWelshValue) {
      cacheMapIdToWelshYesNo(cacheId)
    } else {
      cacheMapIdToEnglishYesNo(cacheId)
    }
    if (b) {
      yesNos._1
    } else {
      yesNos._2
    }
  }

  private def jsValueToString(jsVal: JsValue, generateWelshValue: Boolean, cacheId: String): String =
    jsVal match {
      case b: JsBoolean => booleanValueForPDF(b.value, generateWelshValue, cacheId)
      case s: JsString  => s.toString
      case _            => jsVal.toString
    }

  private def formatValueForPDF(jsVal: String, cacheId: String): String = {
    val dateCacheIds    = Set(Constants.dateOfDeathId, Constants.datePropertyWasChangedId)
    val decimalCacheIds = Set(Constants.percentagePassedToDirectDescendantsId)
    jsVal match {
      case s if dateCacheIds.contains(cacheId)    => Transformers.transformDateFormat(s)
      case s if decimalCacheIds.contains(cacheId) => Transformers.transformDecimalFormat(s)
      case s                                      => s
    }
  }

  private def storeFormattedValueInPDFFields(
      fieldNames: Seq[String],
      valueFormattedForPDF: String,
      form: PDAcroForm
  ) = {
    val retrieveValueToStore: (String, Int) => String =
      if (fieldNames.size == 1) retrieveValueToStoreFor1Field else retrieveValueToStoreForMoreThan1Field
    fieldNames.indices.foreach { i =>
      form.getField(fieldNames(i)).setValue(retrieveValueToStore(valueFormattedForPDF, i))
    }
  }

  private def storeValuesInPDF(cacheMap: CacheMap, form: PDAcroForm, storeWelshValues: Boolean) = {
    val ua = new UserAnswers(cacheMap)
    cacheMapIdToFieldName.foreach { case (cacheId, fieldNames) =>
      val optionalJsVal = cacheMap.data.get(cacheId)
      val valueFormattedForPDF: Option[String] = (optionalJsVal, cacheId) match {
        case (_, Constants.percentagePassedToDirectDescendantsId) =>
          Some(formatValueForPDF(ua.getPercentagePassedToDirectDescendants.toString, cacheId))
        case (_, Constants.transferAvailableWhenPropertyChangedId) =>
          ua.isTransferAvailableWhenPropertyChanged.map(isAvailable =>
            formatValueForPDF(booleanValueForPDF(isAvailable, storeWelshValues, cacheId), cacheId)
          )
        case (Some(jsVal), _) => Some(formatValueForPDF(jsValueToString(jsVal, storeWelshValues, cacheId), cacheId))
        case _                => None
      }
      valueFormattedForPDF.foreach(value => storeFormattedValueInPDFFields(fieldNames, value, form))
    }
  }

  def generatePDF(cacheMap: CacheMap, generateWelshPDF: Boolean)(implicit lang: Lang): Option[ByteArrayOutputStream] = {
    val resourceName = if (generateWelshPDF) {
      "IHT435Cymraeg.pdf"
    } else {
      "IHT435.pdf"
    }
    env.resourceAsStream(s"resource/$resourceName").map { is =>
      var pdd: Option[PDDocument] = None
      val baos                    = new ByteArrayOutputStream()
      try {
        pdd = Option(PDDocument.load(is))
        pdd.foreach { pdf =>
          setupPDFDocument(pdf)
          storeValuesInPDF(cacheMap, pdf.getDocumentCatalog.getAcroForm, generateWelshPDF)
          pdf.save(baos)
        }
      } finally {
        pdd.foreach(pdf => pdf.close())
        is.close()
      }
      baos
    }
  }

}
