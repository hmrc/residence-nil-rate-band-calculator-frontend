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

package uk.gov.hmrc.residencenilratebandcalculator.forms.mappings

import org.joda.time.format.DateTimeFormatterBuilder
import org.joda.time.{DateTimeFieldType, LocalDate}
import play.api.data.Forms.{mapping, text}
import play.api.data.Mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationResult}
import uk.gov.hmrc.residencenilratebandcalculator.utils.FormHelpers

import scala.util.Try

object DateMapping {

  private val isYearValidPredicate: Int => Boolean = _ > 999
  private val isMonthValidPredicate: Int => Boolean = month => month > 0 && month < 13
  private val isDayValidPredicate: Int => Boolean = day => day > 0 && day < 32
  private val isYearWithinUpperBound: Int => Boolean = _ < 10000
  private val isYearValidAndWithinUpperBound: Int => Boolean = x => isYearValidPredicate(x) && isYearWithinUpperBound(x)

  private def parseTupleAsDate(dateAsTuple: (String, String, String)) = {
    val requiredYearLength = 4

    val dateFormatter = new DateTimeFormatterBuilder()
      .appendDayOfMonth(1)
      .appendLiteral(' ')
      .appendMonthOfYear(1)
      .appendLiteral(' ')
      .appendFixedDecimal(DateTimeFieldType.year(), requiredYearLength)
      .toFormatter

    dateAsTuple match {
      case (day: String, month: String, year: String) =>{
        Try(LocalDate.parse(s"$day $month $year", dateFormatter)).toOption
      }

    }
  }

  // scalastyle:off
 private def dateConstraint(errorBlankFieldKey: String,
                             errorInvalidCharsKey: String,
                             errorInvalidDayKey: String,
                             errorInvalidDayForMonthKey: String,
                             errorInvalidMonthKey: String,
                             errorInvalidYearKey: String,
                             errorInvalidAllKey: String,
                             errorInvalidYearUpperBound: String,
                             errorInvalidDayAndMonthKey: String,
                             errorInvalidDayAndYearKey: String,
                             errorInvalidMonthAndYearKey: String): Constraint[(String, String, String)] =
    Constraint[(String, String, String)](
      (dateAsTuple: (String, String, String)) => {
        FormHelpers.convertToNumbers(
          Seq(dateAsTuple._1, dateAsTuple._2, dateAsTuple._3),
          errorBlankFieldKey,
          errorInvalidCharsKey
        ) match {
          case Left(errorKey) => Invalid(errorKey)
          case Right(numericElements) =>
            lazy val day = numericElements.head
            lazy val month = numericElements(1)
            lazy val year = numericElements(2)

            if (!isYearValidPredicate(year) && !isMonthValidPredicate(month) && !isDayValidPredicate(day)) {
              Invalid(errorInvalidAllKey)
            } else if (!isMonthValidPredicate(month) && !isDayValidPredicate(day)) {
              Invalid(errorInvalidDayAndMonthKey)
            } else if (!isYearValidAndWithinUpperBound(year) && !isDayValidPredicate(day)) {
              Invalid(errorInvalidDayAndYearKey)
            } else if (!isYearValidAndWithinUpperBound(year) && !isMonthValidPredicate(month)) {
              Invalid(errorInvalidMonthAndYearKey)
            } else if (!isYearValidPredicate(year)) {
              Invalid(errorInvalidYearKey)
            } else if (!isMonthValidPredicate(month)) {
              Invalid(errorInvalidMonthKey)
            } else if (!isDayValidPredicate(day)) {
              Invalid(errorInvalidDayKey)
            } else if(!isYearWithinUpperBound(year)) {
              Invalid(errorInvalidYearUpperBound)
            } else {
              checkDateElementsMakeValidNonFutureDate(dateAsTuple, errorInvalidDayForMonthKey)
            }
        }
      }
    )
  // scalastyle:on

