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

import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.views.html.before_you_start

class BeforeYouStartViewSpec extends ViewSpecBase {

  val messageKeyPrefix = "before_you_start"

  "Before You Start view" must {
    "display the correct browser title" in {
      val doc = asDocument(before_you_start(frontendAppConfig)(request, messages))
      assertEqualsMessage(doc, "title", s"$messageKeyPrefix.browser_title")
    }

    "display the correct title" in {
      val doc = asDocument(before_you_start(frontendAppConfig)(request, messages))
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title")
    }

    "display the correct guidance" in {
      val doc = asDocument(before_you_start(frontendAppConfig)(request, messages))
      assertContainsMessages(
        doc,
        s"$messageKeyPrefix.guidance1",
        s"$messageKeyPrefix.guidance2",
        s"$messageKeyPrefix.guidance3",
        s"$messageKeyPrefix.guidance3.bullet1",
        s"$messageKeyPrefix.guidance3.bullet2",
        s"$messageKeyPrefix.guidance3.bullet3",
        s"$messageKeyPrefix.guidance3.bullet4",
        s"$messageKeyPrefix.guidance4",
        s"$messageKeyPrefix.guidance4.bullet1",
        s"$messageKeyPrefix.guidance4.bullet2",
        s"$messageKeyPrefix.guidance4.bullet3"
      )
    }

    "display a Start button linking to the 'Date Of Death' page" in {
      val doc = asDocument(before_you_start(frontendAppConfig)(request, messages))
      val startLink = doc.getElementById("start")
      startLink.className shouldBe "button"
      startLink.attr("href") shouldBe routes.DateOfDeathController.onPageLoad().url
      startLink.text shouldBe messages("site.start")
    }
  }
}
