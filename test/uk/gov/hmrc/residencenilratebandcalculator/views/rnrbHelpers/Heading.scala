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

import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.playComponents.heading

class Heading extends HtmlSpec {

  val heading = injector.instanceOf[heading]

  "Heading" when {
    "rendered" must {

      "show heading in medium size when it is of default size" in {
        val headingRow = heading("title")(messages)
        val doc = asDocument(headingRow)
        assertContainsText(doc, "title")
        assert(doc.select("h1").hasClass("govuk-heading-xl"))
      }

      "show heading in the given size when it is explicitly provided" in {
        val headingRow = heading("title", "heading-xlarge")(messages)
        val doc = asDocument(headingRow)
        assertContainsText(doc, "title")
        assert(doc.select("h1").hasClass("govuk-heading-xlarge"))
      }

    }
  }
}
