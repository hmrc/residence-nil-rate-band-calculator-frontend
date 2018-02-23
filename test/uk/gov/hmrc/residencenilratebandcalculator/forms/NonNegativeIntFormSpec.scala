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

package uk.gov.hmrc.residencenilratebandcalculator.forms

class NonNegativeIntFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyDecimal = "decimal"
  val errorKeyNonNumeric = "non numeric"

  "Non Negative Int Form" must {

    "bind positive numbers" in {
      val form = NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric).bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "bind positive, comma separated numbers" in {
      val form = NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric).bind(Map("value" -> "10,000"))
      form.get shouldBe 10000
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), emptyForm, expectedError)
    }

    "fail to bind decimal numbers" in {
      val expectedError = error("value", errorKeyDecimal)
      checkForError(NonNegativeIntForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> "123.45"), expectedError)
    }
  }
}
