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

import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value
import scala.language.reflectiveCalls

class PropertyValueViewSpec extends HtmlSpec {

  def fixture() = new {
    val view = property_value(frontendAppConfig)(request, messages)
    val doc = asDocument(view)
  }

  "Property Value View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "property_value.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "property_value.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "property_value.guidance")
    }

    "contain a form that POSTs to the correct action" in {
      val f = fixture()
      val forms = f.doc.getElementsByTag("form")
      forms.size shouldBe 1
      val form = forms.first
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe routes.PropertyValueController.onSubmit().url
    }

    "contain a label for the value" in {
      val f = fixture()
      assertContainsLabel(f.doc, "value", messages("property_value.label"))
    }

    "contain an input for the value" in {
      val f = fixture()
      assertRenderedById(f.doc, "value")
    }

    "contain a submit button" in {
      val f = fixture()
      assertRenderedByCssSelector(f.doc, "input[type=submit]")
    }
  }
}
