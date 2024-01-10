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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.claim_downsizing_threshold

class ClaimDownsizingThresholdControllerSpec extends NewSimpleControllerSpecBase {

  val messageKey = "claim_downsizing_threshold.error.required"
  val messageKeyPrefix = "claim_downsizing_threshold"

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val claim_downsizing_threshold: claim_downsizing_threshold = fakeApplication.injector.instanceOf[claim_downsizing_threshold]

  def createView: Option[Map[String, String]] => HtmlFormat.Appendable = {
    case None => claim_downsizing_threshold(BooleanForm.apply(messageKey))(fakeRequest, messages)
    case Some(v) => claim_downsizing_threshold(BooleanForm(messageKey).bind(v))(fakeRequest, messages)
  }

  def createController: () => ClaimDownsizingThresholdController = () =>
    new ClaimDownsizingThresholdController(messagesControllerComponents, mockSessionConnector, navigator, claim_downsizing_threshold)

  val testValue = true

  behave like rnrbController(
    createController, createView, Constants.claimDownsizingThresholdId, messageKeyPrefix, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

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
      Constants.valueBeingTransferredId))(Reads.BooleanReads, Writes.BooleanWrites)
}
