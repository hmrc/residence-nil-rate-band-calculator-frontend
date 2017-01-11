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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.{Form, FormError}
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_brought_forward_allowance_on_disposal
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

import scala.language.reflectiveCalls

class AnyBroughtForwardAllowanceOnDisposalViewSpec extends HtmlSpec {

  val url = "url"
  val number = 123
  val errorKey = "value"
  val errorMessage = "error.number"
  val error = FormError(errorKey, errorMessage)

  def fixture(form: Option[Form[Boolean]] = None) = new {
    val view = any_brought_forward_allowance_on_disposal(frontendAppConfig, url, form)(request, messages)
    val doc = asDocument(view)
  }

  "Any Brought Forward Allowance on Disposal View" when {

    "rendered" must {

      def thisFixture() = fixture()

      "contain a back link pointing to another page" in {
        val f = thisFixture()
        f.doc.getElementById("back").attr("href") should be(url)
      }

      "display the correct question designator" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "any_brought_forward_allowance_on_disposal.question_number")
      }

      "display the correct browser title" in {
        val f = thisFixture()
        assertEqualsMessage(f.doc, "title", "any_brought_forward_allowance_on_disposal.browser_title")
      }

      "display the correct page title" in {
        val f = thisFixture()
        assertPageTitleEqualsMessage(f.doc, "any_brought_forward_allowance_on_disposal.title")
      }

      "display the correct guidance" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "any_brought_forward_allowance_on_disposal.guidance")
      }

      "contain a form that POSTs to the correct action" in {
        val f = thisFixture()
        val forms = f.doc.getElementsByTag("form")
        forms.size shouldBe 1
        val form = forms.first
        form.attr("method") shouldBe "POST"
        form.attr("action") shouldBe AnyBroughtForwardAllowanceOnDisposalController.onSubmit().url
      }

      "contain a legend for the question" in {
        val f = thisFixture()
        val legends = f.doc.getElementsByTag("legend")
        legends.size shouldBe 1
        legends.first.text shouldBe messages("any_brought_forward_allowance_on_disposal.label")
      }

      "contain an input for the value" in {
        val f = thisFixture()
        assertRenderedById(f.doc, "yes")
        assertRenderedById(f.doc, "no")
      }

      "have no values checked when rendered with no form" in {
        val f = thisFixture()
        assert(!f.doc.getElementById("yes").hasAttr("checked"))
        assert(!f.doc.getElementById("no").hasAttr("checked"))
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

    "rendered with a value of true" must {

      def thisFixture(value: Boolean) = fixture(Some(BooleanForm().fill(value)))

      "have only the 'Yes' value checked when rendered with an appropriate form" in {
        val f = thisFixture(true)
        assert(f.doc.getElementById("yes").hasAttr("checked"))
        assert(!f.doc.getElementById("no").hasAttr("checked"))
      }

      "have only the 'No' value checked when rendered with an appropriate form" in {
        val f = thisFixture(false)
        assert(!f.doc.getElementById("yes").hasAttr("checked"))
        assert(f.doc.getElementById("no").hasAttr("checked"))
      }

      "not render an error summary" in {
        val f = thisFixture(true)
        assertNotRenderedById(f.doc, "error-summary-heading")
      }
    }

    "rendered with an error" must {

      def thisFixture = fixture(Some(BooleanForm().withError(error)))

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
