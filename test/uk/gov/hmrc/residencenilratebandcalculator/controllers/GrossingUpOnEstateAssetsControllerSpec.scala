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

import play.api.libs.json.{Reads, Writes}
import play.api.mvc.DefaultMessagesControllerComponents
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_assets

class GrossingUpOnEstateAssetsControllerSpec extends NewSimpleControllerSpecBase {

  val messageKey = "grossing_up_on_estate_assets.error.required"
  val messageKeyPrefix = "grossing_up_on_estate_assets"

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val grossing_up_on_estate_assets: grossing_up_on_estate_assets = injector.instanceOf[grossing_up_on_estate_assets]
  "Grossing Up On Estate Assets Controller" must {

    def createView: Option[Map[String, String]] => HtmlFormat.Appendable = {
      case None => grossing_up_on_estate_assets(BooleanForm.apply(messageKey))(fakeRequest, messages)
      case Some(v) => grossing_up_on_estate_assets(BooleanForm(messageKey).bind(v))(fakeRequest, messages)
    }

    def createController: () => GrossingUpOnEstateAssetsController = () =>
      new GrossingUpOnEstateAssetsController(messagesControllerComponents, mockSessionConnector, navigator, grossing_up_on_estate_assets)

    val testValue = false

    behave like rnrbController[Boolean](
      createController, createView, Constants.grossingUpOnEstateAssetsId, messageKeyPrefix, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

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
