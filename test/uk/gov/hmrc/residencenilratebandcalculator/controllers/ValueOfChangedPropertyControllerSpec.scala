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

import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_changed_property

class ValueOfChangedPropertyControllerSpec extends SimpleControllerSpecBase {

  val errorKeyBlank = "value_of_changed_property.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"
  val messageKeyPrefix = "value_of_changed_property"

  "Value Of Changed Property Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => value_of_changed_property(frontendAppConfig, NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge),answerRows = Seq())(fakeRequest, messages, applicationProvider)
        case Some(v) => value_of_changed_property(frontendAppConfig, NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v), Seq())(fakeRequest, messages, applicationProvider)
      }
    }

    def createController = () => new ValueOfChangedPropertyController(frontendAppConfig, messagesApi, mockSessionConnector, navigator, applicationProvider)

    val testValue = 123

    behave like rnrbController(createController, createView, Constants.valueOfChangedPropertyId, messageKeyPrefix,testValue)(Reads.IntReads, Writes.IntWrites)

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
        Constants.datePropertyWasChangedId))(Reads.IntReads, Writes.IntWrites)
  }
}
