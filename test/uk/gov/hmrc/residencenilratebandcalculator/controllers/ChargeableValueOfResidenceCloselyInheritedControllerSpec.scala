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
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_value_of_residence_closely_inherited

class ChargeableValueOfResidenceCloselyInheritedControllerSpec extends SimpleControllerSpecBase {

  "ChargeableValueOfResidenceCloseInheritedController"  must {

    val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.ChargeableValueOfResidenceController.onPageLoad().url

  def createView = (value: Option[Map[String, String]]) => value match {
    case None => chargeable_value_of_residence_closely_inherited(frontendAppConfig, url)(fakeRequest, messages)
    case Some(v) => chargeable_value_of_residence_closely_inherited(frontendAppConfig, url, Some(NonNegativeIntForm().bind(v)))(fakeRequest, messages)
  }

  def createController = () => new ChargeableValueOfResidenceCloselyInheritedController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

  val testValue = 123

  val valuesToCacheBeforeSubmission = Map(Constants.chargeableValueOfResidenceId -> testValue)

  behave like rnrbController(createController, createView, Constants.chargeableValueOfResidenceCloselyInheritedId,
    testValue, valuesToCacheBeforeSubmission)(Reads.IntReads, Writes.IntWrites)

}

}
