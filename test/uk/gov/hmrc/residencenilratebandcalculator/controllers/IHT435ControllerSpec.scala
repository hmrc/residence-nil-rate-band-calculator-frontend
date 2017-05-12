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

import java.io.ByteArrayOutputStream

import com.google.inject.Provider
import org.mockito.Matchers._
import org.mockito.Mockito._
import play.api.{Application, Environment}
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.utils.PDFHelper

class IHT435ControllerSpec extends UnitSpec with WithFakeApplication with MockSessionConnector {
  private val fakeRequest = FakeRequest("", "")
  private val injector = fakeApplication.injector

  private def messagesApi = injector.instanceOf[MessagesApi]

  def applicationProvider: Provider[Application] = injector.instanceOf[Provider[Application]]
  implicit val appProvider: Provider[Application] = applicationProvider

  private def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private val env = injector.instanceOf[Environment]

  private val mockPDFHelper = mock[PDFHelper]

  private def controller = new IHT435Controller(frontendAppConfig, env, messagesApi, mockSessionConnector, mockPDFHelper, applicationProvider)

  "onPageLoad" must {
    "return 200" in {
      when(mockPDFHelper.generatePDF(any())) thenReturn Some(new ByteArrayOutputStream())
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return exception when no resource" in {
      when(mockPDFHelper.generatePDF(any())) thenReturn None
      a[RuntimeException] shouldBe thrownBy {
        status(controller.onPageLoad()(fakeRequest))
      }
    }

    "with an expired session return a redirect to an expired session page" in {
      expireSessionConnector()
      val result = controller.onPageLoad()(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
