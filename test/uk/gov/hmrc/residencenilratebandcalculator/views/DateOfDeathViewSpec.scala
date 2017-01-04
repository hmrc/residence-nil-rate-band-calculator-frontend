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

import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death

import scala.language.reflectiveCalls

class DateOfDeathViewSpec extends HtmlSpec {

  def fixture() = new {
    val view = date_of_death(frontendAppConfig)(request, messages)
    val doc = asDocument(view)
  }

  "Date of Death View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "date_of_death.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "date_of_death.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "date_of_death.guidance")
    }

    "contain a form that POSTs to the correct action" in {
      val f = fixture()
      val forms = f.doc.getElementsByTag("form")
      forms.size shouldBe 1
      val form = forms.first
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe routes.DateOfDeathController.onSubmit().url
    }

    "contain a label for the date" in {
      val f = fixture()
      assertContainsMessages(f.doc, "date_of_death.label")
    }

    "contain a label for the day" in {
      val f = fixture()
      assertContainsLabel(f.doc, "day", messages("date.day"))
    }

    "contain an input for the day" in {
      val f = fixture()
      assertRenderedById(f.doc, "day")
    }

    "contain a label for the month" in {
      val f = fixture()
      assertContainsLabel(f.doc, "month", messages("date.month"))
    }

    "contain an input for the month" in {
      val f = fixture()
      assertRenderedById(f.doc, "month")
    }

    "contain a label for the year" in {
      val f = fixture()
      assertContainsLabel(f.doc, "year", messages("date.year"))
    }

    "contain an input for the year" in {
      val f = fixture()
      assertRenderedById(f.doc, "year")
    }

    "contain a submit button" in {
      val f = fixture()
      assertRenderedByCssSelector(f.doc, "input[type=submit]")
    }
  }
}
