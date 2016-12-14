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

import uk.gov.hmrc.residencenilratebandcalculator.models.CalculationResult
import uk.gov.hmrc.residencenilratebandcalculator.views.html.results
import scala.language.reflectiveCalls

/**
  * Created by andy on 14/12/2016.
  */
class ResultsViewSpec extends HtmlSpec {
  def fixture() = new {
    val view = results(frontendAppConfig, Right(CalculationResult(10, 100)))(request, messages)
    val doc = asDocument(view)
  }

  "Results View" when {
    def thisFixture() = fixture()

    "rendered" must {
      "display the correct browser title" in {
        val f = thisFixture()
        assertEqualsMessage(f.doc, "title", "results.browser_title")
      }

      "contain a label for the value" in {
        val f = thisFixture()
        assertContainsMessages(f.doc, "results.residenceNilRateAmount.label")
      }
    }
  }
}
