/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import org.joda.time.LocalDate
import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import org.scalatest.Matchers.convertToAnyShouldWrapper


class DateFormSpec extends FormSpec {

  private def dateMap(fieldName: String, day: String, month: String, year: String) =
    Map(s"$fieldName.day" -> day, s"$fieldName.month" -> month, s"$fieldName.year" -> year)

  private def completeDate(fieldName:String) = dateMap(fieldName, "01", "01", "2015")

  private def dateFieldCheckDay(dateForm: Form[Date], fieldName:String, errorKeyPrefix: String) = {
    "give an error when the day is blank" in {
      val data = dateMap(fieldName, "", "01", "2014")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.date_not_complete")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the day is not supplied" in {
      val data = completeDate(fieldName) - s"$fieldName.day"
      val expectedErrors = error(s"$fieldName.day", "error.required")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the day is invalid" in {
      val data = dateMap(fieldName, "INVALID", "01", "2014")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.only_using_numbers")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the day is too high for the month" in {
      val data = dateMap(fieldName, "29", "22", "2013")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.month_invalid")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the day for month is invalid" in {
      val data = dateMap(fieldName, "31", "11", "2013")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.month_days_invalid")
      checkForError(dateForm, data, expectedErrors)
    }
  }

  private def dateFieldCheckMonth(dateForm: Form[Date], fieldName:String, errorKeyPrefix: String) = {
     "give an error when the month is blank" in {
      val data = dateMap(fieldName, "01", "", "2014")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.date_not_complete")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the month is not supplied" in {
      val data = completeDate(fieldName) - s"$fieldName.month"
      val expectedErrors = error(s"$fieldName.month", "error.required")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the month is invalid" in {
      val data = dateMap(fieldName, "01", "INVALID", "2014")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.only_using_numbers")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the month is too high" in {
      val data = dateMap(fieldName, "01", "13", "2013")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.month_invalid")
      checkForError(dateForm, data, expectedErrors)
    }
  }

  private def dateFieldCheckYear(dateForm: Form[Date], fieldName:String, errorKeyPrefix: String) = {
    "give an error when the year is blank" in {
      val data = dateMap(fieldName, "01", "01", "")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.date_not_complete")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the year is not supplied" in {
      val data = completeDate(fieldName) - s"$fieldName.year"
      val expectedErrors = error(s"$fieldName.year", "error.required")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the year is invalid" in {
      val data = dateMap(fieldName, "01", "01", "INVALID")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.only_using_numbers")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the year is beyond the upper bound" in {
      val data = dateMap(fieldName, "01", "01", "10000")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.year_beyond_upper_bound")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when the year is supplied as a two-digit number" in {
      val data = dateMap(fieldName, "01", "01", "14")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.year_invalid")
      checkForError(dateForm, data, expectedErrors)
    }
  }

  private def dateFieldCheckMultipleFields(dateForm: Form[Date], fieldName:String, errorKeyPrefix: String) = {
    "give only one error when three fields are invalid" in {
      val data = dateMap(fieldName, "32", "XX", "14")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.only_using_numbers")
      checkForError(dateForm, data, expectedErrors)
    }

    "give only one error when day and month only are invalid" in {
      val data = dateMap(fieldName, "32", "15", "2014")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.day_and_month_invalid")
      checkForError(dateForm, data, expectedErrors)
    }

    "give only one error when day and year only are invalid" in {
      val data = dateMap(fieldName, "32", "8", "10000")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.day_and_year_invalid")
      checkForError(dateForm, data, expectedErrors)
    }

    "give only one error when month and year only are invalid" in {
      val data = dateMap(fieldName, "15", "15", "10000")
      val expectedErrors = error(s"$fieldName", s"$errorKeyPrefix.error.month_and_year_invalid")
      checkForError(dateForm, data, expectedErrors)
    }

    "give an error when no date is supplied" in {
      val expectedErrors = error(s"$fieldName.day", "error.required") ++
        error(s"$fieldName.month", "error.required") ++
        error(s"$fieldName.year", "error.required")
      checkForError(dateForm, emptyForm, expectedErrors)
    }
  }

  private def dateField(dateForm: Form[Date], fieldName:String, errorKeyPrefix: String) = {
    "not give an error for a valid date" in {
      dateForm.bind(completeDate(fieldName)).get shouldBe Date(new LocalDate(2015, 1, 1))
    }
    dateFieldCheckMultipleFields(dateForm, fieldName, errorKeyPrefix)
    dateFieldCheckDay(dateForm, fieldName, errorKeyPrefix)
    dateFieldCheckMonth(dateForm, fieldName, errorKeyPrefix)
    dateFieldCheckYear(dateForm, fieldName, errorKeyPrefix)
  }

  "dateOfDeathForm" must {
    behave like dateField(dateOfDeathForm, "dateOfDeath", "date_of_death")
  }

  "dateOfDownsizingForm" must {
    behave like dateField(dateOfDownsizingForm, "dateOfDownsizing", "date_of_downsizing")
  }
}
