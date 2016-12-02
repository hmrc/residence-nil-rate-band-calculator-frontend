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

class GrossEstateValueFormSpec extends FormSpec {

  "Gross Estate Value Form" must {

    "bind positive numbers" in {
      val form = GrossEstateValueForm().bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", "error.min")
      checkForError(GrossEstateValueForm(), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", "error.number")
      checkForError(GrossEstateValueForm(), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", "error.number")
      checkForError(GrossEstateValueForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.required")
      checkForError(GrossEstateValueForm(), emptyForm, expectedError)
    }
  }
}