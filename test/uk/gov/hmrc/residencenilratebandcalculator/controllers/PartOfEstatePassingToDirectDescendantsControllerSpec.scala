/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.part_of_estate_passing_to_direct_descendants

class PartOfEstatePassingToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  val messageKey = "part_of_estate_passing_to_direct_descendants.error.required"
  val messageKeyPrefix = "part_of_estate_passing_to_direct_descendants"

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  "Part Of Estate Passing To Direct Descendants Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => part_of_estate_passing_to_direct_descendants(BooleanForm.apply(messageKey), answerRows = Seq())(fakeRequest, messages, mockConfig)
        case Some(v) => part_of_estate_passing_to_direct_descendants(BooleanForm(messageKey).bind(v), Seq())(fakeRequest, messages, mockConfig)
      }
    }

    def createController = () => new PartOfEstatePassingToDirectDescendantsController(messagesControllerComponents, mockSessionConnector, navigator, mockConfig)

    val testValue = true

    behave like
      rnrbController[Boolean](createController, createView, Constants.partOfEstatePassingToDirectDescendantsId, messageKeyPrefix, testValue)(Reads.BooleanReads, Writes.BooleanWrites)

    behave like nonStartingController[Boolean](createController, answerRowConstants = List(Constants.dateOfDeathId))(Reads.BooleanReads, Writes.BooleanWrites)
  }
}
