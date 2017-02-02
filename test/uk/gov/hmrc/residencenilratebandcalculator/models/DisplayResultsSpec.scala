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

package uk.gov.hmrc.residencenilratebandcalculator.models

import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class DisplayResultsSpec extends UnitSpec with WithFakeApplication {
  val injector = fakeApplication.injector

  val fakeRequest = FakeRequest("", "")

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  "Display Results" must {
    "include an adjusted allowance when it is different from the default allowance" in {
      val defaultAllowance = 40
      val adjustedAllowance = 50
      DisplayResults(CalculationResult(10, 20, 30, defaultAllowance, adjustedAllowance), Seq())(messages).resultsRows should
        contain(ResultsRow("results.adjustedAllowanceAmount.label", adjustedAllowance)(messages))
    }

    "not include an adjusted allowance when it is the same as the default allowance" in {
      val defaultAndAdjustedAllowance = 40
      val resultsRows = DisplayResults(CalculationResult(10, 20, 30, defaultAndAdjustedAllowance, defaultAndAdjustedAllowance), Seq())(messages).resultsRows
      resultsRows shouldNot contain(ResultsRow("results.adjustedAllowanceAmount.label", defaultAndAdjustedAllowance)(messages))
      resultsRows.size should be(3)
    }
  }

}
