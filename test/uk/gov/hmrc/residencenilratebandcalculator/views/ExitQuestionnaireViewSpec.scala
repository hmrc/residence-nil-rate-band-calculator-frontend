/*
 * Copyright 2018 HM Revenue & Customs
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

  def createView(form: Form[ExitQuestionnaire]) = exit_questionnaire(frontendAppConfig, form)(request, messages, applicationProvider, localPartialRetriever)

  "Exit Questionnaire view" must {
    behave like rnrbPage[ExitQuestionnaire](createView, messageKeyPrefix,
      "guidance", "comments.help.2", "user_research.title", "user_research.guidance")(ExitQuestionnaireForm.apply())
  }

  "Exit Questionnaire view" when {
    "rendered" must {

      "contain a 'no thanks' link" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        val links = doc.select("a.button")
        links.size shouldBe 1
        links.first.text shouldBe messages("service.no_thanks")
        links.first.attr("href") shouldBe Constants.callExitService.url
      }

      "contain a service improvement intro" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.improve_service.label"))
      }

      "contain a legend for the service feel" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.service_feel.label"))
      }

      "contain radio buttons for service feel" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsRadioButton(doc, "serviceFeel-very_satisfied", "serviceFeel", "very_satisfied", false)
        assertContainsRadioButton(doc, "serviceFeel-satisfied", "serviceFeel", "satisfied", false)
        assertContainsRadioButton(doc, "serviceFeel-neither", "serviceFeel", "neither", false)
        assertContainsRadioButton(doc, "serviceFeel-dissatisfied", "serviceFeel", "dissatisfied", false)
        assertContainsRadioButton(doc, "serviceFeel-very_dissatisfied", "serviceFeel", "very_dissatisfied", false)
      }

      "contain a legend for the service difficulty" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsMessages(doc, messages(s"$messageKeyPrefix.service_difficulty.label"))
      }

      "contain radio buttons for service difficuly" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsRadioButton(doc, "serviceDifficulty-very_easy", "serviceDifficulty", "very_easy", false)
        assertContainsRadioButton(doc, "serviceDifficulty-easy", "serviceDifficulty", "easy", false)
        assertContainsRadioButton(doc, "serviceDifficulty-neither", "serviceDifficulty", "neither", false)
        assertContainsRadioButton(doc, "serviceDifficulty-difficult", "serviceDifficulty", "difficult", false)
        assertContainsRadioButton(doc, "serviceDifficulty-very_difficult", "serviceDifficulty", "very_difficult", false)
      }

      "contain a label for the comments" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsLabel(doc, "comments", messages(s"$messageKeyPrefix.comments.label"))
      }

      "contain a textarea for the comments" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertRenderedById(doc, "comments")
      }

      "contain a label for the full name" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsLabel(doc, "fullName", messages(s"$messageKeyPrefix.full_name.label"))
      }

      "contain an input for the full name" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertRenderedById(doc, "fullName")
      }

      "contain a label for the email" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsLabel(doc, "email", messages(s"$messageKeyPrefix.email.label"))
      }

      "contain an input for the email" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertRenderedById(doc, "email")
      }

      "contain a label for the phone number" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
        assertContainsLabel(doc, "phoneNumber", messages(s"$messageKeyPrefix.phone_number.label"))
      }

      "contain an input for the phone number" in {
        val doc = asDocument(createView(ExitQuestionnaireForm.apply()))
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
        val doc = asDocument(createView(filledForm))
        doc.getElementById("fullName").attr("value") shouldBe fullName
      }

      "include the form's email in the correct input" in {
        val doc = asDocument(createView(filledForm))
        doc.getElementById("email").attr("value") shouldBe email
      }

      "include the form's phone number in the correct input" in {
        val doc = asDocument(createView(filledForm))
        doc.getElementById("phoneNumber").attr("value") shouldBe phoneNumber
      }
    }

    "rendered with an error" must {
      "show an error summary" in {
        val doc = asDocument(createView(ExitQuestionnaireForm().withError(error)))
        assertRenderedById(doc, "error-summary-heading")
      }
    }
  }
}
