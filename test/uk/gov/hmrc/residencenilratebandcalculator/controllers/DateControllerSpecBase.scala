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

import play.api.http.Status
import play.api.i18n._
import play.api.inject.Injector
import play.api.libs.json._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, CacheMap, Date}
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}
import java.time.LocalDate

trait DateControllerSpecBase extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication {
  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(SessionKeys.sessionId -> "id")

  val injector: Injector = fakeApplication.injector

  val navigator: Navigator = injector.instanceOf[Navigator]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  def rnrbDateController(
      createController: () => ControllerBase[Date],
      createView: Option[Date] => HtmlFormat.Appendable,
      cacheKey: String,
      date: String = "dateOfDeath"
  )(rds: Reads[Date], wts: Writes[Date]): Unit = {

    "return 200 for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the View for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      contentAsString(result) mustBe createView(None).toString
    }

    "return a redirect on submit with valid data" in {
      val day   = 1
      val month = 1
      val year  = 2018
      val fakePostRequest = fakeRequest
        .withFormUrlEncodedBody((s"$date${".day"}", "01"), (s"$date${".month"}", "01"), (s"$date${".year"}", "2018"))
        .withMethod("POST")
      setCacheValue(cacheKey, LocalDate.of(year, month, day))
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val value = Date(LocalDate.of(2018, 1, 1))
      val fakePostRequest = fakeRequest
        .withFormUrlEncodedBody((s"$date${".day"}", "01"), (s"$date${".month"}", "01"), (s"$date${".year"}", "2018"))
        .withMethod("POST")
      setCacheValue(cacheKey, LocalDate.of(2018, 1, 1))
      await(createController().onSubmit(wts)(fakePostRequest))
      verifyValueIsCached(cacheKey, value)
    }

    "return bad request on submit with invalid data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest
        .withFormUrlEncodedBody((s"$date${".day"}", value), (s"$date${".month"}", value), (s"$date${".year"}", value))
        .withMethod("POST")
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) mustBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data is submitted" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest
        .withFormUrlEncodedBody((s"$date${".day"}", value), (s"$date${".month"}", value), (s"$date${".year"}", value))
        .withMethod("POST")
      val result = createController().onSubmit(wts)(fakePostRequest)
      contentAsString(result) must include(messages(s"$date.error.invalid"))
    }

    "not store invalid submitted data" in {
      val value = "not a number"
      val fakePostRequest =
        fakeRequest.withFormUrlEncodedBody(("day", value), ("month", value), ("month", value)).withMethod("POST")
      createController().onSubmit(wts)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      val day   = 1
      val month = 1
      val year  = 2018
      val value = Date(LocalDate.of(year, month, day))
      setCacheValue(cacheKey, value)
      val result = createController().onPageLoad(rds)(fakeRequest)

      contentAsString(result) mustBe createView(Some(value)).toString
    }
  }

  def nonStartingDateController(
      createController: () => DatePropertyWasChangedController,
      answerRowConstants: List[String]
  ): Unit = {
    "On a page load with an expired session, return a redirect to an expired session page" in {
      expireSessionConnector()

      val rds = Date.dateReads

      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(
        uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url
      )
    }

    "The answer constants must be the same as the calulated constants for the controller" in {
      val filledOutCacheMap = new CacheMap(
        "",
        Map[String, JsValue](
          Constants.dateOfDeathId                            -> JsString("2019-03-04"),
          Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
          Constants.valueOfEstateId                          -> JsNumber(500000),
          Constants.chargeableEstateValueId                  -> JsNumber(450000),
          Constants.propertyInEstateId                       -> JsBoolean(true),
          Constants.propertyValueId                          -> JsNumber(400000),
          Constants.propertyPassingToDirectDescendantsId     -> JsBoolean(true),
          Constants.percentagePassedToDirectDescendantsId    -> JsNumber(100),
          Constants.transferAnyUnusedThresholdId             -> JsBoolean(true),
          Constants.valueBeingTransferredId                  -> JsNumber(50000),
          Constants.claimDownsizingThresholdId               -> JsBoolean(true)
        )
      )
      val controllerId        = createController().controllerId
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList      = AnswerRows.rowOrderList.filter(calculatedConstants contains _)
      answerRowConstants mustBe calculatedList
    }
  }

}
