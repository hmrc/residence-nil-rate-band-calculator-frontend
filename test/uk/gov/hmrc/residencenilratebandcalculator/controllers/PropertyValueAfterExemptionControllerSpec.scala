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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyValueAfterExemptionForm
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.PropertyValueAfterExemption
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value_after_exemption
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

class PropertyValueAfterExemptionControllerSpec extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector {

  val propertyValue = 456

  val propertyValueCloselyInherited = 123

  val testValue = PropertyValueAfterExemption(propertyValue, propertyValueCloselyInherited)

  val cacheKey = Constants.propertyValueAfterExemptionId

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  def createController = () => new PropertyValueAfterExemptionController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

  def createView = (values: Option[PropertyValueAfterExemption]) => {
    val backUrl = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.DoesGrossingUpApplyController.onPageLoad().url

    values match {
      case None => property_value_after_exemption(frontendAppConfig, backUrl)(fakeRequest, messages)
      case Some(v) => property_value_after_exemption(frontendAppConfig, backUrl, Some(PropertyValueAfterExemptionForm().fill(v)))(fakeRequest, messages)
    }
  }

  def reads = PropertyValueAfterExemption.propertyValueAfterExemptionReads

  def writes = PropertyValueAfterExemption.propertyValueAfterExemptionWrites

  val valuesToCacheBeforeSubmission = Map(Constants.propertyValueId -> propertyValue)

  "Property Value After Exemption Controller" must {

    "return 200 for a GET" in {
      val result = createController().onPageLoad(reads)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the view for a GET" in {
      val result = createController().onPageLoad(reads)(fakeRequest)
      contentAsString(result) shouldBe createView(None).toString
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(
        ("value", propertyValue.toString),
        ("valueCloselyInherited", propertyValueCloselyInherited.toString))
      for (v <- valuesToCacheBeforeSubmission) setCacheValue(v._1, v._2)
      setCacheValue(cacheKey, testValue)
      val result = createController().onSubmit(writes)(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(
        ("value", propertyValue.toString),
        ("valueCloselyInherited", propertyValueCloselyInherited.toString))
      for (v <- valuesToCacheBeforeSubmission) setCacheValue(v._1, v._2)
      setCacheValue(cacheKey, testValue)
      await (createController().onSubmit(writes)(fakePostRequest))
      verifyValueIsCached(cacheKey, testValue)
    }

    "return bad request on submit with invalid data" in {
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = createController().onSubmit(writes)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "not store invalid submitted data" in {
      val value = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      createController().onSubmit(writes)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      setCacheValue(cacheKey, testValue)
      val result = createController().onPageLoad(reads)(fakeRequest)
      contentAsString(result) shouldBe createView(Some(testValue)).toString
    }

    "return bad request on submit with a value greater than the previously saved Property Value" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(
        ("value", propertyValue.toString),
        ("valueCloselyInherited", propertyValueCloselyInherited.toString))
      setCacheValue(Constants.propertyValueId, propertyValue - 1)
      val result = createController().onSubmit(writes)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onPageLoad(reads)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
