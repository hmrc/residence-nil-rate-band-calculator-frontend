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

    def values(value: String, valueCloselyInherited: String) = Map("propertyValue" -> value, "propertyValueCloselyInherited" -> valueCloselyInherited)

    "bind positive numbers" in {
      val form = PropertyValueAfterExemptionForm().bind(values("1", "2"))
      form.get shouldBe PropertyValueAfterExemption(1, 2)
    }

    "fail to bind a negative property value" in {
      val expectedError = error("propertyValue", "error.min")
      checkForError(PropertyValueAfterExemptionForm(), values("-1", "2"), expectedError)
    }

    "fail to bind a negative property value closely inherited" in {
      val expectedError = error("propertyValueCloselyInherited", "error.min")
      checkForError(PropertyValueAfterExemptionForm(), values("1", "-2"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedErrors = error("propertyValue", "error.min") ++ error("propertyValueCloselyInherited", "error.min")
      checkForError(PropertyValueAfterExemptionForm(), values("-1", "-2"), expectedErrors)
    }

    "fail to bind non-numeric property value" in {
      val expectedError = error("propertyValue", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("A", "2"), expectedError)
    }

    "fail to bind non-numeric property value closely inherited" in {
      val expectedError = error("propertyValueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("1", "A"), expectedError)
    }

    "fail to bind non-numeric values" in {
      val expectedErrors = error("propertyValue", "error.number") ++ error("propertyValueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("A", "B"), expectedErrors)
    }

    "fail to bind a blank property value" in {
      val expectedError = error("propertyValue", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("", "1"), expectedError)
    }

    "fail to bind a blank property value closely inherited" in {
      val expectedError = error("propertyValueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("1", ""), expectedError)
    }

    "fail to bind blank values" in {
      val expectedErrors = error("propertyValue", "error.number") ++ error("propertyValueCloselyInherited", "error.number")
      checkForError(PropertyValueAfterExemptionForm(), values("", ""), expectedErrors)
    }

    "fail to bind when property value is omitted" in {
      val expectedError = error("propertyValue", "error.required")
      checkForError(PropertyValueAfterExemptionForm(), Map("propertyValueCloselyInherited" -> "2"), expectedError)
    }

    "fail to bind when property value closely inherited is omitted" in {
      val expectedError = error("propertyValueCloselyInherited", "error.required")
      checkForError(PropertyValueAfterExemptionForm(), Map("propertyValue" -> "1"), expectedError)
    }

    "fail to bind when both values are omitted" in {
      val expectedError = error("propertyValue", "error.required") ++ error("propertyValueCloselyInherited", "error.required")
      checkForError(PropertyValueAfterExemptionForm(), Map(), expectedError)
    }
  }
}
