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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_disposal

import scala.language.reflectiveCalls

class DateOfDisposalViewSpec extends HtmlSpec {

  val url = "URL"

  def fixture() = new {
    val view = date_of_disposal(frontendAppConfig, url)(request, messages)
    val doc = asDocument(view)
  }

  "Date of Disposal View" must {

    "contain a back link pointing to another page" in {
      val f = fixture()
      f.doc.getElementById("back").attr("href") should be(url)
    }

    "display the correct question designator" in {
      val f = fixture()
      assertContainsMessages(f.doc, "date_of_disposal.question_number")
    }

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "date_of_disposal.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "date_of_disposal.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "date_of_disposal.guidance")
    }

    "contain a form that POSTs to the correct action" in {
      val f = fixture()
      val forms = f.doc.getElementsByTag("form")
      forms.size shouldBe 1
      val form = forms.first
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe routes.DateOfDisposalController.onSubmit().url
    }

    "contain a label for the date" in {
      val f = fixture()
      assertContainsMessages(f.doc, "date_of_disposal.label")
    }

    "contain a hint for the date" in {
      val f = fixture()
      assertContainsMessages(f.doc, "date_of_disposal.hint")
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
