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
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_transfer_amount
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class ChargeableTransferAmountControllerSpec extends SimpleControllerSpecBase {

  "Chargeable Transfer Amount Controller" must {

    def createView = (value: Option[Int]) => value match {
      case None => chargeable_transfer_amount(frontendAppConfig)(fakeRequest, messages)
      case Some(v) => chargeable_transfer_amount(frontendAppConfig, Some(NonNegativeIntForm().fill(v)))(fakeRequest, messages)
    }

    def createController = () => new ChargeableTransferAmountController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = 123

    val valuesToCacheBeforeSubmission = Map(Constants.grossEstateValueId -> testValue)

    behave like rnrbController(createController, createView, Constants.chargeableTransferAmountId,
      testValue, valuesToCacheBeforeSubmission)(Reads.IntReads, Writes.IntWrites)

    "return bad request on submit with a value greater than the previously saved Gross Estate Value" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString))
      setCacheValue(Constants.grossEstateValueId, testValue - 1)
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
