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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.results

import scala.language.reflectiveCalls

class ResultsViewSpec extends HtmlSpec {
  def fixture() = new {
    val view = results(frontendAppConfig, "£10.00", Nil)(request, messages)
    val doc = asDocument(view)
  }

  "Results View" when {
    def thisFixture() = fixture()

    "rendered" must {
      "display the correct browser title" in {
        val f = thisFixture()
        assertEqualsMessage(f.doc, "title", "results.browser_title")
      }

      "display the correct guidance" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.action.header", "results.action.guidance.form_435", "results.action.guidance.form_400",
          "results.action.guidance.continue", "results.print_prefix", "results.link_to_print", "results.print_suffix")
      }

      "display the correct information when there is no Residence Nil Rate Amount" in {
        val residenceNilRateAmount = "£0.00"
        val view = results(frontendAppConfig, residenceNilRateAmount, Nil)(request, messages)
        val doc = asDocument(view)

        assertContainsMessages(doc, "results.info.zero.header", "results.info.zero.guidance", "results.info.zero.threshold_change")
        assertContainsText(doc, messages("results.action.guidance", residenceNilRateAmount))
      }

      "display the correct information when there is a positive Residence Nil Rate Amount" in {
        val residenceNilRateAmount = "£10.00"
        val view = results(frontendAppConfig, residenceNilRateAmount, Nil)(request, messages)
        val doc = asDocument(view)

        assertContainsMessages(doc, "results.info.non_zero.header", "results.info.non_zero.guidance", "results.info.non_zero.threshold_change")
        assertContainsText(doc, messages("results.action.guidance", residenceNilRateAmount))
      }

      "contain a link to the exit questionnaire" in {
        val f = thisFixture()
        val links = f.doc.getElementsByAttributeValue("href", routes.ExitQuestionnaireController.onPageLoad().url)
        links.size shouldBe 1
        links.first.text shouldBe messages("site.finish")
      }
    }
  }
}
