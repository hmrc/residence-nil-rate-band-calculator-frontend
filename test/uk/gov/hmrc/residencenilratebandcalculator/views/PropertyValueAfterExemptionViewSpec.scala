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

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyValueAfterExemptionForm
import uk.gov.hmrc.residencenilratebandcalculator.models.PropertyValueAfterExemption
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value_after_exemption

import scala.language.reflectiveCalls

class PropertyValueAfterExemptionViewSpec extends ViewSpecBase {

  val propertyValue = 123
  val propertyValueCloselyInherited = 111
  val propertyValueKey = "value"
  val messageKeyPrefix = "property_value_after_exemption"

  def createView(form: Option[Form[PropertyValueAfterExemption]] = None) = property_value_after_exemption(frontendAppConfig, backUrl, form)(request, messages)

  def filledForm = PropertyValueAfterExemptionForm().fill(PropertyValueAfterExemption(propertyValue, propertyValueCloselyInherited))

  "Property Value After Exemption View" must {

    behave like pageWithBackLink[PropertyValueAfterExemption](createView)

    "behave correctly" when {
      "rendered" must {

        "display the correct browser title" in {
          val doc = asDocument(createView(None))
          assertEqualsMessage(doc, "title", s"$messageKeyPrefix.browser_title")
        }

        "display the correct page title" in {
          val doc = asDocument(createView(None))
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.value_closely_inherited.title")
        }

        "display the correct guidance" in {
          val doc = asDocument(createView(None))
          val expectedGuidanceKeys = Seq("value_closely_inherited.guidance", "value.guidance")
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "contain a form that POSTs to the correct action" in {
          val doc = asDocument(createView(None))
          val forms = doc.getElementsByTag("form")
          forms.size shouldBe 1
          val form = forms.first
          form.attr("method") shouldBe "POST"
          form.attr("action") shouldBe routes.PropertyValueAfterExemptionController.onSubmit().url
        }

        "contain a submit button" in {
          val doc = asDocument(createView(None))
          val input = assertRenderedByCssSelector(doc, "input[type=submit]")
        }

        "contain a title for the value" in {
          val doc = asDocument(createView(None))
          assertContainsMessages(doc, "property_value_after_exemption.value.title")
        }

        "contain guidance for the value" in {
          val doc = asDocument(createView(None))
          assertContainsMessages(doc, "property_value_after_exemption.value.guidance")
        }

        "contain a label for the value" in {
          val doc = asDocument(createView(None))
          assertContainsLabel(doc, "value", messages("property_value_after_exemption.value.label"))
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(None))
          assertRenderedById(doc, "value")
        }

        "contain a title for the value closely inherited" in {
          val doc = asDocument(createView(None))
          assertContainsMessages(doc, "property_value_after_exemption.value_closely_inherited.title")
        }

        "contain guidance for the value closely inherited" in {
          val doc = asDocument(createView(None))
          assertContainsMessages(doc, "property_value_after_exemption.value_closely_inherited.guidance")
        }

        "contain a label for the value closely inherited" in {
          val doc = asDocument(createView(None))
          assertContainsLabel(doc, "valueCloselyInherited", messages("property_value_after_exemption.value_closely_inherited.label"))
        }

        "contain an input for the value closely inherited" in {
          val doc = asDocument(createView(None))
          assertRenderedById(doc, "valueCloselyInherited")
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the correct input" in {
          val doc = asDocument(createView(Some(filledForm)))
          doc.getElementById("value").attr("value") shouldBe propertyValue.toString
        }

        "include the form's value closely inherited in the correct input" in {
          val doc = asDocument(createView(Some(filledForm)))
          doc.getElementById("valueCloselyInherited").attr("value") shouldBe propertyValueCloselyInherited.toString
        }
      }

      "rendered with an error" must {
        "show an error summary" in {
          val doc = asDocument(createView(Some(PropertyValueAfterExemptionForm().withError(error))))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error message in the value field's label" in {
          val doc = asDocument(createView(Some(PropertyValueAfterExemptionForm().withError(error))))
          val errorSpan = doc.getElementsByClass("error-notification").first
          errorSpan.text shouldBe messages(errorMessage)
        }
      }
    }
  }
}
