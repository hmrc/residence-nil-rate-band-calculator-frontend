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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.*
import uk.gov.hmrc.residencenilratebandcalculator.forms.Forms
import uk.gov.hmrc.residencenilratebandcalculator.forms.constructors.PropertyPassingToDirectDescendantsForm
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewBooleanViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_passing_to_direct_descendants

class PropertyPassingToDirectDescendantsViewSpec extends NewBooleanViewSpec {

  val messageKeyPrefix = "property_passing_to_direct_descendants"

  val property_passing_to_direct_descendants: property_passing_to_direct_descendants =
    inject[property_passing_to_direct_descendants]

  def createView(form: Form[String]): Html =
    property_passing_to_direct_descendants(form)(request, messages)

  val form: Form[String] = Forms.PropertyPassingToDirectDescendants

  "Property Passing To Direct Descendants View" must {

    behave.like(
      rnrbPage[String](createView, messageKeyPrefix, "guidance1", "guidance2")(
        form
      )
    )

    behave.like(
      pageWithoutBackLink[String](
        createView,
        form
      )
    )

    behave.like(
      questionPage[String](
        createView,
        messageKeyPrefix,
        PropertyPassingToDirectDescendantsController.onSubmit.url,
        form
      )
    )
  }

  "Property Passing To Direct Descendants View" when {

    "rendered" must {

      "contain radio buttons for the value" in {
        val doc = asDocument(createView(PropertyPassingToDirectDescendantsForm.apply()))
        assertContainsRadioButton(doc, "value", "value", "all", false)
        assertContainsRadioButton(doc, "value-2", "value", "some", false)
        assertContainsRadioButton(doc, "value-3", "value", "none", false)
      }
    }

    "rendered with a value of 'all'" must {

      "have the 'all' radion button selected" in {
        val doc = asDocument(createView(PropertyPassingToDirectDescendantsForm.apply().bind(Map("value" -> "all"))))
        assertContainsRadioButton(doc, "value", "value", "all", true)
        assertContainsRadioButton(doc, "value-2", "value", "some", false)
        assertContainsRadioButton(doc, "value-3", "value", "none", false)
      }
    }

    "rendered with a value of 'some'" must {

      "have the 'some' radion button selected" in {
        val doc = asDocument(createView(PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "some"))))
        assertContainsRadioButton(doc, "value", "value", "all", false)
        assertContainsRadioButton(doc, "value-2", "value", "some", true)
        assertContainsRadioButton(doc, "value-3", "value", "none", false)
      }
    }

    "rendered with a value of 'none'" must {

      "have the 'none' radion button selected" in {
        val doc = asDocument(createView(PropertyPassingToDirectDescendantsForm().bind(Map("value" -> "none"))))
        assertContainsRadioButton(doc, "value", "value", "all", false)
        assertContainsRadioButton(doc, "value-2", "value", "some", false)
        assertContainsRadioButton(doc, "value-3", "value", "none", true)
      }
    }
  }

}
