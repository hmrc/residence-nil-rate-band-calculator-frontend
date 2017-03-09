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

package uk.gov.hmrc.residencenilratebandcalculator.views.rnrbHelpers

import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.rnrbHelpers.answer_rows

class RowsViewSpec extends HtmlSpec {
  "Rows View Spec" when {
    "rendered" must {

      "contain two rows when passed a seq containing a single row" in {
        val row = AnswerRow("Title", "Data", "http://www.example.com")
        val partial = answer_rows(Seq[AnswerRow](row))(messages)
        val doc = asDocument(partial)

        assert(doc.select("li").size() == 2)
        assertContainsText(doc, row.title)
        assertContainsText(doc, row.data)
        assertContainsText(doc, row.url)
      }

      "contains three rows when passed a seq containing 2 rows" in {
        val row1 = AnswerRow("Title1", "Data1", "http://www.example.com/1")
        val row2 = AnswerRow("Title2", "Data2", "http://www.example.com/2")
        val partial = answer_rows(Seq[AnswerRow](row1, row2))(messages)
        val doc = asDocument(partial)

        assert(doc.select("li").size() == 3)

        assertContainsText(doc, row1.title)
        assertContainsText(doc, row1.data)
        assertContainsText(doc, row1.url)

        assertContainsText(doc, row2.title)
        assertContainsText(doc, row2.data)
        assertContainsText(doc, row2.url)
      }
    }
  }
}
