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
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.ExitQuestionnaireForm
import uk.gov.hmrc.residencenilratebandcalculator.models.ExitQuestionnaire
import uk.gov.hmrc.residencenilratebandcalculator.views.html.exit_questionnaire

class ExitQuestionnaireViewSpec extends ViewSpecBase {

  val messageKeyPrefix = "exit_questionnaire"

  override val errorKey = "fullName"

  def createView(form: Option[Form[ExitQuestionnaire]]) = exit_questionnaire(frontendAppConfig, form)(request, messages, applicationProvider)

  "Exit Questionnaire view" must {
    behave like rnrbPage[ExitQuestionnaire](createView, messageKeyPrefix,
      "guidance", "comments.help.2", "user_research.title", "user_research.guidance")
  }

  "Exit Questionnaire view" when {
    "rendered" must {

      "contain a 'no thanks' link" in {
        val doc = asDocument(createView(None))
        val links = doc.select("a.button")
        links.size shouldBe 1
        links.first.text shouldBe messages("service.no_thanks")
        links.first.attr("href") shouldBe Constants.callExitService.url
      }

      "contain a legend for the service feel" in {
        val doc = asDocument(createView(None))
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.service_feel.label"))
      }

      "contain radio buttons for service feel" in {
        val doc = asDocument(createView(None))
        assertContainsRadioButton(doc, "service_feel.very_satisfied", "serviceFeel", "very_satisfied", false)
        assertContainsRadioButton(doc, "service_feel.satisfied", "serviceFeel", "satisfied", false)
        assertContainsRadioButton(doc, "service_feel.neither", "serviceFeel", "neither", false)
        assertContainsRadioButton(doc, "service_feel.dissatisfied", "serviceFeel", "dissatisfied", false)
        assertContainsRadioButton(doc, "service_feel.very_dissatisfied", "serviceFeel", "very_dissatisfied", false)
      }

      "contain a legend for the service difficulty" in {
        val doc = asDocument(createView(None))
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.service_difficulty.label"))
      }

      "contain radio buttons for service difficuly" in {
        val doc = asDocument(createView(None))
        assertContainsRadioButton(doc, "service_difficulty.very_easy", "serviceDifficulty", "very_easy", false)
        assertContainsRadioButton(doc, "service_difficulty.easy", "serviceDifficulty", "easy", false)
        assertContainsRadioButton(doc, "service_difficulty.neither", "serviceDifficulty", "neither", false)
        assertContainsRadioButton(doc, "service_difficulty.difficult", "serviceDifficulty", "difficult", false)
        assertContainsRadioButton(doc, "service_difficulty.very_difficult", "serviceDifficulty", "very_difficult", false)
      }

      "contain a label for the comments" in {
        val doc = asDocument(createView(None))
        assertContainsLabel(doc, "comments", messages(s"$messageKeyPrefix.comments.label"))
      }

      "contain a textarea for the comments" in {
        val doc = asDocument(createView(None))
        assertRenderedById(doc, "comments")
      }

      "contain a label for the full name" in {
        val doc = asDocument(createView(None))
        assertContainsLabel(doc, "fullName", messages(s"$messageKeyPrefix.full_name.label"))
      }

      "contain an input for the full name" in {
        val doc = asDocument(createView(None))
        assertRenderedById(doc, "fullName")
      }

      "contain a label for the email" in {
        val doc = asDocument(createView(None))
        assertContainsLabel(doc, "email", messages(s"$messageKeyPrefix.email.label"))
      }

      "contain an input for the email" in {
        val doc = asDocument(createView(None))
        assertRenderedById(doc, "email")
      }

      "contain a label for the phone number" in {
        val doc = asDocument(createView(None))
        assertContainsLabel(doc, "phoneNumber", messages(s"$messageKeyPrefix.phone_number.label"))
      }

      "contain an input for the phone number" in {
        val doc = asDocument(createView(None))
        assertRenderedById(doc, "phoneNumber")
      }
    }

    "rendered with a valid form" must {

      val serviceDifficulty = ExitQuestionnaire.EASY
      val serviceFeel = ExitQuestionnaire.SATISFIED
      val comments = "c"
      val fullName = "d"
      val email = "e"
      val phoneNumber = "f"

      def filledForm = ExitQuestionnaireForm().fill(
        ExitQuestionnaire(Some(serviceDifficulty), Some(serviceFeel), Some(comments), Some(fullName), Some(email), Some(phoneNumber)))

      "include the form's full name in the correct input" in {
        val doc = asDocument(createView(Some(filledForm)))
        doc.getElementById("fullName").attr("value") shouldBe fullName
      }

      "include the form's email in the correct input" in {
        val doc = asDocument(createView(Some(filledForm)))
        doc.getElementById("email").attr("value") shouldBe email
      }

      "include the form's phone number in the correct input" in {
        val doc = asDocument(createView(Some(filledForm)))
        doc.getElementById("phoneNumber").attr("value") shouldBe phoneNumber
      }
    }

    "rendered with an error" must {
      "show an error summary" in {
        val doc = asDocument(createView(Some(ExitQuestionnaireForm().withError(error))))
        assertRenderedById(doc, "error-summary-heading")
      }
    }
  }
}
