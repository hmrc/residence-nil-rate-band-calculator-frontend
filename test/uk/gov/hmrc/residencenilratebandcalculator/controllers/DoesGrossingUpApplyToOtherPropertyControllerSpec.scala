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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.does_grossing_up_apply_to_other_property

class DoesGrossingUpApplyToOtherPropertyControllerSpec extends SimpleControllerSpecBase {

  "Does Grossing Up Apply To Other Property Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val backUrl = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad().url

      value match {
        case None => does_grossing_up_apply_to_other_property(frontendAppConfig, backUrl, answerRows = Seq())(fakeRequest, messages)
        case Some(v) => does_grossing_up_apply_to_other_property(frontendAppConfig, backUrl, Some(BooleanForm().bind(v)), Seq())(fakeRequest, messages)
      }
    }

    def createController = () => new DoesGrossingUpApplyToOtherPropertyController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = false

    behave like
      rnrbController[Boolean](createController, createView, Constants.doesGrossingUpApplyToOtherPropertyId, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

    behave like nonStartingController[Boolean](createController,
      List(Constants.dateOfDeathId,
           Constants.partOfEstatePassingToDirectDescendantsId,
           Constants.valueOfEstateId,
           Constants.chargeableEstateValueId,
           Constants.estateHasPropertyId,
           Constants.propertyValueId,
           Constants.anyPropertyCloselyInheritedId,
           Constants.percentageCloselyInheritedId,
           Constants.chargeableValueOfResidenceId,
           Constants.anyBroughtForwardAllowanceId,
           Constants.broughtForwardAllowanceId,
           Constants.anyDownsizingAllowanceId,
           Constants.dateOfDisposalId,
           Constants.valueOfDisposedPropertyId,
           Constants.anyAssetsPassingToDirectDescendantsId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
