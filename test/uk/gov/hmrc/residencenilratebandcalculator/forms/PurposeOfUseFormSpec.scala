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

package uk.gov.hmrc.residencenilratebandcalculator.forms

class PurposeOfUseFormSpec extends FormSpec {

  "Purpose of Use form" must {
    "bind the value 'planning'" in {
      val form = PurposeOfUseForm().bind(Map("value" -> "planning"))
      form.get shouldBe "planning"
    }

    "bind the value 'dealing_with_estate'" in {
      val form = PurposeOfUseForm().bind(Map("value" -> "dealing_with_estate"))
      form.get shouldBe "dealing_with_estate"
    }

    "Fail to bind a different string" in {
      val exepctedError = error("", "error.invalid_purpose_of_use")
      checkForError(PurposeOfUseForm(), Map("value" -> "invalid data"), exepctedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("", "error.invalid_purpose_of_use")
      checkForError(PurposeOfUseForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.required")
      checkForError(PurposeOfUseForm(), emptyForm, expectedError)
    }
  }
}
