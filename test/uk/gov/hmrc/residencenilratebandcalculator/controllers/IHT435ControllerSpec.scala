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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig

class IHT435ControllerSpec extends UnitSpec with WithFakeApplication with MockSessionConnector {
  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def controller = new IHT435Controller(frontendAppConfig, mockSessionConnector)

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
}
