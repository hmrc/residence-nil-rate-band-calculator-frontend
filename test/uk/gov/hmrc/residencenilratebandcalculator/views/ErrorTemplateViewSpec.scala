/*
 * Copyright 2019 HM Revenue & Customs
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

import uk.gov.hmrc.residencenilratebandcalculator.views.html.error_template

import scala.language.reflectiveCalls

class ErrorTemplateViewSpec extends HtmlSpec {

  def fixture() = new {
    val view = error_template("title", "heading", "message")(request, messages)
    val doc = asDocument(view)
  }

  "Error Template" must {
    "display the correct page title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "h1", "heading")
    }

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "title")
    }

    "display the correct message" in {
      val f = fixture()
      assertContainsText(f.doc, "message")
    }
  }
}
