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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm

trait BooleanViewSpecBase extends ViewSpecBase {

  def booleanPage(createView: (Option[Form[Boolean]]) => HtmlFormat.Appendable,
                  messageKeyPrefix: String,
                  expectedFormAction: String, emptyForm: Option[Form[Boolean]] = None, useNewValues: Boolean = false) = {

    behave like questionPage[Boolean](createView, messageKeyPrefix, expectedFormAction, emptyForm)

    val yes = if(useNewValues) "value-true" else "yes"
    val no = if(useNewValues) "value-false" else "no"

    "behave like a page with a Yes/No question" when {
      "rendered" must {
        "contain a legend for the question" in {
          val doc = asDocument(createView(emptyForm))
          val legends = doc.getElementsByTag("legend")
          legends.size shouldBe 1
          legends.first.text shouldBe messages(s"$messageKeyPrefix.title")
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(emptyForm))
          assertRenderedById(doc, yes)
          assertRenderedById(doc, no)
        }

        "have no values checked when rendered with no form" in {
          val doc = asDocument(createView(emptyForm))
          assert(!doc.getElementById(yes).hasAttr("checked"))
          assert(!doc.getElementById(no).hasAttr("checked"))
        }

        "not render an error summary" in {
          val doc = asDocument(createView(emptyForm))
          assertNotRenderedById(doc, "error-summary_header")
        }
      }

      "rendered with a value of true" must {
        behave like answeredBooleanPage(createView, true, emptyForm, useNewValues)
      }

      "rendered with a value of false" must {
        behave like answeredBooleanPage(createView, false, emptyForm, useNewValues)
      }

      "rendered with an error" must {

        "show an error summary" in {
          val doc = asDocument(createView(Some(BooleanForm("").withError(error))))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error in the value field's label" in {
          val doc = asDocument(createView(Some(BooleanForm("").withError(error))))
          val errorSpan = doc.getElementsByClass("error-notification").first
          errorSpan.text shouldBe messages(errorMessage)
        }
      }
    }
  }

  def answeredBooleanPage(createView: (Option[Form[Boolean]]) => HtmlFormat.Appendable,
                          answer: Boolean, emptyForm: Option[Form[Boolean]] = None, useNewValues: Boolean = false) = {

    val yes = if(useNewValues) "value-true" else "yes"
    val no = if(useNewValues) "value-false" else "no"

    "have only the correct value checked" in {
      val doc = asDocument(createView(Some(BooleanForm("").fill(answer))))
      val test = asDocument(createView(Some(emptyForm).get))
      assert(doc.getElementById(yes).hasAttr("checked") == answer)
      assert(doc.getElementById(no).hasAttr("checked") != answer)
    }

    "not render an error summary" in {
      val doc = asDocument(createView(Some(BooleanForm("").fill(answer))))
      assertNotRenderedById(doc, "error-summary_header")
    }
  }
}
