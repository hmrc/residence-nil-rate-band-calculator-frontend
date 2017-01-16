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
import uk.gov.hmrc.residencenilratebandcalculator.forms.ExitQuestionnaireForm
import uk.gov.hmrc.residencenilratebandcalculator.models.ExitQuestionnaire
import uk.gov.hmrc.residencenilratebandcalculator.views.html.exit_questionnaire

class ExitQuestionnaireViewSpec extends ViewSpecBase {

  val messageKeyPrefix = "exit_questionnaire"

  override val errorKey = "fullName"

  def createView(form: Option[Form[ExitQuestionnaire]]) = exit_questionnaire(frontendAppConfig, form)(request, messages)

  "Exit Questionnaire view" must {
    behave like rnrbPage[ExitQuestionnaire](createView, messageKeyPrefix)
  }

  "Exit Questionnaire view" when {
    "rendered" must {

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

      val serviceDifficulty = "a"
      val serviceFeel = "b"
      val comments = "c"
      val fullName = "d"
      val email = "e"
      val phoneNumber = "f"

      def filledForm = ExitQuestionnaireForm().fill(
        ExitQuestionnaire(Some(serviceDifficulty), Some(serviceFeel), Some(comments), Some(fullName), Some(email), Some(phoneNumber)))

      "include the form's full name in the correct input input" in {
        val doc = asDocument(createView(Some(filledForm)))
        doc.getElementById("fullName").attr("value") shouldBe fullName
      }

      "include the form's email in the correct input input" in {
        val doc = asDocument(createView(Some(filledForm)))
        doc.getElementById("email").attr("value") shouldBe email
      }

      "include the form's phone number in the correct input input" in {
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
