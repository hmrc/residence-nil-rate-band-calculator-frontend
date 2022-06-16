/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector

class AnswerRowsSpec extends CommonPlaySpec with MockSessionConnector with JodaWrites with WithCommonFakeApplication {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val cacheMap = CacheMap("", Map[String, JsValue](
    "id1" -> JsBoolean(true),
    "id2" -> JsNumber(1000),
    "id3" -> Json.toJson(new LocalDate(2017, 6, 1))
  ))

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  "Answer Rows" must {

    "correct create answer rows when given valid data" in {

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

    "correctly create Integer AnswerRows without a fractional part" in {
      val data = 100
      AnswerRows.intAnswerRowFn("message.key", "", Call("", "http://example.com"))(JsNumber(data))(messages) shouldBe
        AnswerRow(messages("message.key"), "£100", "http://example.com")
    }

    "throw an exception when intAnswerRowFn is not passed a JSON number" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.intAnswerRowFn("message.key", "", Call("", ""))(JsString(""))(messages)
    }

    "correctly create Boolean AnswerRows" in {
      val data = true
      AnswerRows.boolAnswerRowFn("message.key", "", Call("", "http://example.com"))(JsBoolean(data))(messages) shouldBe
        AnswerRow(messages("message.key"), "Yes", "http://example.com")
    }

    "throw an exception when boolAnswerRowFn is not passed a JSON boolean" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.boolAnswerRowFn("message.key", "", Call("", ""))(JsString(""))(messages)
    }

    "correctly create LocalDate AnswerRows" in {
      val data = new LocalDate(2017, 6, 1)
      AnswerRows.dateAnswerRowFn("message.key", "", Call("", "http://example.com"))(JsString(data.toString))(messages) shouldBe
        AnswerRow(messages("message.key"), "1 June 2017", "http://example.com")
    }

