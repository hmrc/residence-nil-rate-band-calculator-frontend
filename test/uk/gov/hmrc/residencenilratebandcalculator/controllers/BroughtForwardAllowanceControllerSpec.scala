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

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.data.FormError
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNumber, JsString, Reads, Writes}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.brought_forward_allowance
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BroughtForwardAllowanceControllerSpec extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  def mockRnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.getNilRateBand(any[String])) thenReturn Future.successful(HttpResponse(200, Some(JsNumber(100000))))
    mockConnector
  }

  def createView = (value: Option[Map[String, String]]) => {
    val url = "<function1>"
    val answerRow = new AnswerRow("What was the date of death?", "11 May 2017", uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.DateOfDeathController.onPageLoad().url.toString)
    value match {
      case None => brought_forward_allowance(frontendAppConfig, url, "£100,000.00", answerRows = Seq(answerRow))(fakeRequest, messages)
      case Some(v) => brought_forward_allowance(frontendAppConfig, url, "£100,000.00", Some(NonNegativeIntForm().bind(v)), Seq(answerRow))(fakeRequest, messages)
    }
  }

  def createViewWithBacklink = (value: Option[Map[String, String]]) => {
    val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.AnyBroughtForwardAllowanceController.onPageLoad().url.toString()
    val answerRow = new AnswerRow("What was the date of death?", "11 May 2017", uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.DateOfDeathController.onPageLoad().url.toString)
    value match {
      case None => brought_forward_allowance(frontendAppConfig, url, "£100,000.00", answerRows = Seq(answerRow))(fakeRequest, messages)
      case Some(v) => brought_forward_allowance(frontendAppConfig, url, "£100,000.00", Some(NonNegativeIntForm().bind(v)), Seq(answerRow))(fakeRequest, messages)
    }
  }

  def createController = () => new BroughtForwardAllowanceController(frontendAppConfig, messagesApi, mockSessionConnector, navigator, mockRnrbConnector)

  def testValue = "100000"

  "Brought Forward Allowance Controller" must {
    "if the date of death key is set return 200 for a GET" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "if the date of death key is set return the View for a GET" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "if the date of death key is not set throw an exception" in {
      val exception = intercept[NoSuchElementException] {
        val result: Future[Result] = createController().onPageLoad(Reads.IntReads)(fakeRequest)
        contentAsString(result) shouldBe createViewWithBacklink(None).toString
      }
      exception.getMessage shouldBe "key not found: DateOfDeath"
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue))
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"), "value" -> JsNumber(100000))))
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", "100000"))
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"), "value" -> JsNumber(100000))))
      await(createController().onSubmit(Writes.IntWrites)(fakePostRequest))
      verifyValueIsCached(Constants.broughtForwardAllowanceId, 100000)
    }

    "return bad request on submit with invalid data" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data is submitted" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      contentAsString(result) shouldBe createViewWithBacklink(Some(Map("value" -> value))).toString
    }

    "not store invalid submitted data" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"), Constants.broughtForwardAllowanceId -> JsNumber(100000))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      contentAsString(result) shouldBe createView(Some(Map("value" -> testValue))).toString
    }

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "On a page submit with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onSubmit(Writes.IntWrites)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "validate" must {

      "return a FormError when given a value greater than the nil rate band for the year of death" in {
        val testValue = 123000
        val controller = createController()
        val result = controller.validate(testValue, "100000")(new HeaderCarrier())
        result.map(x => x should be(Some(FormError("value", "brought_forward_allowance.error"))))
      }

      "return a None when given a value less than or equal to the nil rate band for the year of death" in {
        val testValue = 90000
        val controller = createController()
        val result = controller.validate(testValue, "100000")(new HeaderCarrier())
        result.map(x => x should be(None))
      }

      "throw an exception when given a nil rate band that cannot be parsed into an int" in {
        val exception = intercept[NumberFormatException] {
          val testValue = 90000
          val controller = createController()
          val result = controller.validate(testValue, "not a number")(new HeaderCarrier())
        }
        exception.getMessage shouldBe "Bad value in nil rate band"
      }

    }

  }
}
