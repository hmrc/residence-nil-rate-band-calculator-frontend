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
import play.api.http.Status
import play.api.libs.json.{JsNumber, Reads}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

class IHT435ControllerSpec extends UnitSpec with WithFakeApplication with MockSessionConnector {
  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def controller = new IHT435Controller(frontendAppConfig, mockSessionConnector)

  "onPageLoad" must {
    "return 200 for a GET" in {
      val result = controller.onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = controller.onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "when the value of the estate is set in the session, it should appear as field IHT435_06 in the generated PDF" in {
      setCacheValue[JsNumber](Constants.valueOfEstateId, JsNumber(500000))

      val result = controller.onPageLoad(Reads.IntReads)(fakeRequest)
      val content: ByteString = contentAsBytes(result)
      val pdfDoc = PDDocument.load(content.toByteBuffer.array())
      val form = pdfDoc.getDocumentCatalog.getAcroForm
      val field = form.getField("IHT435_06")

      field.getValueAsString shouldBe "500000"
    }
  }
}
