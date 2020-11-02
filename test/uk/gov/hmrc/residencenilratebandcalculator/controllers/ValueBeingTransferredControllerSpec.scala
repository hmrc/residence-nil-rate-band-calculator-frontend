/*
 * Copyright 2020 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import play.api.data.FormError
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc.{DefaultMessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, AnswerRows, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_being_transferred
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, Constants, FrontendAppConfig, Navigator}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

class ValueBeingTransferredControllerSpec extends BaseSpec with HttpResponseMocks with MockSessionConnector {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  val errorKeyBlank = "value_being_transferred.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val mockConfig = injector.instanceOf[FrontendAppConfig]

  val navigator = injector.instanceOf[Navigator]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  def mockRnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.getNilRateBand(any[String])(any[HeaderCarrier])) thenReturn Future.successful(HttpResponse(200, Some(JsNumber(100000))))
    mockConnector
  }

  def createView = (value: Option[Map[String, String]]) => {
    val answerRow = new AnswerRow("What was the date of death?", "11 May 2017", routes.DateOfDeathController.onPageLoad().url)
    value match {
      case None => value_being_transferred("£100,000.00", NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge),answerRows = Seq(answerRow))(fakeRequest, messages, mockConfig)
      case Some(v) => value_being_transferred("£100,000.00", NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v), Seq(answerRow))(fakeRequest, messages, mockConfig)
    }
  }

  def createViewWithBacklink = (value: Option[Map[String, String]]) => {
    val answerRow = new AnswerRow("What was the date of death?", "11 May 2017", routes.DateOfDeathController.onPageLoad().url)
    value match {
      case None => value_being_transferred("£100,000.00", NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge),answerRows = Seq(answerRow))(fakeRequest, messages, mockConfig)
      case Some(v) => value_being_transferred( "£100,000.00", NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v), Seq(answerRow))(fakeRequest, messages, mockConfig)
    }
  }

  def createController = () => new ValueBeingTransferredController(messagesControllerComponents, mockSessionConnector, navigator, mockRnrbConnector, mockConfig)

  def testValue = "100000"

  "Value Being Transferred Controller" must {
    "if the date of death key is set return 200 for a GET" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "if the date of death key is set return the View for a GET" in {
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      Jsoup.parse(contentAsString(result)).title() shouldBe messages("value_being_transferred.title")
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
      verifyValueIsCached(Constants.valueBeingTransferredId, 100000)
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
      setCacheMap(new CacheMap("", Map(Constants.dateOfDeathId -> JsString("2017-5-11"), Constants.valueBeingTransferredId -> JsNumber(100000))))
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

    "The answer constants should be the same as the calulated constants for the controller" in {
      val filledOutCacheMap = new CacheMap("",
        Map[String, JsValue](
          Constants.dateOfDeathId -> JsString("2019-03-04"),
          Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
          Constants.valueOfEstateId -> JsNumber(500000),
          Constants.chargeableEstateValueId -> JsNumber(450000),
          Constants.propertyInEstateId -> JsBoolean(true),
          Constants.propertyValueId -> JsNumber(400000),
          Constants.propertyPassingToDirectDescendantsId -> JsBoolean(true),
          Constants.percentagePassedToDirectDescendantsId -> JsNumber(100),
          Constants.transferAnyUnusedThresholdId -> JsBoolean(true)
        ))
      val controllerId = createController().controllerId
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      calculatedList shouldBe (
        List(Constants.dateOfDeathId,
             Constants.partOfEstatePassingToDirectDescendantsId,
             Constants.valueOfEstateId,
             Constants.chargeableEstateValueId,
             Constants.propertyInEstateId,
             Constants.propertyValueId,
             Constants.propertyPassingToDirectDescendantsId,
             Constants.percentagePassedToDirectDescendantsId,
             Constants.transferAnyUnusedThresholdId))
      true shouldBe(true)
    }

    "validate" must {

      "return a FormError when given a value greater than the nil rate band for the year of death" in {
        val cacheMap = CacheMap("a", Map(Constants.dateOfDeathId -> JsString("2020-01-01")))
        val userAnswers = new UserAnswers(cacheMap)
        val testValue = 123000
        val controller = createController()
        val result = controller.validate(testValue, "100000", userAnswers)
        result.map(x => x should be(Some(FormError("value", "value_being_transferred.error", Seq(100000, "2019", "2020")))))
      }

      "return a None when given a value less than or equal to the nil rate band for the year of death" in {
        val cacheMap = CacheMap("a", Map(Constants.dateOfDeathId -> JsString("2020-01-01")))
        val userAnswers = new UserAnswers(cacheMap)
        val testValue = 90000
        val controller = createController()
        val result = controller.validate(testValue, "100000", userAnswers)
        result.map(x => x should be(None))
      }

      "throw an exception when given a nil rate band that cannot be parsed into an int" in {
        val exception = intercept[NumberFormatException] {
          val cacheMap = CacheMap("a", Map(Constants.dateOfDeathId -> JsString("2020-01-01")))
          val userAnswers = new UserAnswers(cacheMap)
          val testValue = 90000
          val controller = createController()
          controller.validate(testValue, "not a number", userAnswers)
        }
        exception.getMessage shouldBe "Bad value in nil rate band"
      }

    }

  }
}
