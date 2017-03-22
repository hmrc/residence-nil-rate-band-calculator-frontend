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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_estate

class ValueOfEstateControllerSpec extends SimpleControllerSpecBase {

  "Value Of Estate Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.PartOfEstatePassingToDirectDescendantsController.onPageLoad().url

      value match {
        case None => value_of_estate(frontendAppConfig, url, answerRows = Seq())(fakeRequest, messages)
        case Some(v) => value_of_estate(frontendAppConfig, url, Some(NonNegativeIntForm().bind(v)), Seq())(fakeRequest, messages)
      }
    }

    def createController = () => new ValueOfEstateController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = 123

    behave like rnrbController[Int](createController, createView, Constants.valueOfEstateId, testValue)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController, List(Constants.dateOfDeathId, Constants.partOfEstatePassingToDirectDescendantsId))(Reads.IntReads, Writes.IntWrites)
  }
}
