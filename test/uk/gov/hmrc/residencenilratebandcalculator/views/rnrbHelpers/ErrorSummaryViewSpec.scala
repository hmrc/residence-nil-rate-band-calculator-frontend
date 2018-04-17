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

package uk.gov.hmrc.residencenilratebandcalculator.views.rnrbHelpers

import play.api.data.{Form, FormError}
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.rnrbHelpers.error_summary
import org.mockito.Mockito._

class ErrorSummaryViewSpec extends HtmlSpec {

  val errorKey1 = "key1"
  val errorKey2 = "key2"
  val message1 = "message1"
  val message2 = "message2"

  val error = FormError(errorKey1, message1)
  val error2 = FormError(errorKey2, message2)

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
        error_summary(createForm(Seq()))(messages).toString shouldBe "\n"
      }
    }

    "given an error" must {

      "render a title" in {
        val doc = asDocument(error_summary(createForm(Seq(error)))(messages))
        val heading = doc.getElementById("error-summary-heading")
        heading.text shouldBe messages("error.summary.title")
      }

      "render some help text" in {
        val doc = asDocument(error_summary(createForm(Seq(error)))(messages))
        assertContainsMessages(doc, "key1-error-summary")
      }

      "render a link to the error key" in {
        val doc = asDocument(error_summary(createForm(Seq(error)))(messages))
        val ul = doc.getElementsByClass("js-error-summary-messages").first
        val link = ul.getElementsByTag("li").first.getElementsByTag("a").first
        link.attr("href") shouldBe s"#$errorKey1"
        link.text shouldBe message1
      }
    }

    "given two errors" must {

      "render a title" in {
        val doc = asDocument(error_summary(createForm(Seq(error, error2)))(messages))
        val heading = doc.getElementById("error-summary-heading")
        heading.text shouldBe messages("error.summary.title")
      }

      "render some help text" in {
        val doc = asDocument(error_summary(createForm(Seq(error, error2)))(messages))
        assertContainsMessages(doc, "key2-error-summary")
      }

      "render a link to the first error key" in {
        val doc = asDocument(error_summary(createForm(Seq(error, error2)))(messages))
        val ul = doc.getElementsByClass("js-error-summary-messages").first
        val link = ul.getElementsByTag("li").first.getElementsByTag("a").first
        link.attr("href") shouldBe s"#$errorKey1"
        link.text shouldBe message1
      }

      "render a link to the second error key" in {
        val doc = asDocument(error_summary(createForm(Seq(error, error2)))(messages))
        val ul = doc.getElementsByClass("js-error-summary-messages").first
        val link = ul.getElementsByTag("li").get(1).getElementsByTag("a").first
        link.attr("href") shouldBe s"#$errorKey2"
        link.text shouldBe message2
      }
    }
  }
}
