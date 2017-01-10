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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_downsizing_allowance

class AnyDownsizingAllowanceControllerSpec extends SimpleControllerSpecBase {

  def createView = (value: Option[Boolean]) => value match {
    case None => any_downsizing_allowance(frontendAppConfig)(fakeRequest, messages)
    case Some(v) => any_downsizing_allowance(frontendAppConfig, Some(BooleanForm().fill(v)))(fakeRequest, messages)
  }

  def createController = () => new AnyDownsizingAllowanceController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

  val testValue = true

  behave like rnrbController(createController, createView, Constants.anyDownsizingAllowanceId, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

}
