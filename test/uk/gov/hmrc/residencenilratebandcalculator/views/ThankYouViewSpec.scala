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

import uk.gov.hmrc.residencenilratebandcalculator.views.html.thank_you

class ThankYouViewSpec extends ViewSpecBase {

  val messageKeyPrefix = "thank_you"

  "Thank You view" must {
    "display the correct browser title" in {
      val doc = asDocument(thank_you()(request, messages, mockConfig))
      assertEqualsMessage(doc, "title", s"$messageKeyPrefix.browser_title")
    }

    "display the correct title" in {
      val doc = asDocument(thank_you()(request, messages, mockConfig))
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title")
    }

    "not display the HMRC logo" in {
      val doc = asDocument(thank_you()(request, messages, mockConfig))
      assertNotRenderedByCssSelector(doc, ".organisation-logo")
    }
  }
}
