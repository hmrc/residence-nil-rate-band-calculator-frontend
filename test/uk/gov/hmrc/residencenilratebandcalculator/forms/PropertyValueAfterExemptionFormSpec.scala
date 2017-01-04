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

import uk.gov.hmrc.residencenilratebandcalculator.models.PropertyValueAfterExemption

class PropertyValueAfterExemptionFormSpec extends FormSpec {

  "Property Value After Exemption Form" must {

    def values(value: String, valueCloselyInherited: String) = Map("value" -> value, "valueCloselyInherited" -> valueCloselyInherited)

    "bind positive numbers" in {
      val form = PropertyValueAfterExemptionForm().bind(values("1", "2"))
      form.get shouldBe PropertyValueAfterExemption(1, 2)
    }

    "fail to bind a negative value" in {
      val expectedError = error("value", "error.min")
      checkForError(PropertyValueAfterExemptionForm(), values("-1", "2"), expectedError)
    }

    "fail to bind a negative value closely inherited" in {
      val expectedError = error("valueCloselyInherited", "error.min")
      checkForError(PropertyValueAfterExemptionForm(), values("1", "-2"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedErrors = error("value", "error.min") ++ error("valueCloselyInherited", "error.min")
      checkForError(PropertyValueAfterExemptionForm(), values("-1", "-2"), expectedErrors)
    }

    "fail to bind non-numeric value" in {
      val expectedError = error("value", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("A", "2"), expectedError)
    }

    "fail to bind non-numeric value closely inherited" in {
      val expectedError = error("valueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("1", "A"), expectedError)
    }

    "fail to bind non-numeric values" in {
      val expectedErrors = error("value", "error.number") ++ error("valueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("A", "B"), expectedErrors)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("", "1"), expectedError)
    }

    "fail to bind a blank value closely inherited" in {
      val expectedError = error("valueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("1", ""), expectedError)
    }

    "fail to bind blank values" in {
      val expectedErrors = error("value", "error.number") ++ error("valueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("", ""), expectedErrors)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.required")
      checkForError(PropertyValueAfterExemptionForm(), Map("valueCloselyInherited" -> "2"), expectedError)
    }

    "fail to bind when value closely inherited is omitted" in {
      val expectedError = error("valueCloselyInherited", "error.required")
      checkForError(PropertyValueAfterExemptionForm(), Map("value" -> "1"), expectedError)
    }

    "fail to bind when both values are omitted" in {
      val expectedError = error("value", "error.required") ++ error("valueCloselyInherited", "error.required")
      checkForError(PropertyValueAfterExemptionForm(), Map(), expectedError)
    }
  }
}
