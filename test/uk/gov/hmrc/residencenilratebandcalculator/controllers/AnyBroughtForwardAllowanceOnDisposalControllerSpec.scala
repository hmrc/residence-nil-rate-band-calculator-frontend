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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_brought_forward_allowance_on_disposal

class AnyBroughtForwardAllowanceOnDisposalControllerSpec extends SimpleControllerSpecBase {

  "Any Brought Forward Allowance On Disposal Controller" must {

    def createView = (value: Option[Boolean]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.AssetsPassingToDirectDescendantsController.onPageLoad().url

      value match {
        case None => any_brought_forward_allowance_on_disposal(frontendAppConfig, url)(fakeRequest, messages)
        case Some(v) => any_brought_forward_allowance_on_disposal(frontendAppConfig, url, Some(BooleanForm().fill(v)))(fakeRequest, messages)
      }
    }

    def createController = () => new AnyBroughtForwardAllowanceOnDisposalController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = true

    behave like rnrbController(createController, createView, Constants.anyBroughtForwardAllowanceOnDisposalId, testValue)(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
