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

import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.models.Date

trait DateViewSpecBase extends ViewSpecBase {

  def datePage(createView: (Option[Form[Date]]) => HtmlFormat.Appendable,
               messageKeyPrefix: String,
               expectedFormAction: String) = {

    behave like singleQuestionPage[Date](createView, messageKeyPrefix, expectedFormAction)

    "behave like a date page" when {
      "rendered" must {
        "contain a label for the day" in {
          val doc = asDocument(createView(None))
          assertContainsLabel(doc, "day", messages("date.day"))
        }

        "contain an input for the day" in {
          val doc = asDocument(createView(None))
          assertRenderedById(doc, "day")
        }

        "contain a label for the month" in {
          val doc = asDocument(createView(None))
          assertContainsLabel(doc, "month", messages("date.month"))
        }

        "contain an input for the month" in {
          val doc = asDocument(createView(None))
          assertRenderedById(doc, "month")
        }

        "contain a label for the year" in {
          val doc = asDocument(createView(None))
          assertContainsLabel(doc, "year", messages("date.year"))
        }

        "contain an input for the year" in {
          val doc = asDocument(createView(None))
          assertRenderedById(doc, "year")
        }
      }
    }
  }
}
