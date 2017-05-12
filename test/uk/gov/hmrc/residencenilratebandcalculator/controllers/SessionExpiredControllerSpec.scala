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

import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.session_expired

class SessionExpiredControllerSpec extends HtmlSpec with WithFakeApplication with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  "Session Expired controller" must {

    "return 200 for a GET" in {
      val result = new SessionExpiredController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      status(result) shouldBe 200
    }

    "return the View for a GET" in {
      val result = new SessionExpiredController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe session_expired(frontendAppConfig)(fakeRequest, messages, applicationProvider).toString
    }
  }
}
