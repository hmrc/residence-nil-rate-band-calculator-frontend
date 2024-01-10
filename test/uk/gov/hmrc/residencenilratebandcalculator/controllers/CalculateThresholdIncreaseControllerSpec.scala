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

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.calculate_threshold_increase

class CalculateThresholdIncreaseControllerSpec extends HtmlSpec {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val calculateThresholdIncrease: calculate_threshold_increase = fakeApplication().injector.instanceOf[calculate_threshold_increase]

  "Calculate Threshold Increase controller" must {

    "return 200 for a GET" in {
      val result = new CalculateThresholdIncreaseController(messagesControllerComponents, calculateThresholdIncrease).onPageLoad(fakeRequest)
      status(result) shouldBe 200
    }

    "return the View for a GET" in {
      val result = new CalculateThresholdIncreaseController(messagesControllerComponents, calculateThresholdIncrease).onPageLoad(fakeRequest)
      contentAsString(result) shouldBe calculateThresholdIncrease()(fakeRequest, messages).toString
    }
  }
}
