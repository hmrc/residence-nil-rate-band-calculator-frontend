/*
 * Copyright 2016 HM Revenue & Customs
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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks

trait IntControllerSpecBase extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]
  def messagesApi = injector.instanceOf[MessagesApi]
  def messages = messagesApi.preferred(fakeRequest)

  def rnrbController(createController: () => IntControllerBase,
                     createView: (Option[Int]) => HtmlFormat.Appendable,
                     cacheKey: String) = {

    "return 200 for a GET" in {
      val result = createController().onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the Gross Estate View for a GET" in {
      val result = createController().onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", "100"))
      val result = createController().onSubmit()(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val value = 100
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value.toString))
      createController().onSubmit()(fakePostRequest)
      verifyValueIsCached(cacheKey, value)
    }

    "return bad request on submit with invalid data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = createController().onSubmit()(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data ia submitted" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = createController().onSubmit()(fakePostRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "not store invalid submitted data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      createController().onSubmit()(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      val value = 123
      setCacheValue(cacheKey, value)
      val result = createController().onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe createView(Some(value)).toString
    }
  }
}
