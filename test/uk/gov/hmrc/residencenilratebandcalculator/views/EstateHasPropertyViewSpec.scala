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

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.EstateHasPropertyController
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.estate_has_property

import scala.language.reflectiveCalls

class EstateHasPropertyViewSpec extends HtmlSpec {

  def fixture(form: Option[Form[Boolean]] = None) = new {
    val view = estate_has_property(frontendAppConfig, form)(request, messages)
    val doc = asDocument(view)
  }

  "Estate Has Property View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "estate_has_property.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "estate_has_property.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "estate_has_property.guidance")
    }

    "contain a form that POSTs to the correct action" in {
      val f = fixture()
      val forms = f.doc.getElementsByTag("form")
      forms.size shouldBe 1
      val form = forms.first
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe EstateHasPropertyController.onSubmit().url
    }

    "contain a legend for the question" in {
      val f = fixture()
      val legends = f.doc.getElementsByTag("legend")
      legends.size shouldBe 1
      legends.first.text shouldBe messages("estate_has_property.label")
    }

    "contain an input for the value" in {
      val f = fixture()
      assertRenderedById(f.doc, "value-1")
      assertRenderedById(f.doc, "value-2")
    }

    "have no values checked when rendered with no form" in {
      val f = fixture()
      assert(!f.doc.getElementById("value-1").hasAttr("checked"))
      assert(!f.doc.getElementById("value-2").hasAttr("checked"))
    }

    "have only the 'Yes' value checked when rendered with an appropriate form" in {
      val f = fixture(Some(BooleanForm().fill(true)))
      assert(f.doc.getElementById("value-1").hasAttr("checked"))
      assert(!f.doc.getElementById("value-2").hasAttr("checked"))
    }

    "have only the 'No' value checked when rendered with an appropriate form" in {
      val f = fixture(Some(BooleanForm().fill(false)))
      assert(!f.doc.getElementById("value-1").hasAttr("checked"))
      assert(f.doc.getElementById("value-2").hasAttr("checked"))
    }
    "contain a submit button" in {
      val f = fixture()
      assertRenderedByCssSelector(f.doc, "input[type=submit]")
    }
  }
}
