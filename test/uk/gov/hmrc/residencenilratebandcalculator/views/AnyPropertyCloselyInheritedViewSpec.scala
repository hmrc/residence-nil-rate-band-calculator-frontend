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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_property_closely_inherited
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.AnyPropertyCloselyInheritedForm

import scala.language.reflectiveCalls

class AnyPropertyCloselyInheritedViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "any_property_closely_inherited"

  def createView(form: Option[Form[String]] = None) = any_property_closely_inherited(frontendAppConfig, backUrl, form, Seq())(request, messages)

  "Any Property Closely Inherited View" must {

    behave like rnrbPage[String](createView, messageKeyPrefix, "guidance")

    behave like pageWithBackLink[String](createView)

    behave like questionPage[String](createView, messageKeyPrefix, AnyPropertyCloselyInheritedController.onSubmit().url)
  }

  "Any Property Closely Inherited View" when {

    "rendered" must {

      "contain radio buttons for the value" in {
        val doc = asDocument(createView(None))
        assertContainsRadioButton(doc, "any_property_closely_inherited.all", "value", "all", false)
        assertContainsRadioButton(doc, "any_property_closely_inherited.some", "value", "some", false)
        assertContainsRadioButton(doc, "any_property_closely_inherited.none", "value", "none", false)
      }
    }

    "rendered with a value of 'all'" must {

      "have the 'all' radion button selected" in {
        val doc = asDocument(createView(Some(AnyPropertyCloselyInheritedForm().bind(Map("value" -> "all")))))
        assertContainsRadioButton(doc, "any_property_closely_inherited.all", "value", "all", true)
        assertContainsRadioButton(doc, "any_property_closely_inherited.some", "value", "some", false)
        assertContainsRadioButton(doc, "any_property_closely_inherited.none", "value", "none", false)
      }
    }

    "rendered with a value of 'some'" must {

      "have the 'some' radion button selected" in {
        val doc = asDocument(createView(Some(AnyPropertyCloselyInheritedForm().bind(Map("value" -> "some")))))
        assertContainsRadioButton(doc, "any_property_closely_inherited.all", "value", "all", false)
        assertContainsRadioButton(doc, "any_property_closely_inherited.some", "value", "some", true)
        assertContainsRadioButton(doc, "any_property_closely_inherited.none", "value", "none", false)
      }
    }

    "rendered with a value of 'none'" must {

      "have the 'none' radion button selected" in {
        val doc = asDocument(createView(Some(AnyPropertyCloselyInheritedForm().bind(Map("value" -> "none")))))
        assertContainsRadioButton(doc, "any_property_closely_inherited.all", "value", "all", false)
        assertContainsRadioButton(doc, "any_property_closely_inherited.some", "value", "some", false)
        assertContainsRadioButton(doc, "any_property_closely_inherited.none", "value", "none", true)
      }
    }
  }
}
