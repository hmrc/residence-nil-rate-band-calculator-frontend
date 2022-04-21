/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.common.CommonPlaySpec
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_assets_passing

class ValueOfAssetsPassingControllerSpec extends NewSimpleControllerSpecBase with CommonPlaySpec{

  val errorKeyBlank = "value_of_assets_passing.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"
  val messageKeyPrefix = "value_of_assets_passing"

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val value_of_assets_passing = injector.instanceOf[value_of_assets_passing]
  "Value Of Assets Passing Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => value_of_assets_passing(NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge), formattedPropertyValue = None)(fakeRequest, messages)
        case Some(v) => value_of_assets_passing(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v), None)(fakeRequest, messages)
      }
    }

    def createController = () => new ValueOfAssetsPassingController(messagesControllerComponents, mockSessionConnector, navigator, value_of_assets_passing)

    val testValue = 123

    val valuesToCacheBeforeSubmission = Map(Constants.valueOfEstateId -> testValue)

    behave like rnrbController[Int](createController, createView, Constants.valueOfAssetsPassingId,
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
        Constants.chargeablePropertyValueId,
        Constants.transferAnyUnusedThresholdId,
        Constants.valueBeingTransferredId,
        Constants.claimDownsizingThresholdId,
        Constants.datePropertyWasChangedId,
        Constants.valueOfChangedPropertyId,
        Constants.assetsPassingToDirectDescendantsId,
        Constants.grossingUpOnEstateAssetsId))(Reads.IntReads, Writes.IntWrites)

    "return bad request on submit with a value greater than the previously saved Value Of Estate" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString))
      setCacheValue(Constants.valueOfEstateId, testValue - 1)
      val result = createController().onSubmit(Writes.IntWrites)(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return the correct view when provided with answers including a valid property value" in {
      val result = createController().view(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge), new UserAnswers(CacheMap("id", Map(Constants.propertyValueId -> Json.toJson(1)))))(fakeRequest)
      result shouldBe value_of_assets_passing(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge), Some(CurrencyFormatter.format(1)))(fakeRequest,
        messages)
    }
  }
}
