/*
 * Copyright 2016 HM Revenue & Customs
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

class PercentFormSpec extends FormSpec {

  "Percent Int Form" must {

    "bind positive numbers" in {
      val form = PercentForm().bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "bind numbers as high as 100" in {
      val form = PercentForm().bind(Map("value" -> "100"))
      form.get shouldBe 100
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", "error.min")
      checkForError(PercentForm(), Map("value" -> "-1"), expectedError)
    }

    "fail to bind numbers greater than 100" in {
      val expectedError = error("value", "error.max")
      checkForError(PercentForm(), Map("value" -> "101"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", "error.number")
      checkForError(PercentForm(), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", "error.number")
      checkForError(PercentForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.required")
      checkForError(PercentForm(), emptyForm, expectedError)
    }
  }
}
