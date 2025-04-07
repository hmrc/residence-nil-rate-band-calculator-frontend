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

import play.api.mvc.DefaultMessagesControllerComponents
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.forms.DatePropertyWasChangedForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_property_was_changed
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

class DatePropertyWasChangedControllerSpec extends DateControllerSpecBase {
  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  val date_property_was_changed: date_property_was_changed = injector.instanceOf[date_property_was_changed]
  val mockValidatedSession: ValidatedSession               = injector.instanceOf[ValidatedSession]

  "Date Property Was Changed Controller" must {

    def createView: Option[Date] => HtmlFormat.Appendable = {
      case None    => date_property_was_changed(datePropertyWasChangedForm(messages))(fakeRequest, messages)
      case Some(v) => date_property_was_changed(datePropertyWasChangedForm(messages).fill(v))(fakeRequest, messages)
    }

    def createController: () => DatePropertyWasChangedController = () =>
      new DatePropertyWasChangedController(
        messagesControllerComponents,
        mockSessionConnector,
        navigator,
        mockValidatedSession,
        date_property_was_changed
      )

    behave.like(
      rnrbDateController(createController, createView, Constants.datePropertyWasChangedId, "datePropertyWasChanged")(
        Date.dateReads,
        Date.dateWrites
      )
    )

    behave.like(
      nonStartingDateController(
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
          Constants.transferAnyUnusedThresholdId,
          Constants.valueBeingTransferredId,
          Constants.claimDownsizingThresholdId
        )
      )
    )
  }

}
