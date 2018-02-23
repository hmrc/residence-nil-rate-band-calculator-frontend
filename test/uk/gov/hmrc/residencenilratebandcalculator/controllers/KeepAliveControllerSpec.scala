/*
 * Copyright 2018 HM Revenue & Customs
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

import akka.stream.Materializer
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class KeepAliveControllerSpec extends UnitSpec with WithFakeApplication {

  val fakeRequest = FakeRequest("", "")

  "Calling the onPageLoad action" should {
    lazy val result = fakeApplication.injector.instanceOf[KeepAliveController].onPageLoad(fakeRequest)
    lazy implicit val mat = fakeApplication.injector.instanceOf[Materializer]

    "return a status of 200" in {
      status(result) shouldBe 200
    }

    "return the message 'OK'" in {
      await(bodyOf(result)) shouldBe "OK"
    }
  }
}
