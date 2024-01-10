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

package uk.gov.hmrc.residencenilratebandcalculator.forms.mappings

import java.time.LocalDate
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

  /**
    * A repository for the error message keys to be raised when validation fails:-
    *  errorBlankFieldKey - if the date, or any portion of it, is blank
    *  errorInvalidCharsKey - if any invalid chars in date
    *  errorInvalidDayKey - if the day portion of the date is numeric but invalid, e.g. 33
    *  errorInvalidDayForMonthKey - if the day portion of the date is numeric but invalid for the month, e.g. 30 for month of 2
    *  errorInvalidMonthKey - if the month portion of the date is numeric but invalid, e.g. 13
    *  errorInvalidYearKey - if the year potion of the date is numeric but of less than 4 digits
    *  errorInvalidAllKey - if all portions of the date are numeric but invalid as described above
    *  errorInvalidYearUpperBound - if year of date is greater than 2050
    *  errorInvalidDayAndMonthKey - if day and month only are invalid for any reason
    *  errorInvalidDayAndYearKey - if day and year only are invalid for any reason
    *  errorInvalidMonthAndYearKey - if month and year only are invalid for any reason.
    */
  private case class ValidationErrorMessageKeyProfile(errorBlankFieldKey: String,
                                         errorInvalidCharsKey: String,
                                         errorInvalidDayKey: String,
                                         errorInvalidDayForMonthKey: String,
                                         errorInvalidMonthKey: String,
                                         errorInvalidYearKey: String,
                                         errorInvalidAllKey: String,
                                         errorInvalidYearUpperBound: String,
                                         errorInvalidDayAndMonthKey: String,
                                         errorInvalidDayAndYearKey: String,
                                         errorInvalidMonthAndYearKey: String)

  private def parseTupleAsDate(dateAsTuple: (String, String, String)) = {
    Try(LocalDate.of(dateAsTuple._3.toInt, dateAsTuple._2.toInt, dateAsTuple._1.toInt)).toOption
  }

  private val checkForAllDateElementsInvalid:(Int,Int,Int,ValidationErrorMessageKeyProfile) => Option[String] =
    (day,month,year,errorKeys) =>
    (day, month, year) match {
      case (d, m, y)
        if !isYearValidPredicate(y) && !isMonthValidPredicate(m) && !isDayValidPredicate(d) =>
        Some(errorKeys.errorInvalidAllKey)
      case _ => None
    }

  private val checkForTwoDateElementsOnlyInvalid:(Int,Int,Int,ValidationErrorMessageKeyProfile) => Option[String] =
    (day,month,year,errorKeys) =>
    if (!isMonthValidPredicate(month) && !isDayValidPredicate(day)) {
      Some(errorKeys.errorInvalidDayAndMonthKey)
    } else if (!isYearValidAndWithinUpperBound(year) && !isDayValidPredicate(day)) {
      Some(errorKeys.errorInvalidDayAndYearKey)
    } else if (!isYearValidAndWithinUpperBound(year) && !isMonthValidPredicate(month)) {
      Some(errorKeys.errorInvalidMonthAndYearKey)
    } else {
      None
    }

  private val checkForIndividualDateElementsInvalid:(Int,Int,Int,ValidationErrorMessageKeyProfile)=>Option[String] =
    (day,month,year,errorKeys) =>
    (day, month, year) match {
      case (_, _, y) if !isYearValidPredicate(y) => Some(errorKeys.errorInvalidYearKey)
      case (_, m, _) if !isMonthValidPredicate(m) => Some(errorKeys.errorInvalidMonthKey)
      case (d, _, _) if !isDayValidPredicate(d) => Some(errorKeys.errorInvalidDayKey)
      case (_, _, y) if !isYearWithinUpperBound(y) => Some(errorKeys.errorInvalidYearUpperBound)
      case _ => None
    }

  private def firstValidationFailure(day: Int, month: Int, year: Int,
                                errorKeys: ValidationErrorMessageKeyProfile,
                                validationFunctions:Seq[(Int,Int,Int,ValidationErrorMessageKeyProfile)=>Option[String]]) = {
    validationFunctions.foldLeft[Option[String]](None){
      (currentState:Option[String], currentFunction:(Int, Int, Int, ValidationErrorMessageKeyProfile) => Option[String]) =>
      if(currentState.isEmpty) {
        currentFunction(day, month, year, errorKeys)
      } else {
        currentState
      }
    }
  }

  private def checkForDateElementsMakeValidDate(dateAsTuple: (String, String, String),
                                                errorKeys: ValidationErrorMessageKeyProfile): Option[String] =
    parseTupleAsDate(dateAsTuple) match {
      case None => Some(errorKeys.errorInvalidDayForMonthKey)
      case _ => None
    }

  private val validationFunctions =
    Seq(checkForAllDateElementsInvalid, checkForTwoDateElementsOnlyInvalid, checkForIndividualDateElementsInvalid)

  private def dateConstraint(errorKeys: ValidationErrorMessageKeyProfile): Constraint[(String, String, String)] =
    Constraint[(String, String, String)](
      (dateAsTuple: (String, String, String)) => {
        FormHelpers.convertToNumbers(
          Seq(dateAsTuple._1, dateAsTuple._2, dateAsTuple._3),
          errorKeys.errorBlankFieldKey,
          errorKeys.errorInvalidCharsKey
        ) match {
          case Left(errorKey) => Invalid(errorKey)
          case Right(numericElements) =>
            val optionInvalidErrorKey =
              firstValidationFailure(numericElements.head, numericElements(1), numericElements(2), errorKeys, validationFunctions)
              .fold(
                checkForDateElementsMakeValidDate(dateAsTuple, errorKeys)
              )(Some(_))
            optionInvalidErrorKey.fold[ValidationResult](Valid)(Invalid(_))
        }
      }
    )

  private def dateMapping(constraint: Constraint[(String, String, String)]) = mapping(
    "day" -> text,
    "month" -> text,
    "year" -> text
  )((day, month, year) => (day, month, year))(date => Option(date))
    .verifying(constraint)
    .transform[LocalDate]((date: (String, String, String)) => parseTupleAsDate(date).orNull,
      localDate => Option(localDate) match {
        case Some(date) =>
          (localDate.getDayOfMonth.toString, localDate.getMonthValue.toString, localDate.getYear.toString)
        case _ =>
          ("", "", "")
      })

  private def apply(validationErrorMessageKeyProfile: ValidationErrorMessageKeyProfile): Mapping[LocalDate] =
    dateMapping(dateConstraint(validationErrorMessageKeyProfile))

  private val dateOfDeathValidationErrorMessageKeyProfile = ValidationErrorMessageKeyProfile(
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

  private val downSizingDateValidationErrorMessageKeyProfile = ValidationErrorMessageKeyProfile(
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

  val dateOfDeath: Mapping[LocalDate] = DateMapping(dateOfDeathValidationErrorMessageKeyProfile)
  val downSizingDate: Mapping[LocalDate] = DateMapping(downSizingDateValidationErrorMessageKeyProfile)
}
