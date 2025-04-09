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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.scalatestplus.play.PlaySpec

class TaxYearSpec extends PlaySpec {

  val DateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

  val DefaultToFromDateString = "2000 to 2001"

  "Tax year utils" must {
    "have tax year navigation" in {
      val startYear = 2000
      TaxYear(startYear).back(1).currentYear mustBe 1999
      TaxYear(startYear).forwards(3).currentYear mustBe 2003
    }

    "have check equality" in {
      val startYear = 2000
      val date      = LocalDate.parse("2001/01/01", DateFormatter)
      TaxYear(startYear).contains(date) mustBe true
    }

    "have correct start date" in {
      val startYear = 2000
      val date      = LocalDate.parse("2000/04/06", DateFormatter)
      TaxYear(startYear).starts mustBe date
    }

    "have correct range based on start date" in {
      val startYear     = 2000
      val expectedRange = startYear to startYear + 1
      TaxYear(startYear).yearRange mustBe expectedRange
    }

    "have correct to string impl" in {
      val startYear = 2000
      TaxYear(startYear).toString mustBe DefaultToFromDateString
    }

    "have current tax year check" in {
      class CurrentTaxYearUnderTest extends CurrentTaxYear {
        override def now: () => LocalDate = ???
      }

      val currentTaxYearUnderTest = new CurrentTaxYearUnderTest()
      val taxYearResult           = currentTaxYearUnderTest.taxYearFor(LocalDate.parse("2000/04/06", DateFormatter))
      taxYearResult.toString() mustBe DefaultToFromDateString
    }
  }

}
