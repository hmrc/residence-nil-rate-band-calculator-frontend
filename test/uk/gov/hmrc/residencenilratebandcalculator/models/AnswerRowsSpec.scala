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
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json._
import play.api.mvc.Call
import play.api.test.FakeRequest
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector

class AnswerRowSpecs extends UnitSpec with WithFakeApplication with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  "Answer Rows" must {

    "correct create answer rows when given valid data" in {
      val cacheMap = CacheMap("", Map[String, JsValue](
        "id1" -> JsBoolean(true),
        "id2" -> JsNumber(1000),
        "id3" -> Json.toJson(new LocalDate(2017, 6, 1))
      ))
      setCacheMap(cacheMap)

      val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
        "id1" -> ((_: JsValue) => (_: Messages) => AnswerRow("title1", "Yes", "http://example.com/one")),
        "id2" -> ((_: JsValue) => (_: Messages) => AnswerRow("title2", "£1,000.00", "http://example.com/two")),
        "id3" -> ((_: JsValue) => (_: Messages) => AnswerRow("title3", "1 June 2017", "http://example.com/three"))
      )
      val rowOrder = Map[String, Int](
        "id1" -> 1,
        "id2" -> 2,
        "id3" -> 3
      )

      val result = AnswerRows.constructAnswerRows(cacheMap, answerRowFns, rowOrder, messages)

      result shouldBe Seq(
        AnswerRow("title1", "Yes", "http://example.com/one"),
        AnswerRow("title2", "£1,000.00", "http://example.com/two"),
        AnswerRow("title3", "1 June 2017", "http://example.com/three"))
    }

    "have the same keys in rowOrder and answerRowFns" in {
      AnswerRows.rowOrder.keys shouldEqual AnswerRows.answerRowFns.keys
    }

    "correctly create Integer AnswerRows" in {
      val data = 100
      AnswerRows.intAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsNumber(data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "£100.00", "http://example.com")
    }

    "throw an exception when intAnswerRowFn is not passed a JSON number" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.intAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create Boolean AnswerRows" in {
      val data = true
      AnswerRows.boolAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsBoolean(data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "Yes", "http://example.com")
    }

    "throw an exception when boolAnswerRowFn is not passed a JSON boolean" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.boolAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create LocalDate AnswerRows" in {
      val data = new LocalDate(2017, 6, 1)
      AnswerRows.dateAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsString(data.toString))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "1 June 2017", "http://example.com")
    }

    "throw an exception when dateAnswerRowFn is not passed a legal LocalDate" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.dateAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create percentage AnswerRows" in {
      val data = 55.0
      AnswerRows.percentAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsNumber(data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "55.0%", "http://example.com")
    }

    "throw an exception when percentAnswerRowFn is not passed a double" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.percentAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create property value after exemptions AnswerRows" in {
      val data = PropertyValueAfterExemption(1000, 5000)
      AnswerRows.propertyValueAfterExemptionAnswerRowFn("check_answers.title", "",
        () => Call("", "http://example.com"))(Json.toJson[PropertyValueAfterExemption](data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "£1,000.00 \n£5,000.00", "http://example.com")
    }

    "throw an exception when propertyValueAfterExemptionAnswerRowFn not passed a PropertyValueAfterExemption" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.propertyValueAfterExemptionAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }
  }
}
