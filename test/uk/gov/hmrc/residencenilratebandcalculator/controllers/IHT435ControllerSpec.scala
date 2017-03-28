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
    Constants.valueOfEstateId -> JsNumber(500000),
    Constants.chargeableEstateValueId -> JsNumber(450000),
    Constants.assetsPassingToDirectDescendantsId -> JsBoolean(true),
    Constants.dateOfDeathId -> JsString("2017-5-12")
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

  def checkDate(acroForm: PDAcroForm, baseFieldName:String, expectedDate:String) = {
    acroForm.getField(baseFieldName + "_01").getValueAsString shouldBe expectedDate.charAt(0).toString
  }

  "onPageLoad" must {

//    "list all" in {
//      controller.ook
//    }

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

    "when the value of the estate (2) is set in the session, it should appear as field IHT435_06 in the generated PDF" in {
      acroForm.getField("IHT435_06").getValueAsString shouldBe "500000"
    }

    "when the amount of the total chargeable estate (3) is set in the session, it should appear as field IHT435_07 in the generated PDF" in {
      acroForm.getField("IHT435_07").getValueAsString shouldBe "450000"
    }

    "when \"Does any of the estate pass to the deceased’s children or other direct descendants?\" (1) is set in the session, " +
      "it should appear as field IHT435_05 in the generated PDF" in {
      acroForm.getField("IHT435_05").getValueAsString shouldBe "Yes"
    }

//    "when \"Does the estate include any residential property that the deceased owned and lived in?\" (4) is set in the session, " +
//      "it should appear as field IHT435_08 in the generated PDF" in {
//      acroForm.getField("IHT435_08").getValueAsString shouldBe "No"
//    }

    "when \"Date of death\" is set in the session, it should appear as the appropriate fields in the generated PDF" in {
      checkDate(acroForm, "IHT435_03", "12052017")


    }
  }
}
