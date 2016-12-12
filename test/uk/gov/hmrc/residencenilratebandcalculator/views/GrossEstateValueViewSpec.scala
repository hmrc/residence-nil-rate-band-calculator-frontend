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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.{Form, FormError}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value

import scala.language.reflectiveCalls

class GrossEstateValueViewSpec extends HtmlSpec {

  val number = 123
  val errorKey = "value"
  val errorMessage = "error.number"
  val error = FormError(errorKey, errorMessage)

  def fixture(form: Option[Form[Int]] = None) = new {
    val view = gross_estate_value(frontendAppConfig, form)(request, messages)
    val doc = asDocument(view)
  }

  "Gross Estate Value View" when {

    "rendered" must {

      def thisFixture() = fixture()

      "display the correct browser title" in {
        val f = thisFixture()
        assertEqualsMessage(f.doc, "title", "gross_estate_value.browser_title")
      }

      "display the correct page title" in {
        val f = thisFixture()
        assertPageTitleEqualsMessage(f.doc, "gross_estate_value.title")
      }

      "display the correct guidance" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "gross_estate_value.guidance")
      }

      "contain a form that POSTs to the correct action" in {
        val f = thisFixture()
        val forms = f.doc.getElementsByTag("form")
        forms.size shouldBe 1
        val form = forms.first
        form.attr("method") shouldBe "POST"
        form.attr("action") shouldBe routes.GrossEstateValueController.onSubmit().url
      }

      "contain a label for the value" in {
        val f = thisFixture()
        assertContainsLabel(f.doc, "value", messages("gross_estate_value.label"))
      }

      "contain an input for the value" in {
        val f = thisFixture()
        assertRenderedById(f.doc, "value")
      }

      "contain a submit button" in {
        val f = thisFixture()
        assertRenderedByCssSelector(f.doc, "input[type=submit]")
      }

      "not render an error summary" in {
        val f = thisFixture()
        assertNotRenderedById(f.doc, "error-summary-heading")
      }
    }

    "rendered with a valid form" must {

      def thisFixture() = fixture(Some(NonNegativeIntForm().fill(number)))

      "include the form's value in the value input" in {
        val f = thisFixture()
        f.doc.getElementById("value").attr("value") shouldBe number.toString
      }
    }

    "rendered with an error" must {

      val thisFixture = fixture(Some(NonNegativeIntForm().withError(error)))

      "show an error summary" in {
        val f = thisFixture
        assertRenderedById(f.doc, "error-summary-heading")
      }

      "show an error message in the value field's label" in {
        val f = thisFixture
        val errorSpan = f.doc.getElementsByClass("error-notification").first
        errorSpan.text shouldBe messages(errorMessage)
      }
    }
  }
}