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

import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.libs.json._
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

  var controller: CheckAnswersController = _

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

  }
}
