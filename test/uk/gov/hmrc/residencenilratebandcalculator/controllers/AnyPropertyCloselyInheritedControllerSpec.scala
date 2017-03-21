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
import uk.gov.hmrc.residencenilratebandcalculator.forms.AnyPropertyCloselyInheritedForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_property_closely_inherited

class AnyPropertyCloselyInheritedControllerSpec extends SimpleControllerSpecBase {

  "Any Property Closely Inherited Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.PropertyValueController.onPageLoad().url

      value match {
        case None => any_property_closely_inherited(frontendAppConfig, url, answerRows = Seq())(fakeRequest, messages)
        case Some(v) => any_property_closely_inherited(frontendAppConfig, url, Some(AnyPropertyCloselyInheritedForm().bind(v)), Seq())(fakeRequest, messages)
      }
    }

    def createController = () => new AnyPropertyCloselyInheritedController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = Constants.all

    behave like rnrbController[String](createController, createView, Constants.anyPropertyCloselyInheritedId, testValue)(Reads.StringReads, Writes.StringWrites)

    behave like nonStartingController[String](createController,
      List(Constants.dateOfDeathId,
           Constants.anyEstatePassedToDescendantsId,
           Constants.grossEstateValueId,
           Constants.chargeableTransferAmountId,
           Constants.estateHasPropertyId,
           Constants.propertyValueId))(Reads.StringReads, Writes.StringWrites)
  }
}
