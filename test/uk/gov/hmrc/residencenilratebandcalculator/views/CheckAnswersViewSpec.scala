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

import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow
import uk.gov.hmrc.residencenilratebandcalculator.views.html.check_answers

class CheckAnswersViewSpec extends HtmlSpec {
  "Check Answers View" when {
    "rendered" must {
      "display the correct browser title" in {
        val view = check_answers(frontendAppConfig, Nil, Call("", ""))(request, messages)
        val doc = asDocument(view)
        assertEqualsMessage(doc, "title", "check_answers.browser_title")
      }

      "display the correct page title" in {
        val view = check_answers(frontendAppConfig, Nil, Call("", ""))(request, messages)
        val doc = asDocument(view)
        assertPageTitleEqualsMessage(doc, "check_answers.title")
      }

      "display the given rows" in {
        val view = check_answers(frontendAppConfig, Seq[AnswerRow](), Call("", ""))(request, messages)
        val doc = asDocument(view)

        doc.select("ul.tablular-data").size shouldBe 1
      }

      "contain a button pointing to the results page" in {
        val view = check_answers(frontendAppConfig, Seq[AnswerRow](), routes.ResultsController.onPageLoad())(request, messages)
        val doc = asDocument(view)

        doc.select(s"""a[href="${routes.ResultsController.onPageLoad().url}"""").size shouldBe 1
      }
    }
  }
}