  private def checkDateElementsMakeValidNonFutureDate(dateAsTuple: (String, String, String),
                                                      errorInvalidDateKey: String): ValidationResult =
    parseTupleAsDate(dateAsTuple) match {
      case None => Invalid(errorInvalidDateKey)
      case _ => Valid
    }

  private def dateMapping(constraint: Constraint[(String, String, String)]) = mapping(
    "day" -> text,
    "month" -> text,
    "year" -> text
  )((day, month, year) => (day, month, year))(date => Option(date))
    .verifying(constraint)
    .transform[LocalDate]((date: (String, String, String)) => parseTupleAsDate(date).orNull,
    localDate => Option(localDate) match {
      case Some(date) => (localDate.dayOfMonth().get().toString, localDate.monthOfYear().get().toString, localDate.year().get().toString)
      case _ => ("", "", "")
    })


  /**
    * errorBlankFieldKey - if the date, or any portion of it, is blank
    * errorInvalidCharsKey - if any invalid chars in date
    * errorInvalidDayKey - if the day portion of the date is numeric but invalid, e.g. 33
    * errorInvalidDayForMonthKey - if the day portion of the date is numeric but invalid for the month, e.g. 30 for month of 2
    * errorInvalidMonthKey - if the month portion of the date is numeric but invalid, e.g. 13
    * errorInvalidYearKey - if the year potion of the date is numeric but of less than 4 digits
    * errorInvalidAllKey - if all portions of the date are numeric but invalid as described above
    * errorInvalidYearUpperBound - if year of date is greater than 2050
    * errorInvalidDayAndMonthKey - if day and month only are invalid for any reason
    * errorInvalidDayAndYearKey - if day and year only are invalid for any reason
    * errorInvalidMonthAndYearKey - if month and year only are invalid for any reason
    */
  // scalastyle:off
  def apply(errorBlankFieldKey: String,
            errorInvalidCharsKey: String,
            errorInvalidDayKey: String,
            errorInvalidDayForMonthKey: String,
            errorInvalidMonthKey: String,
            errorInvalidYearKey: String,
            errorInvalidAllKey: String,
            errorInvalidYearUpperBound: String,
            errorInvalidDayAndMonthKey: String,
            errorInvalidDayAndYearKey: String,
            errorInvalidMonthAndYearKey: String
           ) =
  dateMapping(
    dateConstraint(
      errorBlankFieldKey,
      errorInvalidCharsKey,
      errorInvalidDayKey,
      errorInvalidDayForMonthKey,
      errorInvalidMonthKey,
      errorInvalidYearKey,
      errorInvalidAllKey,
      errorInvalidYearUpperBound,
      errorInvalidDayAndMonthKey,
      errorInvalidDayAndYearKey,
      errorInvalidMonthAndYearKey
    )
  )
  // scalastyle:on

  val dateOfDeath: Mapping[LocalDate] = DateMapping(
    "date_of_death.error.date_not_complete",
    "date_of_death.error.only_using_numbers",
    "date_of_death.error.day_invalid",
    "date_of_death.error.month_days_invalid",
    "date_of_death.error.month_invalid",
    "date_of_death.error.year_invalid",
    "date_of_death.error.date_not_complete",
    "date_of_death.error.year_beyond_upper_bound",
    "date_of_death.error.day_and_month_invalid",
    "date_of_death.error.day_and_year_invalid",
    "date_of_death.error.month_and_year_invalid"
  )

  val downSizingDate: Mapping[LocalDate] = DateMapping(
    "date_of_downsizing.error.date_not_complete",
    "date_of_downsizing.error.only_using_numbers",
    "date_of_downsizing.error.day_invalid",
    "date_of_downsizing.error.month_days_invalid",
    "date_of_downsizing.error.month_invalid",
    "date_of_downsizing.error.year_invalid",
    "date_of_downsizing.error.date_not_complete",
    "date_of_downsizing.error.year_beyond_upper_bound",
    "date_of_downsizing.error.day_and_month_invalid",
    "date_of_downsizing.error.day_and_year_invalid",
    "date_of_downsizing.error.month_and_year_invalid"
  )
}
