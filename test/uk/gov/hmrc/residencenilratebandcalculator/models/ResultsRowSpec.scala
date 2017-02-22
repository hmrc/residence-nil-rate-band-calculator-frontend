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

import java.text.NumberFormat
import java.util.Locale

import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ResultsRowSpec extends UnitSpec with WithFakeApplication {
  val injector = fakeApplication.injector

  val fakeRequest = FakeRequest("", "")

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  "Results Row" must {
    "create a properly formatted ResultsRow" in {
      ResultsRow("results.applicableNilRateBandAmount.label", 1000)(messages) shouldBe
        ResultsRow(messages("results.applicableNilRateBandAmount.label"), NumberFormat.getCurrencyInstance(Locale.UK).format(1000))
    }
  }
}
