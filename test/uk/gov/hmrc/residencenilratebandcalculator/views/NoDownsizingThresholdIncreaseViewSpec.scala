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

import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_downsizing_threshold_increase

class NoDownsizingThresholdIncreaseViewSpec extends HtmlSpec {
  implicit val msg = messages
  val messageKeyPrefix = "no_downsizing_threshold_increase"

  "No Downsizing Threshold Increase View" must {
    "display the correct browser title" in {
      val doc = asDocument(no_downsizing_threshold_increase("", Call("", ""), Seq()))
      assertEqualsMessage(doc, "title", s"$messageKeyPrefix.browser_title")
    }

    "display the correct page title" in {
      val doc = asDocument(no_downsizing_threshold_increase("", Call("", ""), Seq()))
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title")
    }

    "display the correct reason" in {
      val doc = asDocument(no_downsizing_threshold_increase(s"$messageKeyPrefix.no_property_reason", Call("", ""), Seq()))
      assertContainsText(doc, messages(s"$messageKeyPrefix.no_property_reason"))
    }

    "contain a link to the given page" in {
      val exampleURL = "http://www.example.com"
      val doc = asDocument(no_downsizing_threshold_increase("", Call("GET", exampleURL), Seq()))
      assertContainsText(doc, exampleURL)
    }

    "contain the Show previous answers link" in {
      val doc = asDocument(no_downsizing_threshold_increase("", Call("", ""), Seq()))
      assertContainsMessages(doc, "site.show_previous_answers")
    }


    "not display the HMRC logo" in {
      val doc = asDocument(no_downsizing_threshold_increase("", Call("", ""), Seq()))
      assertNotRenderedByCssSelector(doc, ".organisation-logo")
    }
  }
}
