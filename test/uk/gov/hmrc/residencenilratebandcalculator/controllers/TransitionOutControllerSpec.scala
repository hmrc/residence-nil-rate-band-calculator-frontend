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

import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.views.html.not_possible_to_use_service

class TransitionOutControllerSpec extends UnitSpec with WithFakeApplication with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]
  def messagesApi = injector.instanceOf[MessagesApi]
  def messages = messagesApi.preferred(fakeRequest)

  "Transition controller" must {
    "return 200 for a GET" in {
      val result = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the Not Possible to use Calculator view for a GET" in {
      val result = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe
        not_possible_to_use_service(frontendAppConfig, "not_possible_to_use_service.grossing_up", Seq())(fakeRequest, messages).toString
    }
  }
}
