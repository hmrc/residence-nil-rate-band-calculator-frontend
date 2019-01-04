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

import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_assets

class GrossingUpOnEstateAssetsControllerSpec extends SimpleControllerSpecBase {

  val messageKey = "grossing_up_on_estate_assets.error.required"
  val messageKeyPrefix = "grossing_up_on_estate_assets"

  "Grossing Up On Estate Assets Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => grossing_up_on_estate_assets(BooleanForm.apply(messageKey), answerRows = Seq())(fakeRequest, messages, applicationProvider)
        case Some(v) => grossing_up_on_estate_assets(BooleanForm(messageKey).bind(v), Seq())(fakeRequest, messages, applicationProvider)
      }
    }

    def createController = () => new GrossingUpOnEstateAssetsController(messagesApi, mockSessionConnector, navigator, applicationProvider)

    val testValue = false

    behave like
      rnrbController[Boolean](createController, createView, Constants.grossingUpOnEstateAssetsId, messageKeyPrefix, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

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
           Constants.assetsPassingToDirectDescendantsId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
