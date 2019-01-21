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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import org.joda.time.{DateTime, DateTimeZone, LocalDate, MonthDay}

trait CurrentTaxYear {
  final val ukTime: DateTimeZone = DateTimeZone.forID("Europe/London")
  private val startOfTaxYear = new MonthDay(4, 6)
  def now: () => DateTime

  final def current: TaxYear = taxYearFor(today)

  final def today = new LocalDate(now(), ukTime)
  final def taxYearFor(date: LocalDate): TaxYear = {
    if (date isBefore firstDayOfTaxYear(date.getYear)) {
      TaxYear(startYear = date.getYear - 1)
    } else {
      TaxYear(startYear = date.getYear)
    }
  }

  final def firstDayOfTaxYear(year: Int) = startOfTaxYear.toLocalDate(year)
}