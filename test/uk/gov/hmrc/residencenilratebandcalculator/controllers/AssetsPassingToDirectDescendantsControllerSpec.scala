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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.assets_passing_to_direct_descendants

class AssetsPassingToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  val messageKey = "assets_passing_to_direct_descendants.error.required"

  "Assets Passing To Direct Descendants Controller" must {

    val propertyValue = 123456
    val formattedPropertyValue = Some("£123,456.00")

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.ValueOfChangedPropertyController.onPageLoad().url

      value match {
        case None =>
          assets_passing_to_direct_descendants(frontendAppConfig, url, None, Seq(), formattedPropertyValue)(fakeRequest, messages)
        case Some(v) =>
          assets_passing_to_direct_descendants(frontendAppConfig, url, Some(BooleanForm(messageKey).bind(v)), Seq(), formattedPropertyValue)(fakeRequest, messages)
      }
    }

    def createController = () => new AssetsPassingToDirectDescendantsController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = true

    val valuesToCacheBeforeLoad = Map(Constants.propertyValueId -> propertyValue)

    behave like rnrbController[Boolean](createController, createView, Constants.assetsPassingToDirectDescendantsId, testValue,
      valuesToCacheBeforeLoad = valuesToCacheBeforeLoad)(Reads.BooleanReads, Writes.BooleanWrites)

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
        Constants.valueOfChangedPropertyId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
