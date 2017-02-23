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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value

class GrossEstateValueControllerSpec extends SimpleControllerSpecBase {

  "Gross Estate Value Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.DateOfDeathController.onPageLoad().url

      value match {
        case None => gross_estate_value(frontendAppConfig, url)(fakeRequest, messages)
        case Some(v) => gross_estate_value(frontendAppConfig, url, Some(NonNegativeIntForm().bind(v)))(fakeRequest, messages)
      }
    }

    def createController = () => new GrossEstateValueController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = 123

    behave like rnrbController[Int](createController, createView, Constants.grossEstateValueId, testValue)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController)(Reads.IntReads)
  }
}
