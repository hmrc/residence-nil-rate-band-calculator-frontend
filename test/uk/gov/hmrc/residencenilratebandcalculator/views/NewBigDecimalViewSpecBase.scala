/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.Form
import play.twirl.api.HtmlFormat
import org.scalatest.Matchers.convertToAnyShouldWrapper

trait NewBigDecimalViewSpecBase extends NewViewSpecBase {

  val number = BigDecimal(50.01)

  def bigDecimalPage(createView: (Form[BigDecimal]) => HtmlFormat.Appendable,
                     messageKeyPrefix: String,
                     expectedFormAction: String,
                     form: Form[BigDecimal], emptyForm: Form[BigDecimal]) = {
    behave like questionPage[BigDecimal](createView, messageKeyPrefix, expectedFormAction, emptyForm)

    "behave like a page with an big decimal value field" when {
      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(emptyForm))
          doc.select("h1").text() shouldBe messages(s"$messageKeyPrefix.title")
        }
        "contain an input for the value" in {
          val doc = asDocument(createView(emptyForm))
          assertRenderedById(doc, "value")
        }

        "rendered with a valid form" must {
          "include the form's value in the value input" in {
            val doc = asDocument(createView(form.fill(number)))
            doc.getElementById("value").attr("value") shouldBe number.toString
          }
        }

        "rendered with an error" must {

          "show an error summary" in {
            val doc = asDocument(createView(form.withError(error)))
            assertRenderedById(doc, "error-summary-title")
          }

          "show an error in the value field's label" in {
            val doc = asDocument(createView(form.withError(error)))
            val errorSpan = doc.getElementsByClass("govuk-error-summary__list").first
            errorSpan.text shouldBe messages(errorMessage)
          }
        }
      }
    }
  }
}