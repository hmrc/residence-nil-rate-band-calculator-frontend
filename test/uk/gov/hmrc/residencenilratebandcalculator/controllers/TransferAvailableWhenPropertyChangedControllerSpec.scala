/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.transfer_available_when_property_changed

class TransferAvailableWhenPropertyChangedControllerSpec extends SimpleControllerSpecBase {

  "Transfer Available When Property Changed Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.AssetsPassingToDirectDescendantsController.onPageLoad().url

      value match {
        case None => transfer_available_when_property_changed(frontendAppConfig, url, answerRows = Seq())(fakeRequest, messages)
        case Some(v) => transfer_available_when_property_changed(frontendAppConfig, url, Some(BooleanForm().bind(v)), Seq())(fakeRequest, messages)
      }
    }

    def createController = () => new TransferAvailableWhenPropertyChangedController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = true

    behave like rnrbController(createController, createView, Constants.transferAvailableWhenPropertyChangedId, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

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
           Constants.anyBroughtForwardAllowanceId,
           Constants.broughtForwardAllowanceId,
           Constants.claimDownsizingThresholdId,
           Constants.datePropertyWasChangedId,
           Constants.valueOfChangedPropertyId,
           Constants.anyAssetsPassingToDirectDescendantsId,
           Constants.grossingUpOnEstateAssetsId,
           Constants.assetsPassingToDirectDescendantsId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
