/*
 * Copyright 2016 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_closely_inherited
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class PercentageCloselyInheritedControllerSpec extends SimpleControllerSpecBase {

  "Percentage Closely Inherited Controller" must {

    def createView = (value: Option[Int]) => value match {
      case None => percentage_closely_inherited(frontendAppConfig)(fakeRequest, messages)
      case Some(v) => percentage_closely_inherited(frontendAppConfig, Some(NonNegativeIntForm().fill(v)))(fakeRequest, messages)
    }

    def createController = () => new PercentageCloselyInheritedController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = 123

    behave like rnrbController(createController, createView, Constants.percentageCloselyInheritedId, testValue)(Reads.IntReads, Writes.IntWrites)
  }
}
