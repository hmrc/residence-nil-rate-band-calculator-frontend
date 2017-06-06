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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import play.api.data.FormError
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec
import uk.gov.hmrc.residencenilratebandcalculator.forms.{BooleanForm, NonNegativeIntForm}

class FormHelpersSpec extends BaseSpec {

  val number = 123
  val day = 1
  val month = 2
  val year = 2000
  val errorKey = "a key"
  val otherErrorKey = "another key"
  val errorMessage = "an error"

  "Get Value" must {

    "return an empty string when given nothing" in {
      FormHelpers.getValue(None) shouldBe ""
    }

    "return an empty string when given an Int form with no value" in {
      FormHelpers.getValue[Int](Some(NonNegativeIntForm("", "", ""))) shouldBe ""
    }

    "return the correct value when given an Int form with no value" in {

      val form = NonNegativeIntForm("", "", "").fill(number)
      FormHelpers.getValue[Int](Some(form)) shouldBe number
    }
  }

  "Get Error by Key" must {

    "return an empty string when given nothing" in {
      FormHelpers.getErrorByKey[Int](None, errorKey) shouldBe ""
    }

    "return an empty string when given a form with no errors" in {
      FormHelpers.getErrorByKey[Int](Some(NonNegativeIntForm("", "", "")), errorKey) shouldBe ""
    }

    "return an empty string when given a form with an error for a different key" in {
      val error = FormError(errorKey, errorMessage)
      val formWithErrors = NonNegativeIntForm("", "", "").withError(error)
      FormHelpers.getErrorByKey[Int](Some(formWithErrors), otherErrorKey) shouldBe ""
    }

    "return the error when given a form with an error for this key" in {
      val error = FormError(errorKey, errorMessage)
      val formWithErrors = NonNegativeIntForm("", "", "").withError(error)
      FormHelpers.getErrorByKey[Int](Some(formWithErrors), errorKey) shouldBe errorMessage
    }
  }

  "Get all errors" must {

    "return an empty string when given nothing" in {
      FormHelpers.getAllErrors(None) shouldBe Seq[FormError]()
    }

    "return an empty sequence when given a form with no errors" in {
      FormHelpers.getAllErrors[Int](Some(NonNegativeIntForm("", "", ""))) shouldBe Seq[FormError]()
    }

    "return all of the errors when given a form with errors" in {
      val errors = Seq(FormError(errorKey, errorMessage), FormError(otherErrorKey, errorMessage))
      val formWithErrors = NonNegativeIntForm("", "", "")
        .withError(FormError(errorKey, errorMessage))
        .withError(FormError(otherErrorKey, errorMessage))

      FormHelpers.getAllErrors(Some(formWithErrors)) shouldBe errors
    }
  }

  "Get Yes / No" must {

    "return an empty string when given nothing" in {
      FormHelpers.getYesNo(None) shouldBe ""
    }

    "return an empty string when given a form with no value" in {
      FormHelpers.getYesNo(Some(BooleanForm(""))) shouldBe ""
    }

    "return 'Yes' when given a form with the value 'true'" in {
      FormHelpers.getYesNo(Some(BooleanForm("").fill(true))) shouldBe "Yes"
    }

    "return 'No' when given a form with the value 'false'" in {
      FormHelpers.getYesNo(Some(BooleanForm("").fill(false))) shouldBe "No"
    }
  }
}
