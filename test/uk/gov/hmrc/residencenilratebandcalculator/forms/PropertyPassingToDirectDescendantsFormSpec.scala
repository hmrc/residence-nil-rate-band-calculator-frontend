/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class PropertyPassingToDirectDescendantsFormSpec extends FormSpec {

  "Property Passing To Direct Descendants form" must {
    "bind the value 'all'" in {
      val form = PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "all"))
      form.get shouldBe "all"
    }

    "bind the value 'some'" in {
      val form = PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "some"))
      form.get shouldBe "some"
    }

    "bind the value 'none'" in {
      val form = PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "none"))
      form.get shouldBe "none"
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", "error.invalid_property_passing_to_direct_descendants_option")
      checkForError(PropertyPassingToDirectDescendantsForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.invalid_property_passing_to_direct_descendants_option")
      checkForError(PropertyPassingToDirectDescendantsForm(), emptyForm, expectedError)
    }
  }
}
