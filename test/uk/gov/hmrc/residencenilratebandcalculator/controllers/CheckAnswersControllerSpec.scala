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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.joda.time.LocalDate
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json._
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow
import uk.gov.hmrc.residencenilratebandcalculator.views.html.check_answers
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}

class CheckAnswersControllerSpec extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector {

  val injector = fakeApplication.injector

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val fakeRequest = FakeRequest("", "")

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  val navigator = injector.instanceOf[Navigator]

  var controller: CheckAnswersController = null

  def fixture() = {
    controller = new CheckAnswersController(frontendAppConfig, navigator, messagesApi, mockSessionConnector)
  }

  "Check Answers Controller" must {
    "return OK on successful page load" in {
      fixture()
      val cacheMap = CacheMap("", Map[String, JsValue]())
      setCacheMap(cacheMap)

      val response = controller.onPageLoad(fakeRequest)
      status(response) shouldBe Status.OK
    }

    "return the check answers view on GET" in {
      fixture()
      val cacheMap = CacheMap("", Map[String, JsValue]())
      setCacheMap(cacheMap)

      val response = controller.onPageLoad(fakeRequest)
      val view = check_answers(frontendAppConfig, Seq[AnswerRow](), routes.ResultsController.onPageLoad())(fakeRequest, messages)

      contentAsString(response) shouldBe view.toString
    }

    "correct create answer rows when given valid data" in {
      fixture()
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

      val result = controller.constructAnswerRows(cacheMap, answerRowFns, rowOrder, fakeRequest)

      result shouldBe Seq(
        AnswerRow("title1", "Yes", "http://example.com/one"),
        AnswerRow("title2", "£1,000.00", "http://example.com/two"),
        AnswerRow("title3", "1 June 2017", "http://example.com/three"))
    }

    "have the same keys in rowOrder and answerRowFns" in {
      fixture()
      controller.rowOrder.keys shouldEqual controller.answerRowFns.keys
    }

    "correctly create Integer AnswerRows" in {
      fixture()
      val data = 100
      controller.intAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsNumber(data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "£100.00", "http://example.com")
    }

    "throw an exception when intAnswerRowFn is not passed a JSON number" in {
      fixture()
      an[RuntimeException] should be thrownBy
        controller.intAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create Boolean AnswerRows" in {
      fixture()
      val data = true
      controller.boolAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsBoolean(data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "Yes", "http://example.com")
    }

    "throw an exception when boolAnswerRowFn is not passed a JSON boolean" in {
      fixture()
      an[RuntimeException] should be thrownBy
        controller.boolAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create LocalDate AnswerRows" in {
      fixture()
      val data = new LocalDate(2017, 6, 1)
      controller.dateAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsString(data.toString))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "1 June 2017", "http://example.com")
    }

    "throw an exception when dateAnswerRowFn is not passed a legal LocalDate" in {
      fixture()
      an[RuntimeException] should be thrownBy
        controller.dateAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }

    "correctly create percentage AnswerRows" in {
      fixture()
      val data = 55.0
      controller.percentAnswerRowFn("check_answers.title", "", () => Call("", "http://example.com"))(JsNumber(data))(messages) shouldBe
        AnswerRow(messages("check_answers.title"), "55.0%", "http://example.com")
    }

    "throw an exception when percentAnswerRowFn is not passed a double" in {
      fixture()
      an[RuntimeException] should be thrownBy
        controller.percentAnswerRowFn("check_answers.title", "", () => Call("", ""))(JsString(""))(messages)
    }
  }
}
