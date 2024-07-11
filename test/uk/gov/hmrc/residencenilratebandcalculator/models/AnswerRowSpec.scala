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

package uk.gov.hmrc.residencenilratebandcalculator.models

import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec

import java.time.LocalDate

class AnswerRowSpec extends BaseSpec {
  val injector: Injector = fakeApplication().injector

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  val welshMessages: Messages = messagesApi.preferred(Seq(Lang("cy"))).messages

  val mockCall: Call = Call("", "")

  val testDate: LocalDate = LocalDate.of(2017, 6, 1)

  "Answer Row" must {

    "correctly format an integer as money without a fractional part" in {
      AnswerRow("", 1000, mockCall)(messages).data mustBe "£1,000"
    }

    "correctly format a date" in {
      AnswerRow("", LocalDate.from(testDate), mockCall)(messages).data mustBe "1 June 2017"
      AnswerRow("", LocalDate.from(testDate), mockCall)(welshMessages).data mustBe "1 Mehefin 2017"
    }

    "correctly format a true Boolean as Yes" in {
      AnswerRow("", yesNo = true, mockCall)(messages).data mustBe messages("site.yes")
    }

    "correctly format a false Boolean as No" in {
      AnswerRow("", yesNo = false, mockCall)(messages).data mustBe messages("site.no")
    }

    "correctly format a string" in {
      AnswerRow("", "abc", mockCall)(messages).data mustBe "abc"
    }

    "pull the title from the messages file" in {
      AnswerRow("site.yes", yesNo = true, mockCall)(messages).title mustBe messages("site.yes")
    }

    "use the url from the provided Call object" in {
      AnswerRow("", yesNo = true, Call("", "http://example.com"))(messages).url mustBe "http://example.com"
    }
  }
}
