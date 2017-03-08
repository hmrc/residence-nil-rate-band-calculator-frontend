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

class AnyPropertyCloselyInheritedFormSpec extends FormSpec {

  "Any Property Closely Inherited form" must {
    "bind the value 'all'" in {
      val form = AnyPropertyCloselyInheritedForm().bind(Map("value" -> "all"))
      form.get shouldBe "all"
    }

    "bind the value 'some'" in {
      val form = AnyPropertyCloselyInheritedForm().bind(Map("value" -> "some"))
      form.get shouldBe "some"
    }

    "bind the value 'none'" in {
      val form = AnyPropertyCloselyInheritedForm().bind(Map("value" -> "none"))
      form.get shouldBe "none"
    }

    "fail to bind a blank value" in {
      val expectedError = error("", "error.invalid_closely_inherited_property_option")
      checkForError(AnyPropertyCloselyInheritedForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.required")
      checkForError(AnyPropertyCloselyInheritedForm(), emptyForm, expectedError)
    }
  }
}
