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
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.estate_has_property

class EstateHasPropertyControllerSpec extends SimpleControllerSpecBase {

  "Estate Has Property Controller" must {

    def createView = (value: Option[Boolean]) => value match {
      case None => estate_has_property(frontendAppConfig)(fakeRequest, messages)
      case Some(v) => estate_has_property(frontendAppConfig, Some(BooleanForm().fill(v)))(fakeRequest, messages)
    }

    def createController = () => new EstateHasPropertyController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val validData = true

    val validRequestBody = Map("value" -> validData.toString)

    val invalidRequestBody = Map("value" -> "invalid data")

    def cacheValue = () => setCacheValue[Boolean](Constants.estateHasPropertyId, validData)

    behave like rnrbController(createController, createView, Constants.estateHasPropertyId, validData, validRequestBody,
      invalidRequestBody, cacheValue, cacheValue)(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
