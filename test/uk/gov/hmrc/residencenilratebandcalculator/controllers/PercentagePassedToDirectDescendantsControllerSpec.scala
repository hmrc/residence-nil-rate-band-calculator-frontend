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
import uk.gov.hmrc.residencenilratebandcalculator.forms.PositivePercentForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_passed_to_direct_descendants

class PercentagePassedToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  "Percentage Passed To Direct Descendants Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.PropertyPassingToDirectDescendantsController.onPageLoad().url

      value match {
        case None => percentage_passed_to_direct_descendants(frontendAppConfig, url, answerRows = Seq())(fakeRequest, messages)
        case Some(v) => percentage_passed_to_direct_descendants(frontendAppConfig, url, Some(PositivePercentForm().bind(v)), Seq())(fakeRequest, messages)
      }
    }

    def createController = () => new PercentagePassedToDirectDescendantsController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val testValue = 50

    behave like rnrbController[Int](createController, createView, Constants.percentagePassedToDirectDescendantsId, testValue)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController,
      List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.valueOfEstateId,
        Constants.chargeableEstateValueId,
        Constants.propertyInEstateId,
        Constants.propertyValueId,
        Constants.propertyPassingToDirectDescendantsId))(Reads.IntReads, Writes.IntWrites)
  }
}
