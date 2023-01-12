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

import org.joda.time.{DateTime, Interval, LocalDate}
import scala.collection.immutable.Range.Inclusive


case class TaxYear(startYear: Int) {
  lazy val finishYear: Int = startYear + 1
  lazy val finishes: LocalDate = new LocalDate(finishYear, 4, 5)
  lazy val previous: TaxYear = back(1)
  lazy val currentYear : Int = startYear
  lazy val next: TaxYear = forwards(1)

  def back(years: Int): TaxYear = TaxYear(startYear - years)

  def forwards(years: Int): TaxYear = TaxYear(startYear + years)

  def contains(date: LocalDate) = !(date.isBefore(starts) || date.isAfter(finishes))

  def starts: LocalDate = TaxYear.firstDayOfTaxYear(startYear)

  def yearRange: Inclusive = startYear to finishYear

  def interval = new Interval(startInstant, finishInstant)

  def finishInstant: DateTime = next.startInstant

  def startInstant: DateTime = starts.toDateTimeAtStartOfDay(TaxYear.ukTime)

  override def toString = s"$startYear to $finishYear"
}

object TaxYear extends CurrentTaxYear with (Int => TaxYear) {
  override def now = () => DateTime.now
}