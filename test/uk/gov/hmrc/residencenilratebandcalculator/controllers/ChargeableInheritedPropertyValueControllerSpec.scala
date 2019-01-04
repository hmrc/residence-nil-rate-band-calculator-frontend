/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_inherited_property_value

class ChargeableInheritedPropertyValueControllerSpec extends SimpleControllerSpecBase {

  val errorKeyBlank = "chargeable_inherited_property_value.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"
  val messageKeyPrefix = "chargeable_inherited_property_value"

  "Chargeable Inherited Property Value Controller"  must {

    def createView = (value: Option[Map[String, String]]) => value match {
      case None => chargeable_inherited_property_value(NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge), answerRows = Seq())(fakeRequest, messages, applicationProvider)
      case Some(v) => chargeable_inherited_property_value(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v), Seq())(fakeRequest, messages, applicationProvider)
    }

    def createController = () => new ChargeableInheritedPropertyValueController(messagesApi, mockSessionConnector, navigator, applicationProvider)

    val testValue = 123

    val valuesToCacheBeforeSubmission = Map(Constants.chargeablePropertyValueId -> testValue)

    behave like rnrbController(createController, createView, Constants.chargeableInheritedPropertyValueId,
      messageKeyPrefix, testValue, valuesToCacheBeforeSubmission)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController,
      List(Constants.dateOfDeathId,
           Constants.partOfEstatePassingToDirectDescendantsId,
           Constants.valueOfEstateId,
           Constants.chargeableEstateValueId,
           Constants.propertyInEstateId,
           Constants.propertyValueId,
           Constants.propertyPassingToDirectDescendantsId,
           Constants.percentagePassedToDirectDescendantsId,
           Constants.chargeablePropertyValueId))(Reads.IntReads, Writes.IntWrites)

    "return bad request on submit with a value greater than the previously saved Chargeable Property Value" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString))
      setCacheValue(Constants.chargeablePropertyValueId, testValue - 1)
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }
  }
}
