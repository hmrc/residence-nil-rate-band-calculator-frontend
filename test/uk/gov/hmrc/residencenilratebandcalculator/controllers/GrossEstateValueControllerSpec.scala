/*
 * Copyright 2016 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value
import play.api.test.Helpers._

class GrossEstateValueControllerSpec extends ControllerSpecBase {
  "Gross Estate Value Controller" must {

    val fakeRequest = FakeRequest("GET", "/")

    "return 200 for a GET" in {
      val result = new GrossEstateValueController(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the Gross Estate View for a GET" in {
      val result = new GrossEstateValueController(frontendAppConfig, messagesApi).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe gross_estate_value(frontendAppConfig)(fakeRequest, messagesApi.preferred(fakeRequest)).toString
    }
  }
}
