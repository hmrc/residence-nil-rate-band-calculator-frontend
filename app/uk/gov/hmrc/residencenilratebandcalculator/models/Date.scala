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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.joda.time.{DateTimeFieldType, LocalDate}
import org.joda.time.format.DateTimeFormatterBuilder
import play.api.libs.json.Json

case class Date (day: Int, month: Int, year: Int)
{
  implicit val formats = Json.format[Date]

  val requiredYearLength = 4

  val dateFormatter = new DateTimeFormatterBuilder()
    .appendDayOfMonth(1)
    .appendLiteral(' ')
    .appendMonthOfYear(1)
    .appendLiteral(' ')
    .appendFixedDecimal(DateTimeFieldType.year(), requiredYearLength)
    .toFormatter

  def unapply: LocalDate = LocalDate.parse(s"$day $month $year", dateFormatter)
}

object Date {
  def apply(date: LocalDate): Date = Date(date.getDayOfMonth, date.getMonthOfYear, date.getYear)
}