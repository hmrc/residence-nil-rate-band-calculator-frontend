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
//    Constants.propertyPassingToDirectDescendantsId -> JsString("88.8899"),
    Constants.exemptionsAndReliefClaimedId -> JsBoolean(true),
    Constants.grossingUpOnEstatePropertyId -> JsBoolean(false)
//    Constants.chargeableEstateValueId -> JsNumber(8893),
//    Constants.chargeableInheritedPropertyValueId -> JsNumber(8894),
//    Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
//    Constants.valueBeingTransferredId -> JsNumber(88728),
//    Constants.claimDownsizingThresholdId -> JsBoolean(false),
//    Constants.datePropertyWasChangedId -> JsString("2017-5-13"),
//    Constants.valueOfChangedPropertyId -> JsNumber(888),
//    Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
//    Constants.grossingUpOnEstateAssetsId -> JsBoolean(false),
//    Constants.valueOfAssetsPassingId -> JsNumber(777),
//    Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true),
//    Constants.valueAvailableWhenPropertyChangedId -> JsNumber(3333)
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

  private def checkDate(acroForm: PDAcroForm, baseFieldName:String, expectedDate:String) = {
    acroForm.getField(baseFieldName + "_01").getValueAsString shouldBe expectedDate.charAt(0).toString
  }

  def testText(fieldName:String) = s"Field $fieldName should be correctly generated in the generated PDF from the value stored in the cache"

  "onPageLoad" must {
    "return 200 for a GET" in {
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    testText("IHT435_03") in {
      checkDate(acroForm, "IHT435_03", "12052017")
    }

    testText("IHT435_05") in {
      acroForm.getField("IHT435_05").getValueAsString shouldBe "Yes"
    }

    testText("IHT435_06") in {
      acroForm.getField("IHT435_06").getValueAsString shouldBe "500000"
    }

    testText("IHT435_07") in {
      acroForm.getField("IHT435_07").getValueAsString shouldBe "450000"
    }

    testText("IHT435_08") in {
      acroForm.getField("IHT435_08").getValueAsString shouldBe "No"
    }

    testText("IHT435_10") in {
      acroForm.getField("IHT435_10").getValueAsString shouldBe "9948"
    }

    testText("IHT435_12") in {
      acroForm.getField("IHT435_12").getValueAsString shouldBe "Yes"
    }

    testText("IHT435_13") in {
      acroForm.getField("IHT435_13").getValueAsString shouldBe "No"
    }


  }
}
