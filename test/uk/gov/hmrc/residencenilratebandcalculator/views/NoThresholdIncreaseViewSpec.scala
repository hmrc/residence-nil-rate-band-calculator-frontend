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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_threshold_increase

import scala.language.reflectiveCalls

class NoThresholdIncreaseViewSpec extends HtmlSpec {

  val prefix = "no_threshold_increase.direct_descendants"
  val no_threshold_increase: no_threshold_increase = injector.instanceOf[no_threshold_increase]
  def fixture(): Object {
    val view: HtmlFormat.Appendable

    val doc: Document
  } = new {
    val view: HtmlFormat.Appendable = no_threshold_increase(prefix)(request, messages)
    val doc: Document = asDocument(view)
  }

  "No Threshold Increase View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", s"${messages("no_threshold_increase.browser_title")} - ${messages("service.name")} - GOV.UK")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, s"$prefix.title")
    }

    "not display the HMRC logo" in {
      val f = fixture()
      assertNotRenderedByCssSelector(f.doc, ".organisation-logo")
    }

  }
}
