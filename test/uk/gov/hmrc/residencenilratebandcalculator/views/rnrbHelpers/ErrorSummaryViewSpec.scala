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

import org.mockito.Mockito._
import play.api.data.{Form, FormError}
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.playComponents.error_summary

class ErrorSummaryViewSpec extends HtmlSpec {

  val errorKey1 = "key1"
  val errorKey2 = "key2"
  val message1 = "message1"
  val message2 = "message2"

  val error: FormError = FormError(errorKey1, message1)
  val error2: FormError = FormError(errorKey2, message2)

  val error_summary: error_summary = injector.instanceOf[error_summary]

  def createForm(errors: Seq[FormError]): Form[Int] = {
    val form = mock[Form[Int]]
    when(form.errors).thenReturn(errors)
    if (errors.isEmpty) {
      when(form.hasErrors).thenReturn(false)
    } else {
      when(form.hasErrors).thenReturn(true)
    }

    form
  }



  "Error summary" when {

    "given no errors" must {

      "not render anything" in {
        error_summary(Seq())(messages).toString.trim mustBe ""
      }
    }

    "given an error" must {

      "render a title" in {
        val doc = asDocument(error_summary(Seq(error))(messages))
        val heading = doc.getElementsByClass("govuk-error-summary__title")
        heading.text mustBe messages("error.summary.title")
      }

      "render some help text" in {
        val doc = asDocument(error_summary(Seq(error))(messages))
        assertContainsMessages(doc, "#key1")
      }

      "render a link to the error key" in {
        val doc = asDocument(error_summary(Seq(error))(messages))
        val ul = doc.getElementsByClass("govuk-list govuk-error-summary__list").first
        val link = ul.getElementsByTag("li").first.getElementsByTag("a").first
        link.attr("href") mustBe s"#$errorKey1"
        link.text mustBe message1
      }
    }

    "given two errors" must {

      "render a title" in {
        val doc = asDocument(error_summary(Seq(error, error2))(messages))
        val heading = doc.getElementsByClass("govuk-error-summary__title")
        heading.text mustBe messages("error.summary.title")
      }

      "render some help text" in {
        val doc = asDocument(error_summary(Seq(error, error2))(messages))
        assertContainsMessages(doc, "#key2")
      }

      "render a link to the first error key" in {
        val doc = asDocument(error_summary(Seq(error, error2))(messages))
        val ul = doc.getElementsByClass("govuk-list govuk-error-summary__list").first
        val link = ul.getElementsByTag("li").first.getElementsByTag("a").first
        link.attr("href") mustBe s"#$errorKey1"
        link.text mustBe message1
      }

      "render a link to the second error key" in {
        val doc = asDocument(error_summary(Seq(error, error2))(messages))
        val ul = doc.getElementsByClass("govuk-list govuk-error-summary__list").first
        val link = ul.getElementsByTag("li").get(1).getElementsByTag("a").first
        link.attr("href") mustBe s"#$errorKey2"
        link.text mustBe message2
      }
    }
  }
}
