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

package uk.gov.hmrc.residencenilratebandcalculator.views.rnrbHelpers

import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.playComponents.answer_rows

class AnswerRowsViewSpec extends HtmlSpec {

  val answer_rows: answer_rows = injector.instanceOf[answer_rows]

  "Answer Rows View Spec" when {
    "rendered" must {

      "contain four description details when passed a seq containing a single row" in {
        val row = AnswerRow("Title", "Data", "http://www.example.com")
        val partial = answer_rows(Seq[AnswerRow](row))(messages)
        val doc = asDocument(partial)
        assert(doc.select("dd").size() == 4)
        assertContainsText(doc, row.title)
        assertContainsText(doc, row.data)
        assertContainsText(doc, row.url)
      }

      "contains six description details when passed a seq containing 2 rows" in {
        val row1 = AnswerRow("Title1", "Data1", "http://www.example.com/1")
        val row2 = AnswerRow("Title2", "Data2", "http://www.example.com/2")
        val partial = answer_rows(Seq[AnswerRow](row1, row2))(messages)
        val doc = asDocument(partial)

        assert(doc.select("dd").size() == 6)

        assertContainsText(doc, row1.title)
        assertContainsText(doc, row1.data)
        assertContainsText(doc, row1.url)

        assertContainsText(doc, row2.title)
        assertContainsText(doc, row2.data)
        assertContainsText(doc, row2.url)
      }

      "display the last item in the Answer Rows collection as the first (non-heading) item in the previous answers" in {
        val row1 = AnswerRow("Title1", "Data1", "http://www.example.com/1")
        val row2 = AnswerRow("Title2", "Data2", "http://www.example.com/2")
        val partial = answer_rows(Seq[AnswerRow](row1, row2))(messages)
        val doc = asDocument(partial)
        assertEqualsValue(doc, "details div dl div:first-child dt", messages("site.previous_answers"))
        assertEqualsValue(doc, "details div dl div:nth-child(2) dt", row2.title)
        assertEqualsValue(doc, "details div dl div:nth-child(3) dt", row1.title)
      }
    }
  }
}
