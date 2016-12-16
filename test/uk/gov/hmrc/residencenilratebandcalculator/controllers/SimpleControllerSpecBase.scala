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
import play.api.libs.json.{Reads, Writes}
import play.api.i18n._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks

import scala.reflect.ClassTag

trait SimpleControllerSpecBase extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  def rnrbController[A: ClassTag](createController: () => SimpleControllerBase[A],
                                  createView: (Option[A]) => HtmlFormat.Appendable,
                                  cacheKey: String,
                                  validData: A,
                                  validRequestBody: Map[String, String],
                                  invalidRequestBody: Map[String, String],
                                  setCacheValue: () => Any,
                                  setCacheFormValue: () => Any)
                                 (rds: Reads[A], wts: Writes[A]) = {

    "return 200 for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val result = createController().onPageLoad(rds)(fakeRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(validRequestBody.toSeq: _*)
      setCacheValue()
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(validRequestBody.toSeq: _*)
      createController().onSubmit(wts)(fakePostRequest)
      verifyValueIsCached(cacheKey, validData)
    }

    "return bad request on submit with invalid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(invalidRequestBody.toSeq: _*)
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data is submitted" in {
      pending
      // This test should checking against a form with errors, not None
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(invalidRequestBody.toSeq: _*)
      val result = createController().onSubmit(wts)(fakePostRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "not store invalid submitted data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(invalidRequestBody.toSeq: _*)
      createController().onSubmit(wts)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      setCacheFormValue()
      val result = createController().onPageLoad(rds)(fakeRequest)
      contentAsString(result) shouldBe createView(Some(validData)).toString
    }
  }
}
