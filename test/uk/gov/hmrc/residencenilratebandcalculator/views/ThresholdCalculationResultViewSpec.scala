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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.views.html.threshold_calculation_result

import scala.language.reflectiveCalls

class ThresholdCalculationResultViewSpec extends HtmlSpec {

  val threshold_calculation_result: threshold_calculation_result = injector.instanceOf[threshold_calculation_result]

  val view: HtmlFormat.Appendable = threshold_calculation_result("£10.00")(request, messages)
  val doc: Document               = asDocument(view)

  "Threshold Calculation Result View" when {
    "rendered" must {
      "display the correct browser title" in
        assertEqualsMessage(
          doc,
          "title",
          s"${messages("threshold_calculation_result.info.non_zero.header")} - ${messages("service.name")} - GOV.UK"
        )

      "display the correct guidance" in
        assertContainsMessages(
          doc,
          "threshold_calculation_result.action.header",
          "threshold_calculation_result.action.guidance.form_435",
          "threshold_calculation_result.action.guidance.form_400",
          "threshold_calculation_result.action.guidance.continue",
          "threshold_calculation_result.print_prefix",
          "threshold_calculation_result.link_to_print",
          "threshold_calculation_result.print_suffix"
        )

      "display the correct information when there is no Residence Nil Rate Amount" in {
        val residenceNilRateAmount = "£0.00"
        val view                   = threshold_calculation_result(residenceNilRateAmount)(request, messages)
        val doc                    = asDocument(view)

        assertContainsMessages(
          doc,
          "threshold_calculation_result.info.zero.header",
          "threshold_calculation_result.info.zero.threshold_change"
        )
        assertContainsText(doc, messages("threshold_calculation_result.info.zero.guidance", residenceNilRateAmount))
        assertContainsText(doc, messages("threshold_calculation_result.action.guidance", residenceNilRateAmount))
      }

      "display the correct information when there is a positive Residence Nil Rate Amount" in {
        val residenceNilRateAmount = "£10.00"
        val view                   = threshold_calculation_result(residenceNilRateAmount)(request, messages)
        val doc                    = asDocument(view)

        assertContainsMessages(
          doc,
          "threshold_calculation_result.info.non_zero.header",
          "threshold_calculation_result.info.non_zero.threshold_change"
        )
        assertContainsText(doc, messages("threshold_calculation_result.info.non_zero.guidance", residenceNilRateAmount))
        assertContainsText(doc, messages("threshold_calculation_result.action.guidance", residenceNilRateAmount))
      }

      "contain a link to the exit questionnaire" in {
        val links = doc.getElementsByAttributeValue("href", routes.FeedbackSurveyController.redirectExitSurvey.url)
        links.size mustBe 1
        links.first.text mustBe messages("site.finish")
      }

      "not display the HMRC logo" in
        assertNotRenderedByCssSelector(doc, ".organisation-logo")

    }
  }

}
