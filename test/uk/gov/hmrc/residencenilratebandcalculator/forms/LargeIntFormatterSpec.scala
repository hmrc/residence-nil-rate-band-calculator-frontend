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

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.residencenilratebandcalculator.forms.LargeIntFormatter.largeIntFormat

object TestForm {
  def apply(): Form[Int] = Form(single("value" -> of[Int]))
}

class LargeIntFormatterSpec extends FormSpec {

  "bind positive numbers" in {
    val form = NonNegativeIntForm().bind(Map("value" -> "1"))
    form.get shouldBe 1
  }

  "bind positive, comma separated numbers" in {
    val form = NonNegativeIntForm().bind(Map("value" -> "10,000"))
    form.get shouldBe 10000
  }

  "fail to bind non-numerics" in {
    val expectedError = error("value", "error.number")
    checkForError(NonNegativeIntForm(), Map("value" -> "not a number"), expectedError)
  }

  "fail to bind a blank value" in {
    val expectedError = error("value", "error.number")
    checkForError(NonNegativeIntForm(), Map("value" -> ""), expectedError)
  }

  "fail to bind when value is omitted" in {
    val expectedError = error("value", "error.required")
    checkForError(NonNegativeIntForm(), emptyForm, expectedError)
  }
}
