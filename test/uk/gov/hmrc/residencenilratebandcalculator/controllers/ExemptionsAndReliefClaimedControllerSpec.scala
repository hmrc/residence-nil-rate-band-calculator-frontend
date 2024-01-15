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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.exemptions_and_relief_claimed

class ExemptionsAndReliefClaimedControllerSpec extends NewSimpleControllerSpecBase {

  val messageKey = "exemptions_and_relief_claimed.error.required"
  val messageKeyPrefix = "exemptions_and_relief_claimed"

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val exemptions_and_relief_claimed: exemptions_and_relief_claimed = injector.instanceOf[exemptions_and_relief_claimed]
  "Exemptions And Relief Claimed Controller" must {

    def createView: Option[Map[String, String]] => HtmlFormat.Appendable = {
      case None => exemptions_and_relief_claimed(BooleanForm.apply(messageKey))(fakeRequest, messages)
      case Some(v) => exemptions_and_relief_claimed(BooleanForm(messageKey).bind(v))(fakeRequest, messages)
    }

    def createController: () => ExemptionsAndReliefClaimedController = () =>
      new ExemptionsAndReliefClaimedController(messagesControllerComponents, mockSessionConnector, navigator, exemptions_and_relief_claimed)

    val testValue = true

    behave like rnrbController[Boolean](
      createController, createView, Constants.exemptionsAndReliefClaimedId, messageKeyPrefix, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

    behave like nonStartingController[Boolean](createController,
      List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.valueOfEstateId,
        Constants.chargeableEstateValueId,
        Constants.propertyInEstateId,
        Constants.propertyValueId,
        Constants.propertyPassingToDirectDescendantsId,
        Constants.percentagePassedToDirectDescendantsId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
