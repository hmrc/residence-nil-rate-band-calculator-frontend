/*
 * Copyright 2019 HM Revenue & Customs
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

  val errorKeyBlank = "value_of_estate.error.blank"
  val errorKeyDecimal = "error.whole_pounds"
  val errorKeyNonNumeric = "value_of_estate.error.non_numeric"
  val errorKeyTooLarge = "error.value_too_large"
  val messageKeyPrefix = "value_of_estate"

  "Value Of Estate Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.PartOfEstatePassingToDirectDescendantsController.onPageLoad().url

      value match {
        case None => value_of_estate(NonNegativeIntForm.apply(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge), answerRows = Seq())(fakeRequest, messages, applicationProvider)
        case Some(v) => value_of_estate(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge).bind(v), Seq())(fakeRequest, messages, applicationProvider)
      }
    }

    
    def createController = () => new ValueOfEstateController(messagesApi, mockSessionConnector, navigator, applicationProvider)

    val testValue = 123

    behave like rnrbController[Int](createController, createView, Constants.valueOfEstateId, messageKeyPrefix,testValue)(Reads.IntReads, Writes.IntWrites)

    behave like nonStartingController[Int](createController, List(Constants.dateOfDeathId, Constants.partOfEstatePassingToDirectDescendantsId))(Reads.IntReads, Writes.IntWrites)
  }
}
