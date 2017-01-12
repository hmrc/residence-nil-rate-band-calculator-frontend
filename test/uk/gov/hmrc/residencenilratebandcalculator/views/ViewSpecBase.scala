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

import play.twirl.api.HtmlFormat
import play.api.data.{Form, FormError}

import scala.reflect.ClassTag

trait ViewSpecBase extends HtmlSpec {

  val backUrl = "backUrl"
  val errorKey = "value"
  val errorMessage = "error.number"
  val error = FormError(errorKey, errorMessage)

  def rnrbPage[A: ClassTag](createView: (Option[Form[A]]) => HtmlFormat.Appendable,
                            messageKeyPrefix: String,
                            expectedGuidanceKeys: String*) = {

    "behave like a standard RNRB page" when {
      "rendered" must {
        "display the correct browser title" in {
          val doc = asDocument(createView(None))
          assertEqualsMessage(doc, "title", s"$messageKeyPrefix.browser_title")
        }

        "display the correct page title" in {
          val doc = asDocument(createView(None))
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title")
        }

        "display the correct guidance" in {
          val doc = asDocument(createView(None))
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }
      }
    }
  }

  def pageWithBackLink[A: ClassTag](createView: (Option[Form[A]]) => HtmlFormat.Appendable) = {

    "behave like a page with a back link" when {
      "rendered" must {
        "contain a back link pointing to another page" in {
          val doc = asDocument(createView(None))
          val backLink = doc.getElementById("back")
          backLink.attr("href") shouldBe backUrl
          backLink.hasClass("back-link") shouldBe true
        }
      }
    }
  }

  def singleQuestionPage[A: ClassTag](createView: (Option[Form[A]]) => HtmlFormat.Appendable,
                                      messageKeyPrefix: String,
                                      expectedFormAction: String) = {

    "behave like a page with a single question" when {
      "rendered" must {
        "contain the correct question designator" in {
          val doc = asDocument(createView(None))
          val questionDesignators = doc.getElementsByClass("pre-heading")
          questionDesignators.size shouldBe 1
          questionDesignators.first.text shouldBe messages(s"$messageKeyPrefix.question_number")
        }

        "contain a form that POSTs to the correct action" in {
          val doc = asDocument(createView(None))
          val forms = doc.getElementsByTag("form")
          forms.size shouldBe 1
          val form = forms.first
          form.attr("method") shouldBe "POST"
          form.attr("action") shouldBe expectedFormAction
        }

        "contain a legend for the question" in {
          val doc = asDocument(createView(None))
          val legends = doc.getElementsByTag("legend")
          legends.size shouldBe 1
          legends.first.text shouldBe messages(s"$messageKeyPrefix.label")
        }

        "contain a submit button" in {
          val doc = asDocument(createView(None))
          val input = assertRenderedByCssSelector(doc, "input[type=submit]")
        }
      }
    }
  }
}