    "throw an exception when dateAnswerRowFn is not passed a legal LocalDate" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.dateAnswerRowFn("message.key", "", Call("", ""))(JsString(""))(messages)
    }

    "correctly create percentage AnswerRows" in {
      val data = 55.0
      AnswerRows.percentAnswerRowFn("message.key", "", Call("", "http://example.com"))(JsNumber(data))(messages) shouldBe
        AnswerRow(messages("message.key"), "55%", "http://example.com")
    }

    "throw an exception when percentAnswerRowFn is not passed a double" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.percentAnswerRowFn("message.key", "", Call("", ""))(JsString(""))(messages)
    }

    "correctly create Chargeable Property Value AnswerRows without a fractional part" in {
      val data = PropertyValueAfterExemption(1000, 5000)
      AnswerRows.intAnswerRowFn("message.key", "",
        Call("", "http://example.com"))(JsNumber(data.value))(messages) shouldBe
        AnswerRow(messages("message.key"), "£1,000", "http://example.com")

      AnswerRows.intAnswerRowFn("message.key", "",
        Call("", "http://example.com"))(JsNumber(data.inheritedValue))(messages) shouldBe
        AnswerRow(messages("message.key"), "£5,000", "http://example.com")
    }

    "correctly create Property Passing To Direct Descendants AnswerRow" in {
      val data = Constants.all
      AnswerRows.propertyPassingToDirectDescendantsAnswerRowFn("message.key", "", Call("", "http://example.com"))(JsString(data))(messages) shouldBe
        AnswerRow(messages("message.key"), messages("property_passing_to_direct_descendants.all"), "http://example.com")
    }

    "throw an exception when propertyPassingToDirectDescendantsAnswerRowFn is not passed a string" in {
      an[RuntimeException] should be thrownBy
        AnswerRows.propertyPassingToDirectDescendantsAnswerRowFn("message.key", "", Call("", ""))(JsBoolean(true))(messages)
    }

    "ignore data in the cache map which does not have a corresponding key in the answer rows function map" in {
      val cacheMap = CacheMap("", Map[String, JsValue](
        "id1" -> JsBoolean(true),
        "id2" -> JsNumber(1000)
      ))
      setCacheMap(cacheMap)

      val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
        "id1" -> ((_: JsValue) => (_: Messages) => AnswerRow("title1", "Yes", "http://example.com/one"))
      )
      val rowOrder = Map[String, Int](
        "id1" -> 1
      )

      val result = AnswerRows.constructAnswerRows(cacheMap, answerRowFns, rowOrder, messages)

      result shouldBe Seq(AnswerRow("title1", "Yes", "http://example.com/one"))
    }

    "truncateAndLocateInCacheMap should return an empty map when the id does not exist in the rowOrder list" in {
      val result = AnswerRows.truncateAndLocateInCacheMap("this ID does not exist", cacheMap)

      result.data shouldBe Map()
    }

    "truncateAndLocateInCacheMap should return an empty map when the id is valid but the cache map is empty" in {
      val result = AnswerRows.truncateAndLocateInCacheMap("id1", CacheMap("", Map()))

      result.data shouldBe Map()
    }

    "truncateAndLocateInCacheMap should return map of values from cache map which keyed by this " +
      "constant and previous constants in the list" in {

      val cacheMap = CacheMap("", Map(
        Constants.dateOfDeathId -> JsNumber(0),
        Constants.partOfEstatePassingToDirectDescendantsId -> JsNumber(1),
        Constants.valueOfEstateId -> JsNumber(2),
        Constants.chargeableEstateValueId -> JsNumber(3)
      ))

      val result = AnswerRows.truncateAndLocateInCacheMap(Constants.valueOfEstateId, cacheMap)

      result.data shouldBe Map(Constants.dateOfDeathId -> JsNumber(0),
        Constants.partOfEstatePassingToDirectDescendantsId -> JsNumber(1))
    }

    "truncateAndLocateInCacheMap should return map of values from cache map which keyed by this " +
      "constant and previous constants, not including any with no values" in {

      val cacheMap = CacheMap("", Map(
        Constants.dateOfDeathId -> JsNumber(0),
        Constants.valueOfEstateId -> JsNumber(2),
        Constants.chargeableEstateValueId -> JsNumber(3)
      ))

      val result = AnswerRows.truncateAndLocateInCacheMap(Constants.valueOfEstateId, cacheMap)

      result.data shouldBe Map(Constants.dateOfDeathId -> JsNumber(0))
    }

    "create answer rows when given truncated data" in {

      val cacheMap = CacheMap("", Map(
        Constants.dateOfDeathId -> JsNumber(0),
        Constants.partOfEstatePassingToDirectDescendantsId -> JsNumber(1),
        Constants.valueOfEstateId -> JsNumber(2),
        Constants.chargeableEstateValueId -> JsNumber(3)
      ))

      setCacheMap(cacheMap)

      val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
        Constants.dateOfDeathId -> ((_: JsValue) => (_: Messages) => AnswerRow("title1", "Yes", "http://example.com/one")),
        Constants.partOfEstatePassingToDirectDescendantsId -> ((_: JsValue) => (_: Messages) => AnswerRow("title2", "£1,000.00", "http://example.com/two")),
        Constants.valueOfEstateId -> ((_: JsValue) => (_: Messages) => AnswerRow("title3", "1 June 2017", "http://example.com/three"))
      )
      val rowOrder = Map[String, Int](
        Constants.dateOfDeathId -> 0,
        Constants.partOfEstatePassingToDirectDescendantsId -> 1,
        Constants.valueOfEstateId -> 2,
        Constants.chargeableEstateValueId -> 3
      )

      val result = AnswerRows.constructAnswerRows(AnswerRows.truncateAndLocateInCacheMap(
        Constants.valueOfEstateId, cacheMap),
        answerRowFns, rowOrder, messages)

      result shouldBe Seq(
        AnswerRow("title1", "Yes", "http://example.com/one"),
        AnswerRow("title2", "£1,000.00", "http://example.com/two"))
    }

  }
}
