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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyValueAfterExemptionForm
import uk.gov.hmrc.residencenilratebandcalculator.models.PropertyValueAfterExemption
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value_after_exemption

import scala.language.reflectiveCalls

class PropertyValueAfterExemptionViewSpec extends HtmlSpec {

  val propertyValue = 123
  val propertyValueCloselyInherited = 111
  val propertyValueKey = "propertyValue"
  val errorMessage = "error.number"
  val error = FormError(propertyValueKey, errorMessage)

  def fixture(form: Option[Form[PropertyValueAfterExemption]] = None) = new {
    val view = property_value_after_exemption(frontendAppConfig, form)(request, messages)
    val doc = asDocument(view)
  }

  "Property Value View" when {

    def thisFixture() = fixture()

    "rendered" must {

      "display the correct browser title" in {
        val f = thisFixture()
        assertEqualsMessage(f.doc, "title", "property_value_after_exemption.browser_title")
      }

      "display the correct page title" in {
        val f = thisFixture()
        assertPageTitleEqualsMessage(f.doc, "property_value_after_exemption.title")
      }

      "contain a form that POSTs to the correct action" in {
        val f = thisFixture()
        val forms = f.doc.getElementsByTag("form")
        forms.size shouldBe 1
        val form = forms.first
        form.attr("method") shouldBe "POST"
        form.attr("action") shouldBe routes.PropertyValueAfterExemptionController.onSubmit().url
      }

      "contain a label for the property value" in {
        val f = thisFixture()
        assertContainsLabel(f.doc, "propertyValue", messages("property_value_after_exemption.property_value.label"))
      }

      "contain an input for the property value" in {
        val f = thisFixture()
        assertRenderedById(f.doc, "propertyValue")
      }

      "contain a label for the property value closely inherited" in {
        val f = thisFixture()
        assertContainsLabel(f.doc, "propertyValueCloselyInherited", messages("property_value_after_exemption.property_value_closely_inherited.label"))
      }

      "contain an input for the property value closely inherited" in {
        val f = thisFixture()
        assertRenderedById(f.doc, "propertyValueCloselyInherited")
      }

      "contain a submit button" in {
        val f = thisFixture()
        assertRenderedByCssSelector(f.doc, "input[type=submit]")
      }
    }

    "rendered with a valid form" must {

      def thisFixture() = fixture(Some(PropertyValueAfterExemptionForm().fill(PropertyValueAfterExemption(propertyValue, propertyValueCloselyInherited))))

      "include the form's property value in the value input" in {
        val f = thisFixture()
        f.doc.getElementById("propertyValue").attr("value") shouldBe propertyValue.toString
      }

      "include the form's property value closely inherited in the value input" in {
        val f = thisFixture()
        f.doc.getElementById("propertyValueCloselyInherited").attr("value") shouldBe propertyValueCloselyInherited.toString
      }
    }

    "rendered with an error" must {

      val thisFixture = fixture(Some(PropertyValueAfterExemptionForm().withError(error)))

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
