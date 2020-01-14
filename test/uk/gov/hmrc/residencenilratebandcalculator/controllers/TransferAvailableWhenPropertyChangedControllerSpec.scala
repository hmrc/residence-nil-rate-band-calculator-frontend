/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.transfer_available_when_property_changed

class TransferAvailableWhenPropertyChangedControllerSpec extends SimpleControllerSpecBase {

  val messageKey = "transfer_available_when_property_changed.error.required"
  val messageKeyPrefix = "transfer_available_when_property_changed"

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  "Transfer Available When Property Changed Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => transfer_available_when_property_changed(BooleanForm.apply(messageKey),answerRows = Seq())(fakeRequest, messages, mockConfig)
        case Some(v) => transfer_available_when_property_changed(BooleanForm(messageKey).bind(v), Seq())(fakeRequest, messages, mockConfig)
      }
    }

    def createController = () => new TransferAvailableWhenPropertyChangedController(messagesControllerComponents, mockSessionConnector, navigator, mockConfig)

    val testValue = true

    behave like rnrbController(createController, createView, Constants.transferAvailableWhenPropertyChangedId, messageKeyPrefix,testValue)(Reads.BooleanReads, Writes.BooleanWrites)

    behave like nonStartingController[Boolean](createController,
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
           Constants.grossingUpOnEstateAssetsId,
           Constants.valueOfAssetsPassingId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
