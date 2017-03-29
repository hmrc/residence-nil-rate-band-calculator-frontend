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

import akka.util.ByteString
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import play.api.http.Status
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

class IHT435ControllerSpec extends UnitSpec with WithFakeApplication with MockSessionConnector {
  private val fakeRequest = FakeRequest("", "")

  private val injector = fakeApplication.injector

  private def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private def controller = new IHT435Controller(frontendAppConfig, mockSessionConnector)

  private val filledcacheMap: CacheMap = new CacheMap("", Map[String, JsValue](
    Constants.dateOfDeathId -> JsString("2017-5-12"),
    Constants.assetsPassingToDirectDescendantsId -> JsBoolean(true),
    Constants.valueOfEstateId -> JsNumber(500000),
    Constants.chargeableEstateValueId -> JsNumber(450000),
    Constants.propertyInEstateId -> JsBoolean(false),
    Constants.propertyValueId -> JsNumber(9948),
//    Constants.percentagePassedToDirectDescendantsId -> JsString("234.8899"),
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
    Constants.valueAvailableWhenPropertyChangedId -> JsNumber(3333)
  ))

  private def acroForm: PDAcroForm = {
    setCacheMap(filledcacheMap)
    val result = controller.onPageLoad()(fakeRequest)
    val content: ByteString = contentAsBytes(result)
    val pdfDoc = PDDocument.load(content.toByteBuffer.array())
    val acroForm = pdfDoc.getDocumentCatalog.getAcroForm
    pdfDoc.close()
    acroForm
  }

  private def checkParts(acroForm: PDAcroForm, baseFieldName:String, expectedDate:String, totalParts:Int) = {
    for(position <- 1 to totalParts) yield {
      acroForm.getField(baseFieldName + "_0" + position).getValueAsString shouldBe expectedDate.charAt(position - 1).toString
    }
  }

  private def describeTest(fieldName:String) = s"generate the correct value for field $fieldName in the generated PDF from the value stored in the cache"

  private def pdfField(fieldName:String, expectedValue:String) = {
    describeTest(fieldName) in {
      acroForm.getField(fieldName).getValueAsString shouldBe expectedValue
    }
  }

  "onPageLoad" must {
    "return 200" in {
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "with an expired session return a redirect to an expired session page" in {
      expireSessionConnector()
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    describeTest("IHT435_03") in {
      checkParts(acroForm, "IHT435_03", "12052017", 8)
    }

    behave like pdfField("IHT435_05", "Yes")

    behave like pdfField("IHT435_06", "500000")

    behave like pdfField("IHT435_07", "450000")

    behave like pdfField("IHT435_08", "No")

    behave like pdfField("IHT435_10", "9948")

//    describeTest("IHT435_10_1 to 7 (decimal number)") in {
//      checkParts(acroForm, "IHT435_10", "2348899", 7)
//    }

    behave like pdfField("IHT435_12", "Yes")

    behave like pdfField("IHT435_13", "No")

    behave like pdfField("IHT435_14", "8893")

    behave like pdfField("IHT435_15", "8894")

    behave like pdfField("IHT435_16", "Yes")

    behave like pdfField("IHT435_17", "88728")

    behave like pdfField("IHT435_18", "No")

    describeTest("IHT435_20") in {
      checkParts(acroForm, "IHT435_20", "13052017", 8)
    }

    behave like pdfField("IHT435_21", "888")

    behave like pdfField("IHT435_22", "Yes")

    behave like pdfField("IHT435_23", "No")

    behave like pdfField("IHT435_24", "777")

    behave like pdfField("IHT435_26", "Yes")

    behave like pdfField("IHT435_27", "3333")
  }
}
