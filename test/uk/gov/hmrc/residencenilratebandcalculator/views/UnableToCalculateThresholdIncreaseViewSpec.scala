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

package uk.gov.hmrc.residencenilratebandcalculator.views

import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.views.html.unable_to_calculate_threshold_increase

import scala.language.reflectiveCalls

class UnableToCalculateThresholdIncreaseViewSpec extends HtmlSpec {

  val prefix = "unable_to_calculate_threshold_increase.grossing_up"
  val unable_to_calculate_threshold_increase: unable_to_calculate_threshold_increase = injector.instanceOf[unable_to_calculate_threshold_increase]
  val view: HtmlFormat.Appendable = unable_to_calculate_threshold_increase(prefix)(request, messages)
  val doc: Document = asDocument(view)

  "Unable To Calculate Threshold Increase View" must {

    "display the correct browser title" in {
      assertEqualsMessage(doc, "title", messages("unable_to_calculate_threshold_increase.browser_title") + " - Calculate the available RNRB - GOV.UK")
    }

    "display the correct page title" in {
      assertPageTitleEqualsMessage(doc, s"$prefix.title")
    }

    "display the correct guidance" in {
      assertContainsMessages(doc,
        s"$prefix.next.title",
        s"$prefix.next.guidance1",
        s"$prefix.next.guidance2",
        s"$prefix.next.guidance3.addr1",
        "HM Revenue &amp; Customs",
        s"$prefix.next.guidance3.addr3",
        s"$prefix.next.guidance4",
        s"$prefix.next.guidance5"
       )
    }

    "not display the HMRC logo" in {
      assertNotRenderedByCssSelector(doc, ".organisation-logo")
    }

  }
}
