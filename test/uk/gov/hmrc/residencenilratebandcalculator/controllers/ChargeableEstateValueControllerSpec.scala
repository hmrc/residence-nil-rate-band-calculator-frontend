/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_estate_value

class ChargeableEstateValueControllerSpec extends SimpleControllerSpecBase {

  val errorKeyBlank = "chargeable_estate_value.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "chargeable_estate_value.error.non_numeric"
  val messageKeyPrefix = "chargeable_estate_value"

  "Chargeable Estate Value Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => chargeable_estate_value(frontendAppConfig, NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), answerRows = Seq())(fakeRequest, messages, applicationProvider, localPartialRetriever)
        case Some(v) => chargeable_estate_value(frontendAppConfig,
          NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric).bind(v), Seq())(fakeRequest, messages, applicationProvider, localPartialRetriever)
      }
    }

    def createController = () => new ChargeableEstateValueController(frontendAppConfig, messagesApi, mockSessionConnector, navigator, applicationProvider, localPartialRetriever)

    val testValue = 123

    val valuesToCacheBeforeSubmission = Map(Constants.valueOfEstateId -> testValue)

    behave like rnrbController(createController, createView, Constants.chargeableEstateValueId,
      messageKeyPrefix, testValue, valuesToCacheBeforeSubmission)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController,
      List(Constants.dateOfDeathId,
           Constants.partOfEstatePassingToDirectDescendantsId,
           Constants.valueOfEstateId))(Reads.IntReads, Writes.IntWrites)

    "return bad request on submit with a value greater than the previously saved Value Of Estate" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString))
      setCacheValue(Constants.valueOfEstateId, testValue - 1)
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
