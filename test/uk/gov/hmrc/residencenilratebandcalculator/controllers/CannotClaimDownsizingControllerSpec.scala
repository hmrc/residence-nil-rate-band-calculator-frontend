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

import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.views.html.cannot_claim_downsizing
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}

class CannotClaimDownsizingControllerSpec extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector with MockitoSugar {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  "Cannot Claim Downsizing Controller" must {
    "return 200 for a GET" in {
      val controller = new CannotClaimDownsizingController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

      val result = controller.onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val controller = new CannotClaimDownsizingController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

      val result = controller.onPageLoad(fakeRequest)
      contentAsString(result) shouldBe
        cannot_claim_downsizing(frontendAppConfig, "cannot_claim_downsizing.date_of_disposal_too_early_reason",
          routes.ResultsController.onPageLoad, Seq())(fakeRequest, messages).toString
    }

    "throw an exception when the cache is unavailable" in {
      val mockSessionConnector = mock[SessionConnector]
      val controller = new CannotClaimDownsizingController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

      an[RuntimeException] should be thrownBy controller.onPageLoad(fakeRequest)
    }
  }
}
