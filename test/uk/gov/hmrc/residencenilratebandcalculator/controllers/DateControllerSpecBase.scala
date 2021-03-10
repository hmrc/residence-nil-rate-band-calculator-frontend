/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.i18n._
import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, Date}
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, Constants, Navigator}

trait DateControllerSpecBase extends BaseSpec with HttpResponseMocks with MockSessionConnector {
  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def messagesApi = injector.instanceOf[MessagesApi]
  def messages = messagesApi.preferred(fakeRequest)

  def rnrbDateController(createController: () => ControllerBase[Date],
                     createView: (Option[Date]) => HtmlFormat.Appendable,
                     cacheKey: String,
                     date: String = "dateOfDeath")(rds: Reads[Date], wts: Writes[Date]) = {

    "return 200 for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody((s"$date${".day"}", "01"), (s"$date${".month"}", "01"), (s"$date${".year"}", "2018"))
      setCacheValue(cacheKey, new LocalDate(2018, 1, 1))
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val value = Date(new LocalDate(2018, 1, 1))
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody((s"$date${".day"}", "01"), (s"$date${".month"}", "01"), (s"$date${".year"}", "2018"))
      setCacheValue(cacheKey, new LocalDate(2018, 1, 1))
      await (createController().onSubmit(wts)(fakePostRequest))
      verifyValueIsCached(cacheKey, value)
    }

    "return bad request on submit with invalid data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody((s"$date${".day"}", value), (s"$date${".month"}", value), (s"$date${".year"}", value))
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data is submitted" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody((s"$date${".day"}", value), (s"$date${".month"}", value), (s"$date${".year"}", value))
      val result = createController().onSubmit(wts)(fakePostRequest)
      contentAsString(result) should include("Give a correct date")
    }

    "not store invalid submitted data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("day", value), ("month", value), ("month", value))
      createController().onSubmit(wts)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      val day = 1
      val month = 1
      val year = 2018
      val value = Date(new LocalDate(year, month, day))
      setCacheValue(cacheKey, value)
      val result = createController().onPageLoad(rds)(fakeRequest)

      contentAsString(result) shouldBe createView(Some(value)).toString
    }
  }

  def nonStartingDateController(createController: () => SimpleControllerBase[Date], answerRowConstants: List[String])(rds: Reads[Date]) = {
    "On a page load with an expired session, return a redirect to an expired session page" in {
      expireSessionConnector()

      val rds = Date.dateReads

      val result = createController().onPageLoad(rds)(fakeRequest)
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
          Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
          Constants.valueBeingTransferredId -> JsNumber(50000),
          Constants.claimDownsizingThresholdId -> JsBoolean(true)
        ))
      val controllerId = createController().controllerId
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      answerRowConstants shouldBe (calculatedList)
    }
  }
}
