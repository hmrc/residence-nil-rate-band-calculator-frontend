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
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.PositivePercentForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_passed_to_direct_descendants

class PercentagePassedToDirectDescendantsControllerSpec extends SimpleControllerSpecBase {

  val errorKeyBlank = "percentage_passed_to_direct_descendants.error.required"
  val errorKeyNonNumeric = "percentage_passed_to_direct_descendants.error.non_numeric"
  val errorKeyOutOfRange = "percentage_passed_to_direct_descendants.error.out_of_range"
  val messageKeyPrefix = "percentage_passed_to_direct_descendants"

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  "Percentage Passed To Direct Descendants Controller" must {

    def createView = (value: Option[Map[String, String]]) => {
      value match {
        case None => percentage_passed_to_direct_descendants(PositivePercentForm.apply(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), answerRows = Seq())(fakeRequest, messages, mockConfig)
        case Some(v) => percentage_passed_to_direct_descendants(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange).bind(v), Seq())(fakeRequest, messages, mockConfig)
      }
    }

    def createController = () => new PercentagePassedToDirectDescendantsController(messagesControllerComponents, mockSessionConnector, navigator, mockConfig)

    val testValue = BigDecimal(50)

    behave like rnrbController[BigDecimal](createController, createView, Constants.percentagePassedToDirectDescendantsId, messageKeyPrefix, testValue)(Reads.bigDecReads, Writes.BigDecimalWrites)

    behave like nonStartingController[BigDecimal](createController,
      List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.valueOfEstateId,
        Constants.chargeableEstateValueId,
        Constants.propertyInEstateId,
        Constants.propertyValueId,
        Constants.propertyPassingToDirectDescendantsId))(Reads.bigDecReads, Writes.BigDecimalWrites)
  }
}
