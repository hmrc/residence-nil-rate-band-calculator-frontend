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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec
import uk.gov.hmrc.residencenilratebandcalculator.forms.ExitQuestionnaireForm
import uk.gov.hmrc.residencenilratebandcalculator.models._

class ExitQuestionnaireFormHelperSpec extends BaseSpec {

  val serviceDifficulty = "a"
  val serviceFeel = "b"
  val comments = "c"
  val fullName = "d"
  val email = "e"
  val phoneNumber = "f"

  val values = ExitQuestionnaire(Some(serviceDifficulty), Some(serviceFeel), Some(comments), Some(fullName), Some(email), Some(phoneNumber))

  private def workingMethod(key: ExitQuestionnaireKey, values: ExitQuestionnaire, expectedResultFromFilledForm: String) = {
    "return an empty string when given nothing" in {
      ExitQuestionnaireFormHelper.getValueByKey(None, key) shouldBe ""
    }

    "return an empty string when given a form with no value" in {
      ExitQuestionnaireFormHelper.getValueByKey(Some(ExitQuestionnaireForm()), key) shouldBe ""
    }

    "return the value when one is present in the form" in {
      val form = ExitQuestionnaireForm().fill(values)
      ExitQuestionnaireFormHelper.getValueByKey(Some(form), key) shouldBe expectedResultFromFilledForm
    }
  }

  "Get Value By Key" when {

    "asking for service difficulty" must {
      behave like workingMethod(ServiceDifficulty, values, serviceDifficulty)
    }

    "asking for service feel" must {
      behave like workingMethod(ServiceFeel, values, serviceFeel)
    }

    "asking for comments" must {
      behave like workingMethod(Comments, values, comments)
    }

    "asking for full name" must {
      behave like workingMethod(FullName, values, fullName)
    }

    "asking for email" must {
      behave like workingMethod(Email, values, email)
    }

    "asking for phone number" must {
      behave like workingMethod(PhoneNumber, values, phoneNumber)
    }
  }
}
