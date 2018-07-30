/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyPassingToDirectDescendantsForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_passing_to_direct_descendants

class PropertyPassingToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  val messageKeyPrefix = "property_passing_to_direct_descendants"

  "Property Passing To Direct Descendants Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => property_passing_to_direct_descendants(PropertyPassingToDirectDescendantsForm.apply(),answerRows = Seq())(fakeRequest, messages, applicationProvider)
        case Some(v) => property_passing_to_direct_descendants(PropertyPassingToDirectDescendantsForm().bind(v), Seq())(fakeRequest, messages, applicationProvider)
      }
    }

    def createController = () => new PropertyPassingToDirectDescendantsController(messagesApi, mockSessionConnector, navigator, applicationProvider)

    val testValue = Constants.all

    behave like rnrbController[String](createController, createView, Constants.propertyPassingToDirectDescendantsId, messageKeyPrefix, testValue)(Reads.StringReads, Writes.StringWrites)

    behave like nonStartingController[String](createController,
      List(Constants.dateOfDeathId,
           Constants.partOfEstatePassingToDirectDescendantsId,
           Constants.valueOfEstateId,
           Constants.chargeableEstateValueId,
           Constants.propertyInEstateId,
           Constants.propertyValueId))(Reads.StringReads, Writes.StringWrites)
  }
}
