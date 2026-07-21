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

import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.residencenilratebandcalculator.controllers.helpers.ControllerSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.calculate_threshold_increase

class CalculateThresholdIncreaseControllerSpec extends ControllerSpec {

  override val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val calculateThresholdIncrease: calculate_threshold_increase =
    inject[calculate_threshold_increase]

  "Calculate Threshold Increase controller" must {

    "return 200 for a GET" in {
      val result = new CalculateThresholdIncreaseController(messagesControllerComponents, calculateThresholdIncrease)
        .onPageLoad(fakeRequest)
      status(result) mustBe 200
    }

    "return the View for a GET" in {
      val result = new CalculateThresholdIncreaseController(messagesControllerComponents, calculateThresholdIncrease)
        .onPageLoad(fakeRequest)
      contentAsString(result) mustBe calculateThresholdIncrease()(fakeRequest, messages).toString
    }
  }

}
