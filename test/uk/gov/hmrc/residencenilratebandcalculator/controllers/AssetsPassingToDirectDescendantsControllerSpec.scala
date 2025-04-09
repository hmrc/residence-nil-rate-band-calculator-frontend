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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.assets_passing_to_direct_descendants

class AssetsPassingToDirectDescendantsControllerSpec extends NewSimpleControllerSpecBase {

  val messageKey       = "assets_passing_to_direct_descendants.error.required"
  val messageKeyPrefix = "assets_passing_to_direct_descendants"

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  val assetsPassingToDirectDescendantsView: assets_passing_to_direct_descendants =
    fakeApplication.injector.instanceOf[assets_passing_to_direct_descendants]

  "Assets Passing To Direct Descendants Controller" must {

    val propertyValue          = 123456
    val formattedPropertyValue = Some("£123,456")

    def createView: Option[Map[String, String]] => HtmlFormat.Appendable = {
      case None =>
        assetsPassingToDirectDescendantsView(BooleanForm(messageKey), formattedPropertyValue)(fakeRequest, messages)
      case Some(v) =>
        assetsPassingToDirectDescendantsView(BooleanForm(messageKey).bind(v), formattedPropertyValue)(
          fakeRequest,
          messages
        )
    }

    def createController: () => AssetsPassingToDirectDescendantsController = () =>
      new AssetsPassingToDirectDescendantsController(
        mockSessionConnector,
        navigator,
        messagesControllerComponents,
        assetsPassingToDirectDescendantsView
      )

    val testValue = true

    val valuesToCacheBeforeLoad = Map(Constants.propertyValueId -> propertyValue)

    behave.like(
      rnrbController[Boolean](
        createController,
        createView,
        Constants.assetsPassingToDirectDescendantsId,
        messageKeyPrefix,
        testValue,
        valuesToCacheBeforeLoad = valuesToCacheBeforeLoad
      )(Reads.BooleanReads, Writes.BooleanWrites)
    )

    behave.like(
      nonStartingController[Boolean](
        createController,
        List(
          Constants.dateOfDeathId,
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
          Constants.valueOfChangedPropertyId
        )
      )(Reads.BooleanReads, Writes.BooleanWrites)
    )
  }

}
