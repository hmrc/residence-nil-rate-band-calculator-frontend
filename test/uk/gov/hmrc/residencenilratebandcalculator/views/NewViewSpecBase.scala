/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalatest.Matchers.convertToAnyShouldWrapper
import play.api.data.{Form, FormError}
import play.api.i18n.Lang
import play.twirl.api.HtmlFormat

import scala.reflect.ClassTag

trait NewViewSpecBase extends HtmlSpec {

  val errorKey = "value"
  val errorMessage = "error.number"
  val error = FormError(errorKey, errorMessage)

  def rnrbPage[A: ClassTag](createView: Form[A] => HtmlFormat.Appendable,
                            messageKeyPrefix: String,
                            expectedGuidanceKeys: String*)(emptyForm: Form[A]): Unit = {
    "behave like a standard RNRB page" when {
      "rendered" must {
        "have the correct banner title" in {
          implicit val lang: Lang = Lang("en")

          val doc = asDocument(createView(emptyForm))
          val serviceName = doc.getElementsByClass("govuk-header__content").text
          serviceName shouldBe messagesApi("site.service_name")
        }

        "display the correct browser title" in {
          implicit val lang: Lang = Lang("en")
          val doc = asDocument(createView(emptyForm))
          doc.select("title").text shouldBe (messagesApi(s"$messageKeyPrefix.browser_title") + " - Calculate the available RNRB - GOV.UK")
          //assertEqualsMessage(doc, "title", s"$messageKeyPrefix.browser_title")
        }

        "display the correct page title" in {
          val doc = asDocument(createView(emptyForm))
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title")
        }

        "display the correct guidance" in {
          val doc = asDocument(createView(emptyForm))
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "not display the HMRC logo" in {
          val doc = asDocument(createView(emptyForm))
          assertNotRenderedByCssSelector(doc, ".organisation-logo")
        }
      }
    }
  }

  def pageWithoutBackLink[A: ClassTag](createView: (Form[A]) => HtmlFormat.Appendable, emptyForm: Form[A]): Unit = {

    "behave like a page without a back link" when {
      "rendered" must {
        "not contain a back link pointing to another page" in {
          val doc = asDocument(createView(emptyForm))
          assertNotRenderedById(doc, "back")
          assert(!doc.toString.contains(messages("site.back")))
        }
      }
    }
  }

  def questionPage[A: ClassTag](createView: Form[A] => HtmlFormat.Appendable,
                                messageKeyPrefix: String,
                                expectedFormAction: String, emptyForm: Form[A]): Unit = {

    "behave like a page with a question" when {
      "rendered" must {
        "contain a form that POSTs to the correct action" in {
          val doc = asDocument(createView(emptyForm))
          val forms = doc.getElementsByTag("form")
          forms.size shouldBe 1
          val form = forms.first
          form.attr("method") shouldBe "POST"
          form.attr("action") shouldBe expectedFormAction
        }

        "contain a submit button" in {
          val doc = asDocument(createView(emptyForm))
          val input = assertRenderedById(doc, "submit")
        }
      }
    }
  }

  def pageContainingPreviousAnswers[A: ClassTag](createView: Form[A] => HtmlFormat.Appendable, emptyForm: Form[A]): Unit = {
    "behave like a page containing previous answers" when {
      "rendered" must {
        "contain the Show previous answers link" in {
          val doc = asDocument(createView(emptyForm))
          assertContainsMessages(doc, "site.show_previous_answers")
        }
      }
    }

  }

}
