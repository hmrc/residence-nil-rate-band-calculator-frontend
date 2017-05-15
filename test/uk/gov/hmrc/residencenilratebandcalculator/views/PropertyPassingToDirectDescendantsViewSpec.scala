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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyPassingToDirectDescendantsForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_passing_to_direct_descendants

import scala.language.reflectiveCalls

class PropertyPassingToDirectDescendantsViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "property_passing_to_direct_descendants"

  def createView(form: Option[Form[String]] = None) = property_passing_to_direct_descendants(frontendAppConfig, form, Seq())(request, messages, applicationProvider)

  "Property Passing To Direct Descendants View" must {

    behave like rnrbPage[String](createView, messageKeyPrefix, "guidance1", "guidance2")

    behave like pageWithoutBackLink[String](createView)

    behave like questionPage[String](createView, messageKeyPrefix, PropertyPassingToDirectDescendantsController.onSubmit().url)

    behave like pageContainingPreviousAnswers(createView)

  }

  "Property Passing To Direct Descendants View" when {

    "rendered" must {

      "contain radio buttons for the value" in {
        val doc = asDocument(createView(None))
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.all", "value", "all", false)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.some", "value", "some", false)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.none", "value", "none", false)
      }
    }

    "rendered with a value of 'all'" must {

      "have the 'all' radion button selected" in {
        val doc = asDocument(createView(Some(PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "all")))))
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.all", "value", "all", true)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.some", "value", "some", false)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.none", "value", "none", false)
      }
    }

    "rendered with a value of 'some'" must {

      "have the 'some' radion button selected" in {
        val doc = asDocument(createView(Some(PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "some")))))
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.all", "value", "all", false)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.some", "value", "some", true)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.none", "value", "none", false)
      }
    }

    "rendered with a value of 'none'" must {

      "have the 'none' radion button selected" in {
        val doc = asDocument(createView(Some(PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "none")))))
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.all", "value", "all", false)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.some", "value", "some", false)
        assertContainsRadioButton(doc, "property_passing_to_direct_descendants.none", "value", "none", true)
      }
    }
  }
}
