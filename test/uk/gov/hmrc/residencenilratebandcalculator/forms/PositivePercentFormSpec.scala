/*
 * Copyright 2020 HM Revenue & Customs
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

class PositivePercentFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyNonNumeric = "non numeric"
  val errorKeyOutOfRange = "out of range"

  "Positive Percent Int Form" must {

    "bind positive numbers" in {
      val form = PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange).bind(Map("value" -> "0.0001"))
      form.get shouldBe BigDecimal(0.0001)
    }

    "bind numbers as high as 100" in {
      val form = PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange).bind(Map("value" -> "100"))
      form.get shouldBe 100
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), Map("value" -> "-0.0001"), expectedError)
    }

    "fail to bind zero" in {
      val expectedError = error("value", errorKeyOutOfRange)
      checkForError(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), Map("value" -> "0"), expectedError)
    }

    "fail to bind numbers greater than 100" in {
      val expectedError = error("value", errorKeyOutOfRange)
      checkForError(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), Map("value" -> "100.0001"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(PositivePercentForm(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange), emptyForm, expectedError)
    }
  }
}
