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

import java.text.NumberFormat
import java.util.Locale

import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationResult, DisplayResults}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.results

import scala.language.reflectiveCalls

class ResultsViewSpec extends HtmlSpec {
  def fixture() = new {
    val view = results(frontendAppConfig, DisplayResults(CalculationResult(10, 50, 300, 260, 15), Nil)(messages))(request, messages)
    val doc = asDocument(view)
  }

  "Results View" when {
    def thisFixture() = fixture()

    "rendered" must {
      "display the correct browser title" in {
        val f = thisFixture()
        assertEqualsMessage(f.doc, "title", "results.browser_title")
      }

      "display the correct page title" in {
        val f = thisFixture()
        assertPageTitleEqualsMessage(f.doc, "results.title", NumberFormat.getCurrencyInstance(Locale.UK).format(10))
      }

      "display the correct guidance" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.guidance")
      }

      "contain an amount for the value" in {
        val f = thisFixture()
        assertContainsText(f.doc, "10")
      }

      "contain a label for the carry forward amount" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.carryForwardAmount.label")
      }

      "contain an amount for the carry forward amount" in {
        val f = thisFixture()
        assertContainsText(f.doc, "300")
      }

      "contain a label for the applicable nil rate band amount" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.applicableNilRateBandAmount.label")
      }

      "contain an amount for the applicable nil rate band amount" in {
        val f = thisFixture()
        assertContainsText(f.doc, "50")
      }

      "contain an amount for the default allowance amount" in {
        val f = thisFixture()
        assertContainsText(f.doc, "260")
      }

      "contain a label for the adjusted allowance" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.adjustedAllowanceAmount.label")
      }

      "contain an amount for the adjusted allowance" in {
        val f = thisFixture()
        assertContainsText(f.doc, "15")
      }

      "contain headers for your answers" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.question_header", "results.amount_header")
      }

      "contain a link to the exit questionnaire" in {
        val f = thisFixture()
        val links = f.doc.getElementsByAttributeValue("href", routes.ExitQuestionnaireController.onPageLoad().url)
        links.size shouldBe 1
        links.first.text shouldBe messages("results.link_to_exit_questionnaire.text")
      }
    }
  }
}
