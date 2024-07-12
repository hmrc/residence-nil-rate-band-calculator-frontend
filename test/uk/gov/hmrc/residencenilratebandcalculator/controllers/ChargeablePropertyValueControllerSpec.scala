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
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.DefaultMessagesControllerComponents
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.common.CommonPlaySpec
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_property_value

class ChargeablePropertyValueControllerSpec extends NewSimpleControllerSpecBase with CommonPlaySpec {

  val errorKeyBlank = "chargeable_property_value.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "chargeable_property_value.error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"
  val messageKeyPrefix = "chargeable_property_value"

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val chargeable_property_value: chargeable_property_value = fakeApplication.injector.instanceOf[chargeable_property_value]

  "Chargeable Property Value Controller" must {
    def createView: Option[Map[String, String]] => HtmlFormat.Appendable = {
      case None => chargeable_property_value(
        NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge))(fakeRequest, messages)
      case Some(v) => chargeable_property_value(
        NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v))(fakeRequest, messages)
    }

    def createController: () => ChargeablePropertyValueController = () =>
      new ChargeablePropertyValueController(messagesControllerComponents, mockSessionConnector, navigator, chargeable_property_value)

    val testValue = 123

    val valuesToCacheBeforeSubmission = Map(Constants.propertyValueId -> testValue)

    behave like rnrbController(createController, createView, Constants.chargeablePropertyValueId,
      messageKeyPrefix, testValue, valuesToCacheBeforeSubmission)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController,
      List(Constants.dateOfDeathId,
           Constants.partOfEstatePassingToDirectDescendantsId,
           Constants.valueOfEstateId,
           Constants.chargeableEstateValueId,
           Constants.propertyInEstateId,
           Constants.propertyValueId,
           Constants.propertyPassingToDirectDescendantsId,
           Constants.percentagePassedToDirectDescendantsId))(Reads.IntReads, Writes.IntWrites)

    "return bad request on submit with a value greater than the previously saved Property Value" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString)).withMethod("POST")
      setCacheValue(Constants.propertyValueId, testValue - 1)
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) mustBe Status.BAD_REQUEST
    }
  }
}
