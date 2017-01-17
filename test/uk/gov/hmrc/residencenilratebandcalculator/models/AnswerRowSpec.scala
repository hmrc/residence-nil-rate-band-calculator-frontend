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

import org.joda.time.LocalDate
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class AnswerRowSpec extends UnitSpec with WithFakeApplication {
  val injector = fakeApplication.injector

  val fakeRequest = FakeRequest("", "")

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  val mockCall = Call("", "")

  "Answer Row" must {

    "correctly format an integer as money" in {
      AnswerRow("", 1000, mockCall)(messages).data shouldBe "£1,000.00"
    }

    "correctly format a date" in {
      AnswerRow("", new LocalDate(2017, 6, 1), mockCall)(messages).data shouldBe "1 June 2017"
    }

    "correctly format a true Boolean as Yes" in {
      AnswerRow("", true, mockCall)(messages).data shouldBe messages("site.yes")
    }

    "correctly format a false Boolean as No" in {
      AnswerRow("", false, mockCall)(messages).data shouldBe messages("site.no")
    }

    "pull the title from the messages file" in {
      AnswerRow("site.yes", true, mockCall)(messages).title shouldBe messages("site.yes")
    }

    "use the url from the provided Call object" in {
      AnswerRow("", true, Call("", "http://example.com"))(messages).url shouldBe "http://example.com"
    }
  }
}
