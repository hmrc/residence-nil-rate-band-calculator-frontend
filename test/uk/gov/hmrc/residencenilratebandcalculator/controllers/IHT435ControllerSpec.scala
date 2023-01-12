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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import java.io.ByteArrayOutputStream

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import play.api.Environment
import play.api.http.Status
import play.api.mvc.DefaultMessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.utils.PDFHelperImpl

class IHT435ControllerSpec extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication {
  private val fakeRequest = FakeRequest("", "")

  private val injector = fakeApplication.injector

  private val env = injector.instanceOf[Environment]

  private val mockPDFHelper = mock[PDFHelperImpl]

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  private def controller = new IHT435Controller(env, messagesControllerComponents, mockSessionConnector, mockPDFHelper)

  "onPageLoad" must {
    "return 200" in {
      when(mockPDFHelper.generatePDF(any(), any())(any())) thenReturn Some(new ByteArrayOutputStream())
      val result = controller.onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return exception when no resource" in {
      when(mockPDFHelper.generatePDF(any(), any())(any())) thenReturn None
      a[RuntimeException] shouldBe thrownBy {
        status(controller.onPageLoad(fakeRequest))
      }
    }

    "with an expired session return a redirect to an expired session page" in {
      expireSessionConnector()
      val result = controller.onPageLoad(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
