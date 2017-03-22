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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.part_of_estate_passing_to_direct_descendants

class PartOfEstatePassingToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  "Part Of Estate Passing To Direct Descendants Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val backUrl = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.DateOfDeathController.onPageLoad().url

      value match {
        case None => part_of_estate_passing_to_direct_descendants(frontendAppConfig, backUrl, answerRows = Seq())(fakeRequest, messages)
        case Some(v) => part_of_estate_passing_to_direct_descendants(frontendAppConfig, backUrl, Some(BooleanForm().bind(v)), Seq())(fakeRequest, messages)
      }
    }

    def createController = () => new PartOfEstatePassingToDirectDescendantsController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = true

    behave like
      rnrbController[Boolean](createController, createView, Constants.partOfEstatePassingToDirectDescendantsId, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

    behave like nonStartingController[Boolean](createController, answerRowConstants = List(Constants.dateOfDeathId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}