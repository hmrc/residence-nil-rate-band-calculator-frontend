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
import play.api.i18n._
import play.api.libs.json.{Reads, Writes}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}

trait DateControllerSpecBase extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]
  def messagesApi = injector.instanceOf[MessagesApi]
  def messages = messagesApi.preferred(fakeRequest)

  def rnrbDateController(createController: () => ControllerBase[Date],
                     createView: (Option[Map[String, String]]) => HtmlFormat.Appendable,
                     cacheKey: String)(rds: Reads[Date], wts: Writes[Date]) = {

    "return 200 for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("day", "01"), ("month", "01"), ("year", "2018"))
      setCacheValue(cacheKey, new LocalDate(2018, 1, 1))
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val value = Date(1, 1, 2018)
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("day", "01"), ("month", "01"), ("year", "2018"))
      setCacheValue(cacheKey, new LocalDate(2018, 1, 1))
      await (createController().onSubmit(wts)(fakePostRequest))
      verifyValueIsCached(cacheKey, value)
    }

    "return bad request on submit with invalid data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("day", value), ("month", value), ("month", value))
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data ia submitted" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("day", value), ("month", value), ("month", value))
      val result = createController().onSubmit(wts)(fakePostRequest)
      val valueMap = Map("day" -> value, "month" -> value, "year" -> value)
      contentAsString(result) shouldBe createView(Some(valueMap)).toString
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
      val value = new Date(day, month, year)
      setCacheValue(cacheKey, value)
      val result = createController().onPageLoad(rds)(fakeRequest)

      val valueMap = Map("day" -> day.toString, "month" -> month.toString, "year" -> year.toString)
      contentAsString(result) shouldBe createView(Some(valueMap)).toString
    }
  }

  def nonStartingDateController(createController: () => ControllerBase[Date])(rds: Reads[Date]) = {
    "On a page load with an expired session, return a redirect to an expired session page" in {
      expireSessionConnector()

      val rds = Date.dateReads

      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
