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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import play.api.data.FormError
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json._
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, SessionKeys}
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, CacheMap}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_available_when_property_changed
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

import scala.concurrent.Future

class ValueAvailableWhenPropertyChangedControllerSpec extends CommonPlaySpec with HttpResponseMocks with MockSessionConnector with WithCommonFakeApplication{

  val errorKeyBlank = "value_available_when_property_changed.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(SessionKeys.sessionId -> "id")

  val injector: Injector = fakeApplication.injector

  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val navigator: Navigator = injector.instanceOf[Navigator]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]

  val value_available_when_property_changed: value_available_when_property_changed = injector.instanceOf[value_available_when_property_changed]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def mockRnrbConnector: RnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.getNilRateBand(any[String])(any[HeaderCarrier])) thenReturn
      Future.successful(HttpResponse(200, JsNumber(100000).toString))
    mockConnector
  }

  def createView: Option[Map[String, String]] => HtmlFormat.Appendable = {
    case None => value_available_when_property_changed(
      "£100,000", NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge))(fakeRequest, messages)
    case Some(v) => value_available_when_property_changed(
      "£100,000", NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v))(fakeRequest, messages)
  }

  def testValue = "100000"

  def createController: () => ValueAvailableWhenPropertyChangedController = () =>
    new ValueAvailableWhenPropertyChangedController(
      messagesControllerComponents, mockSessionConnector, navigator, mockRnrbConnector, mockValidatedSession, value_available_when_property_changed)

  "Value Available When Property Changed Controller" must {

    "return 200 for a GET" in {
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the View for a GET" in {
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      Jsoup.parse(contentAsString(result)).title() mustBe messages("value_available_when_property_changed.title")  + " - Calculate the available RNRB - GOV.UK"

    }

    "if the date property was changed key is not set throw an exception" in {
      val exception = intercept[NoSuchElementException] {
        val result: Future[Result] = createController().onPageLoad(Reads.IntReads)(fakeRequest)
        contentAsString(result) mustBe createView(None).toString
      }
      exception.getMessage mustBe "key not found: DatePropertyWasChanged"
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue)).withMethod("POST")
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"))))
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", "100000")).withMethod("POST")
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"))))
      await(createController().onSubmit(Writes.IntWrites)(fakePostRequest))
      verifyValueIsCached(Constants.valueAvailableWhenPropertyChangedId, 100000)
    }

    "return bad request on submit with invalid data" in {
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"))))
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value)).withMethod("POST")
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) mustBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data is submitted" in {
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"))))
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value)).withMethod("POST")
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      contentAsString(result) mustBe createView(Some(Map("value" -> value))).toString
    }

    "not store invalid submitted data" in {
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value)).withMethod("POST")
      createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      setCacheMap(new CacheMap("", Map(Constants.datePropertyWasChangedId -> JsString("2018-5-11"),
        Constants.valueAvailableWhenPropertyChangedId -> JsNumber(100000))))
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      contentAsString(result) mustBe createView(Some(Map("value" -> testValue))).toString
    }

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url)
    }

    "On a page submit with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onSubmit(Writes.IntWrites)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url)
    }

    "The answer constants must be the same as the calulated constants for the controller" in {
      val filledOutCacheMap = new CacheMap("",
        Map[String, JsValue](
          Constants.dateOfDeathId -> JsString("2019-03-04"),
          Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
          Constants.valueOfEstateId -> JsNumber(100000),
          Constants.chargeableEstateValueId -> JsNumber(95000),
          Constants.propertyInEstateId -> JsBoolean(true),
          Constants.propertyValueId -> JsNumber(90000),
          Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.all),
          Constants.percentagePassedToDirectDescendantsId -> JsNumber(0),
          Constants.chargeablePropertyValueId -> JsNumber(70000),
          Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
          Constants.valueBeingTransferredId -> JsNumber(60000),
          Constants.claimDownsizingThresholdId -> JsBoolean(true),
          Constants.datePropertyWasChangedId -> JsString("2019-01-04"),
          Constants.valueOfChangedPropertyId -> JsNumber(45000),
          Constants.assetsPassingToDirectDescendantsId -> JsBoolean(true),
          Constants.grossingUpOnEstateAssetsId -> JsBoolean(false),
          Constants.valueOfAssetsPassingId -> JsNumber(40000),
          Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true)
        ))
      val controllerId = createController().controllerId
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      calculatedList mustBe
        List(Constants.dateOfDeathId,
          Constants.partOfEstatePassingToDirectDescendantsId,
          Constants.valueOfEstateId,
          Constants.chargeableEstateValueId,
          Constants.propertyInEstateId,
          Constants.propertyValueId,
          Constants.propertyPassingToDirectDescendantsId,
          Constants.percentagePassedToDirectDescendantsId,
          Constants.chargeablePropertyValueId,
          Constants.transferAnyUnusedThresholdId,
          Constants.valueBeingTransferredId,
          Constants.claimDownsizingThresholdId,
          Constants.datePropertyWasChangedId,
          Constants.valueOfChangedPropertyId,
          Constants.assetsPassingToDirectDescendantsId,
          Constants.grossingUpOnEstateAssetsId,
          Constants.valueOfAssetsPassingId,
          Constants.transferAvailableWhenPropertyChangedId)
      true mustBe true
    }

    "validate" must {

      "return a FormError when given a value greater than the nil rate band for the year of disposal" in {
        val testValue = 123000
        val controller = createController()
        val result = controller.validate(testValue, "100000")
        result.map(x => x must be(Some(FormError("value", "value_available_when_property_changed.error"))))
      }

      "return a None when given a value less than or equal to the nil rate band for the year of disposal" in {
        val testValue = 90000
        val controller = createController()
        val result = controller.validate(testValue, "100000")
        result.map(x => x must be(None))
      }

      "throw an exception when given a nil rate band that cannot be parsed into an int" in {
        val exception = intercept[NumberFormatException] {
          val testValue = 90000
          val controller = createController()
          controller.validate(testValue, "not a number")
        }
        exception.getMessage mustBe "Bad value in nil rate band"
      }

    }

  }
}
