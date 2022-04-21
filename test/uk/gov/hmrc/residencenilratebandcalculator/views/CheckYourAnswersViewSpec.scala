/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.views.html.check_your_answers
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.ThresholdCalculationResultController
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow

class CheckYourAnswersViewSpec extends HtmlSpec {
  val messageKeyPrefix = "check_your_answers"
  val check_your_answers = injector.instanceOf[check_your_answers]
  val numberOfRows = 22
  val answerRows: Seq[AnswerRow] = (1 to numberOfRows).map {
    i => AnswerRow(titleKey = s"title$i", amount = i, url = Call("GET", s"url$i"))(messages)
  }
  def createView() = check_your_answers(answerRows)(request, messages)

  "Check Your Answers View" must {
    "display the correct browser title" in {
      val doc = asDocument(createView())
      assertEqualsMessage(doc, "title",
        s"${messages(s"$messageKeyPrefix.browser_title")} - ${messages("service.name")} - GOV.UK")
    }
    "display the correct page title" in {
      val doc = asDocument(createView())
      assertPageTitleEqualsMessage(doc, s"${messages(s"$messageKeyPrefix.title")}")
    }
    "display a continue link" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, ".govuk-button")
    }
    "link to the threshold-calculation-result page" in {
      val doc = asDocument(createView())
      val continueLink = doc.getElementsByClass("govuk-button").get(0)
      assert(continueLink.attr("href") == ThresholdCalculationResultController.onPageLoad().url)
    }
    "display the all answer rows" in {
      val doc = asDocument(createView())
      assert(doc.getElementsByClass("govuk-summary-list__row").size() == answerRows.size)
    }
    "provide links to change answers" in {
      val doc = asDocument(createView())
      for (i <- 1 to numberOfRows) {
        val answerRow = doc.getElementsByClass("govuk-summary-list__row").get(i - 1)
        assert(answerRow.getElementsByClass("govuk-link").get(0).attr("href") == s"url$i")
      }
    }
  }
}
