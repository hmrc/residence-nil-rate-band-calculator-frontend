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

    behave like rnrbPage[PropertyValueAfterExemption](createView, messageKeyPrefix)

    behave like pageWithBackLink[PropertyValueAfterExemption](createView)

    behave like questionPage[PropertyValueAfterExemption](createView, messageKeyPrefix, routes.PropertyValueAfterExemptionController.onSubmit().url)

    "behave correctly" when {
      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(None))
          assertContainsLabel(doc, "value", messages("property_value_after_exemption.value.label"))
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(None))
          assertRenderedById(doc, "value")
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
