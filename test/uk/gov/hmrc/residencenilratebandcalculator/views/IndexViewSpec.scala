/*
 * Copyright 2016 HM Revenue & Customs
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

import uk.gov.hmrc.residencenilratebandcalculator.views.html.index

import scala.language.reflectiveCalls

class IndexViewSpec extends HtmlSpec {

  def fixture() = new {
    val view = index(frontendAppConfig)(request, messages)
    val doc = asDocument(view)
  }

  "Index View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "index.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "index.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "index.guidance")
    }
  }
}