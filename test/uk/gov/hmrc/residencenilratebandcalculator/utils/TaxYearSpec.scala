/*
 * Copyright 2020 HM Revenue & Customs
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

import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.DateTimeFormat
import uk.gov.hmrc.play.test.UnitSpec

class TaxYearSpec extends UnitSpec {

  val DateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd")

  val DefaultToFromDateString = "2000 to 2001"
  "Tax year utils" should {
    "have tax year navigation" in {
      val startYear = 2000
      TaxYear(startYear).back(1).currentYear shouldBe 1999
      TaxYear(startYear).forwards(3).currentYear shouldBe 2003
    }

    "have check equality" in {
      val startYear = 2000
      val date = LocalDate.parse("2001/01/01", DateFormatter)
      TaxYear(startYear).contains(date) shouldBe true
    }

    "have correct start date" in {
      val startYear = 2000
      val date = LocalDate.parse("2000/04/06", DateFormatter)
      TaxYear(startYear).starts shouldBe date
    }

    "have correct range based on start date" in {
      val startYear = 2000
      val expectedRange = startYear to startYear + 1
      TaxYear(startYear).yearRange shouldBe expectedRange
    }

    "have correct finish instant value" in {
      val startYear = 2000
      val expectedDate = LocalDate.parse("2001/04/06", DateFormatter)
      val dateString = TaxYear(startYear).finishInstant.toString("yyyy-MM-dd")
      LocalDate.parse(dateString) shouldBe expectedDate
    }

    "have correct start instant value" in {
      val startYear = 2000
      val expectedDate = LocalDate.parse("2000/04/06", DateFormatter)
      val dateString = TaxYear(startYear).startInstant.toString("yyyy-MM-dd")
      LocalDate.parse(dateString) shouldBe expectedDate
    }

    "have correct to string impl" in {
      val startYear = 2000
      TaxYear(startYear).toString shouldBe DefaultToFromDateString
    }

    "have current tax year check" in {
      class CurrentTaxYearUnderTest extends CurrentTaxYear{
        override def now: () => DateTime = ???
      }

      val currentTaxYearUnderTest = new CurrentTaxYearUnderTest()
      val taxYearResult = currentTaxYearUnderTest.taxYearFor(LocalDate.parse("2000/04/06", DateFormatter))
      taxYearResult.toString() shouldBe DefaultToFromDateString
    }
  }
}
