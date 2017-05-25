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

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import play.api.Environment
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, Constants}

class PDFHelperSpec extends BaseSpec with WithFakeApplication {
  private val injector = fakeApplication.injector
  private val env = injector.instanceOf[Environment]
  private def messagesApi = injector.instanceOf[MessagesApi]
  private val cacheMapKey = "aa"
  private val noDigitsInDate = 8
  private val noDigitsInDecimal = 7
  private val cacheMapAllNonDecimalFields: CacheMap = new CacheMap(cacheMapKey, Map[String, JsValue](
    Constants.dateOfDeathId -> JsString("2017-5-12"),
    Constants.assetsPassingToDirectDescendantsId -> JsBoolean(true),
    Constants.valueOfEstateId -> JsNumber(500000),
    Constants.chargeableEstateValueId -> JsNumber(450000),
    Constants.propertyInEstateId -> JsBoolean(false),
    Constants.propertyValueId -> JsNumber(9948),
    Constants.exemptionsAndReliefClaimedId -> JsBoolean(true),
    Constants.grossingUpOnEstatePropertyId -> JsBoolean(false),
    Constants.chargeablePropertyValueId -> JsNumber(8893),
    Constants.chargeableInheritedPropertyValueId -> JsNumber(8894),
    Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
    Constants.valueBeingTransferredId -> JsNumber(88728),
    Constants.claimDownsizingThresholdId -> JsBoolean(false),
    Constants.datePropertyWasChangedId -> JsString("2017-5-13"),
    Constants.valueOfChangedPropertyId -> JsNumber(888),
    Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
    Constants.grossingUpOnEstateAssetsId -> JsBoolean(false),
    Constants.valueOfAssetsPassingId -> JsNumber(777),
    Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true),
    Constants.valueAvailableWhenPropertyChangedId -> JsNumber(3333),
    Constants.thresholdCalculationResultId -> JsNumber(229988)
  ))

  private def acroForm(filledCacheMap: CacheMap = cacheMapAllNonDecimalFields, generateWelshPDF: Boolean = false): PDAcroForm = {
    val pdfHelper = new PDFHelper(messagesApi, env)
    val optionPDAcroForm: Option[PDAcroForm] = pdfHelper.generatePDF(filledCacheMap, generateWelshPDF=generateWelshPDF).map { baos =>
      val pdfDoc = PDDocument.load(baos.toByteArray)
      baos.close()
      val acroForm = pdfDoc.getDocumentCatalog.getAcroForm
      pdfDoc.close()
      acroForm
    }
    optionPDAcroForm.fold[PDAcroForm](throw new RuntimeException("Unable to get stream"))(identity)
  }

  private def checkMultipleFieldValues(acroForm: PDAcroForm, baseFieldName: String, expectedDate: String, totalFields: Int) = {
    for (position <- 1 to totalFields) yield {
      val actualResult = acroForm.getField(baseFieldName + "_0" + position).getValueAsString
      val expectedResult = expectedDate.charAt(position - 1).toString
      assert(actualResult == expectedResult, s"(position $position)")
    }
  }

  private def describeTest(fieldName: String, isWelshTest: Boolean) = {
    val suffix = if(isWelshTest) {" in the welsh language"} else {""}
    s"generate from the cache the correct PDF value for $fieldName$suffix"
  }

  private def pdfField(fieldName: String, expectedValue: String, generateWelshPDF: Boolean) = {
    describeTest(fieldName, isWelshTest = generateWelshPDF) in {
      acroForm(generateWelshPDF=generateWelshPDF).getField(fieldName).getValueAsString shouldBe expectedValue
    }
  }

  private def pdfFieldTest(generateWelshPDF: Boolean) = {

    def yes(welsh: Option[String] = None) = welsh.fold("Yes")(identity)

    def no(welsh: Option[String] = None) = welsh.fold("No")(identity)

    describeTest("IHT435_03", isWelshTest = generateWelshPDF) in {
      checkMultipleFieldValues(acroForm(generateWelshPDF=generateWelshPDF), "IHT435_03", "12052017", noDigitsInDate)
    }

    behave like pdfField(fieldName = "IHT435_05", expectedValue = yes(if(generateWelshPDF) Some("Oes") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_06", expectedValue = "500000", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_07", expectedValue = "450000", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_08", expectedValue = no(if(generateWelshPDF) Some("Nac ydy") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_10", expectedValue = "9948", generateWelshPDF=generateWelshPDF)

    describeTest("IHT435_10_1 to 7: decimal number of less than maximum size", isWelshTest = generateWelshPDF) in {
      val cacheMap: CacheMap = new CacheMap(cacheMapKey, Map[String, JsValue](
        Constants.percentagePassedToDirectDescendantsId -> JsString("34.8899"),
        Constants.propertyInEstateId -> JsBoolean(true),
        Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.some)
      ))
      checkMultipleFieldValues(acroForm(filledCacheMap=cacheMap, generateWelshPDF=generateWelshPDF), "IHT435_10", " 348899", noDigitsInDecimal)
    }

    describeTest("IHT435_10_1 to 7: decimal number of maximum size", isWelshTest = generateWelshPDF) in {
      val cacheMap: CacheMap = new CacheMap(cacheMapKey, Map[String, JsValue](
        Constants.percentagePassedToDirectDescendantsId -> JsString("234.8899"),
        Constants.propertyInEstateId -> JsBoolean(true),
        Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.some)
      ))
      checkMultipleFieldValues(acroForm(filledCacheMap=cacheMap, generateWelshPDF=generateWelshPDF), "IHT435_10", "2348899", noDigitsInDecimal)
    }

    describeTest("IHT435_10_1 to 7: decimal number with less than max mantissa", isWelshTest = generateWelshPDF) in {
      val cacheMap: CacheMap = new CacheMap(cacheMapKey, Map[String, JsValue](
        Constants.percentagePassedToDirectDescendantsId -> JsString("234.889"),
        Constants.propertyInEstateId -> JsBoolean(true),
        Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.some)
      ))
      checkMultipleFieldValues(acroForm(filledCacheMap=cacheMap, generateWelshPDF=generateWelshPDF), "IHT435_10", "2348890", noDigitsInDecimal)
    }

    behave like pdfField(fieldName = "IHT435_12", expectedValue = yes(if(generateWelshPDF) Some("Oes") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_13", expectedValue = no(if(generateWelshPDF) Some("Nac ydy") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_14", expectedValue = "8893", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_15", expectedValue = "8894", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_16", expectedValue = yes(if(generateWelshPDF) Some("Ydw") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_17", expectedValue = "88728", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_18", expectedValue = no(if(generateWelshPDF) Some("Nac ydw") else None), generateWelshPDF=generateWelshPDF)

    describeTest("IHT435_20", isWelshTest = generateWelshPDF) in {
      checkMultipleFieldValues(acroForm(generateWelshPDF=generateWelshPDF), "IHT435_20", "13052017", noDigitsInDate)
    }

    behave like pdfField(fieldName = "IHT435_21", "888", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_22", yes(if(generateWelshPDF) Some("Oes") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_23", no(if(generateWelshPDF) Some("Nac ydy") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_24", "777", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_26", yes(if(generateWelshPDF) Some("Byddai") else None), generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_27", "3333", generateWelshPDF=generateWelshPDF)

    behave like pdfField(fieldName = "IHT435_28", "229988", generateWelshPDF=generateWelshPDF)

    describeTest("IHT435_10_1 to 7 when field in cache but it can be calculated", isWelshTest = generateWelshPDF) in {
      val cacheMap: CacheMap = new CacheMap(cacheMapKey, Map[String, JsValue](
        Constants.percentagePassedToDirectDescendantsId -> JsString("34.8899"),
        Constants.propertyInEstateId -> JsBoolean(true),
        Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.all)
      ))
      checkMultipleFieldValues(acroForm(filledCacheMap=cacheMap, generateWelshPDF=generateWelshPDF), "IHT435_10", "100    ", noDigitsInDecimal)
    }

    describeTest("IHT435_26", isWelshTest = generateWelshPDF) + " when field in cache but it can be calculated" in {
      val cacheMap = CacheMap(
        cacheMapKey, Map(
          Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true),
          Constants.claimDownsizingThresholdId -> JsBoolean(true),
          Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
          Constants.datePropertyWasChangedId -> JsString("2017-04-05")
        )
      )
      acroForm(filledCacheMap=cacheMap, generateWelshPDF=generateWelshPDF).getField("IHT435_26")
        .getValueAsString shouldBe no(if(generateWelshPDF) Some("Na fyddai") else None)
    }
  }

  "PDFHelper" must {
    behave like pdfFieldTest(generateWelshPDF = false)

    behave like pdfFieldTest(generateWelshPDF = true)
  }
}
