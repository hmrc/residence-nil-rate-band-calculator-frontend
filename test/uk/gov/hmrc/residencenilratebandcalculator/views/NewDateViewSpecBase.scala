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

import java.time.LocalDate
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import org.scalatest.matchers
import matchers.should.Matchers.convertToAnyShouldWrapper

trait NewDateViewSpecBase extends NewViewSpecBase {

  val day = 1
  val month = 2
  val year = 2000
  val date: Date = Date(LocalDate.of(year, month, day))

  def datePage(createView: (Form[Date]) => HtmlFormat.Appendable,
               messageKeyPrefix: String,
               expectedFormAction: String,
               pageType: String = "dateOfDeath",
               form: Form[Date] = dateOfDeathForm, emptyForm: Form[Date]): Unit = {

    behave like questionPage[Date](createView, messageKeyPrefix, expectedFormAction, emptyForm)

    "behave like a date page" when {
      "rendered" must {
        "contain a label for the day" in {
          val doc = asDocument(createView(emptyForm))
          assertContainsLabel(doc, s"$pageType${".day"}", messages("date.day"))
        }

        "contain an input for the day" in {
          val doc = asDocument(createView(emptyForm))
          assertRenderedById(doc, s"$pageType${".day"}")
        }

        "contain a label for the month" in {
          val doc = asDocument(createView(emptyForm))
          assertContainsLabel(doc, s"$pageType${".month"}", messages("date.month"))
        }

        "contain an input for the month" in {
          val doc = asDocument(createView(emptyForm))
          assertRenderedById(doc, s"$pageType${".month"}")
        }

        "contain a label for the year" in {
          val doc = asDocument(createView(emptyForm))
          assertContainsLabel(doc, s"$pageType${".year"}", messages("date.year"))
        }

        "contain an input for the year" in {
          val doc = asDocument(createView(emptyForm))
          assertRenderedById(doc, s"$pageType${".year"}")
        }

        "not render an error summary" in {
          val doc = asDocument(createView(emptyForm))
          assertNotRenderedByCssSelector(doc, ".govuk-error-summary")
        }
      }

      "rendered with a value" must {
        "include the day value in the day input" in {
          val doc = asDocument(createView(form.fill(date)))
          doc.getElementById(s"$pageType${".day"}").attr("value") shouldBe day.toString
        }

        "include the month value in the month input" in {
          val doc = asDocument(createView(form.fill(date)))
          doc.getElementById(s"$pageType${".month"}").attr("value") shouldBe month.toString
        }

        "include the year value in the year input" in {
          val doc = asDocument(createView(form.fill(date)))
          doc.getElementById(s"$pageType${".year"}").attr("value") shouldBe year.toString
        }
      }

      "rendered with an error" must {
        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedByCssSelector(doc, ".govuk-error-summary")
        }
      }
    }
  }
}
