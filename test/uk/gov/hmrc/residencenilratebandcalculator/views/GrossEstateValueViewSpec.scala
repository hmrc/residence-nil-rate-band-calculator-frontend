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

import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value

import scala.language.reflectiveCalls

class GrossEstateValueViewSpec extends HtmlSpec {

  def fixture() = new {
    val view = gross_estate_value(frontendAppConfig)(request, messages)
    val doc = asDocument(view)
  }

  "Gross Estate Value View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "gross_estate_value.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "gross_estate_value.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "gross_estate_value.guidance")
    }

    "contain a form that POSTs to the correct action" in {
      val f = fixture()
      val forms = f.doc.getElementsByTag("form")
      forms.size shouldBe 1
      val form = forms.first
      pending // Assert that the href is correct
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
