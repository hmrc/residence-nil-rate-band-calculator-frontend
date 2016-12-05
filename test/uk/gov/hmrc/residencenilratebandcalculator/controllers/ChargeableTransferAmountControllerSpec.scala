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
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_transfer_amount

class ChargeableTransferAmountControllerSpec extends ControllerSpecBase with MockSessionConnector {

  "Chargeable Transfer Amount Controller" must {

    "return 200 for a GET" in {
      val result = new GrossEstateValueController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the Chargeable Transfer Amount View for a GET" in {
      val result = new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe chargeable_transfer_amount(frontendAppConfig)(fakeRequest, messages).toString
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", "100"))
      val result = new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onSubmit()(fakePostRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val value = 100
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value.toString))
      new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onSubmit()(fakePostRequest)
      verifyValueIsCached(value)
    }

    "return bad request on submit with invalid data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onSubmit()(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "not store invalid submitted data" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onSubmit()(fakePostRequest)
      verifyValueIsNotCached()
    }

    "return form with errors when invalid data ia submitted" in {
      val value = "not a number"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value))
      val result = new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onSubmit()(fakePostRequest)
      contentAsString(result) shouldBe chargeable_transfer_amount(frontendAppConfig)(fakeRequest, messages).toString
    }

    "get a previously stored value from keystore" in {
      val value = 123
      setCacheValue("ChargeableTransferAmount", value)
      val result = new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe
        chargeable_transfer_amount(frontendAppConfig, Some(NonNegativeIntForm().fill(value)))(fakeRequest, messages).toString
    }
  }
}
