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

import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_disposal

class DateOfDisposalControllerSpec extends DateControllerSpecBase {
  "Date Of Disposal Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.AnyDownsizingAllowanceController.onPageLoad().url
      value match {
        case None => date_of_disposal(frontendAppConfig, url)(fakeRequest, messages)
        case Some(v) => date_of_disposal(frontendAppConfig, url, Some(DateForm().bind(v)))(fakeRequest, messages)
      }
    }

    def createController = () => new DateOfDisposalController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    behave like rnrbDateController(createController, createView, Constants.dateOfDisposalId)(Date.dateReads, Date.dateWrites)

    behave like nonStartingDateController(createController)(Date.dateReads)
  }

}
