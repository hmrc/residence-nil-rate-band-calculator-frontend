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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_assets_passing_to_direct_descendants

class AnyAssetsPassingToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  "Any Assets Passing to Direct Descendants Controller" must {

    val propertyValue = 123456
    val formattedPropertyValue = Some("£123,456.00")

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.ValueOfDisposedPropertyController.onPageLoad().url

      value match {
        case None =>
          any_assets_passing_to_direct_descendants(frontendAppConfig, url, None, Seq(), formattedPropertyValue)(fakeRequest, messages)
        case Some(v) =>
          any_assets_passing_to_direct_descendants(frontendAppConfig, url, Some(BooleanForm().bind(v)), Seq(), formattedPropertyValue)(fakeRequest, messages)
      }
    }

    def createController = () => new AnyAssetsPassingToDirectDescendantsController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = true

    val valuesToCacheBeforeLoad = Map(Constants.propertyValueId -> propertyValue)

    behave like rnrbController[Boolean](createController, createView, Constants.anyAssetsPassingToDirectDescendantsId, testValue,
      valuesToCacheBeforeLoad = valuesToCacheBeforeLoad)(Reads.BooleanReads, Writes.BooleanWrites)

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
        Constants.valueOfDisposedPropertyId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
