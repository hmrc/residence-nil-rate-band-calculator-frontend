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

import com.google.inject.Provider
import play.api.Application
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_property_was_changed

class DatePropertyWasChangedControllerSpec extends DateControllerSpecBase {
  implicit val appProvider: Provider[Application] = applicationProvider
  
  "Date Property Was Changed Controller" must {

    def createView = (value: Option[Date]) => {
      value match {
        case None => date_property_was_changed(frontendAppConfig, dateOfDownsizingForm, answerRows = Seq())(fakeRequest, messages, applicationProvider)
        case Some(v) => date_property_was_changed(frontendAppConfig, dateOfDownsizingForm.fill(v) , Seq())(fakeRequest, messages, applicationProvider)
      }
    }

    def createController = () => new DatePropertyWasChangedController(frontendAppConfig, messagesApi, mockSessionConnector, navigator, applicationProvider)

    behave like rnrbDateController(createController, createView, Constants.datePropertyWasChangedId)(Date.dateReads, Date.dateWrites)

    behave like nonStartingDateController(createController,
      List(Constants.dateOfDeathId,
           Constants.partOfEstatePassingToDirectDescendantsId,
           Constants.valueOfEstateId,
           Constants.chargeableEstateValueId,
           Constants.propertyInEstateId,
           Constants.propertyValueId,
           Constants.propertyPassingToDirectDescendantsId,
           Constants.percentagePassedToDirectDescendantsId,
           Constants.transferAnyUnusedThresholdId,
           Constants.valueBeingTransferredId,
           Constants.claimDownsizingThresholdId))(Date.dateReads)
  }

}
