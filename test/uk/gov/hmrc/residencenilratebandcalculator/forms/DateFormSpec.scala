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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import org.joda.time.LocalDate
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import scala.language.reflectiveCalls

class DateFormSpec extends FormSpec {

  def date(day: String, month: String, year: String) = Map("day" -> day, "month" -> month, "year" -> year)

  def dateOfDeath(day: String, month: String, year: String) =
    Map("dateOfDeath.day" -> day, "dateOfDeath.month" -> month, "dateOfDeath.year" -> year)

  def dateOfDownsizing(day: String, month: String, year: String) =
    Map("dateOfDownsizing.day" -> day, "dateOfDownsizing.month" -> month, "dateOfDownsizing.year" -> year)

  lazy val completeDateOfDeath = dateOfDeath("01", "01", "2015")
  lazy val completeDateOfDownsizing = dateOfDownsizing("01", "01", "2015")

  "dateOfDeathForm" must {

    "not give an error for a valid date" in {
      dateOfDeathForm.bind(completeDateOfDeath).get shouldBe Date(new LocalDate(2015, 1, 1))
    }

    "give an error when the day is blank" in {
      val data = dateOfDeath("", "01", "2014")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.date_not_complete")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day is not supplied" in {
      val data = completeDateOfDeath - "dateOfDeath.day"
      val expectedErrors = error("dateOfDeath.day", "error.required")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day is invalid" in {
      val data = dateOfDeath("INVALID", "01", "2014")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.only_using_numbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day is too high for the month" in {
      val data = dateOfDeath("29", "22", "2013")
      val expectedErrors = error("dateOfDeath", "date_of_death.error.month_invalid")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is blank" in {
      val data = dateOfDeath("01", "", "2014")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.date_not_complete")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is not supplied" in {
      val data = completeDateOfDeath - "dateOfDeath.month"
      val expectedErrors = error("dateOfDeath.month", "error.required")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is invalid" in {
      val data = dateOfDeath("01", "INVALID", "2014")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.only_using_numbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day for month is invalid" in {
      val data = dateOfDeath("31", "11", "2013")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.month_days_invalid")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is too high" in {
      val data = dateOfDeath("01", "13", "2013")
      val expectedErrors = error("dateOfDeath", "date_of_death.error.month_invalid")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is blank" in {
      val data = dateOfDeath("01", "01", "")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.date_not_complete")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is not supplied" in {
      val data = completeDateOfDeath - "dateOfDeath.year"
      val expectedErrors = error("dateOfDeath.year", "error.required")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is invalid" in {
      val data = dateOfDeath("01", "01", "INVALID")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.only_using_numbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is beyond the upper bound" in {
      val data = dateOfDeath("01", "01", "10000")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.year_beyond_upper_bound")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is supplied as a two-digit number" in {
      val data = dateOfDeath("01", "01", "14")
      val expectedErrors = error("dateOfDeath", "date_of_death.error.year_invalid")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give only one error when two fields are invalid" in {
      val data = dateOfDeath("32", "XX", "14")
      val expectedErrors = error("dateOfDeath", "date_Of_death.error.only_using_numbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when no data is supplied" in {
      val expectedErrors = error("dateOfDeath.day", "error.required") ++
        error("dateOfDeath.month", "error.required") ++
        error("dateOfDeath.year", "error.required")

      checkForError(dateOfDeathForm, emptyForm, expectedErrors)
    }
  }

  "dateOfDownsizingForm" must {

    "not give an error for a valid date" in {
      dateOfDownsizingForm.bind(completeDateOfDownsizing).get shouldBe Date(new LocalDate(2015, 1, 1))
    }

    "give an error when the day is blank" in {
      val data = dateOfDownsizing("", "01", "2014")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.date_not_complete")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the day is not supplied" in {
      val data = completeDateOfDownsizing - "dateOfDownsizing.day"
      val expectedErrors = error("dateOfDownsizing.day", "error.required")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the day is invalid" in {
      val data = dateOfDownsizing("INVALID", "01", "2014")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.only_using_numbers")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the day is too high for the month" in {
      val data = dateOfDownsizing("29", "22", "2013")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.month_invalid")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the month is blank" in {
      val data = dateOfDownsizing("01", "", "2014")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.date_not_complete")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the month is not supplied" in {
      val data = completeDateOfDownsizing - "dateOfDownsizing.month"
      val expectedErrors = error("dateOfDownsizing.month", "error.required")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the month is invalid" in {
      val data = dateOfDownsizing("01", "INVALID", "2014")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.only_using_numbers")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the month is too high" in {
      val data = dateOfDownsizing("01", "13", "2013")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.month_invalid")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the day for month is invalid" in {
      val data = dateOfDownsizing("31", "11", "2013")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.month_days_invalid")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the year is blank" in {
      val data = dateOfDownsizing("01", "01", "")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.date_not_complete")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the year is not supplied" in {
      val data = completeDateOfDownsizing - "dateOfDownsizing.year"
      val expectedErrors = error("dateOfDownsizing.year", "error.required")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the year is invalid" in {
      val data = dateOfDownsizing("01", "01", "INVALID")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.only_using_numbers")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the year is beyond the upper bound" in {
      val data = dateOfDownsizing("01", "01", "10000")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.year_beyond_upper_bound")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when the year is supplied as a two-digit number" in {
      val data = dateOfDownsizing("01", "01", "14")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.year_invalid")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give only one error when two fields are invalid" in {
      val data = dateOfDownsizing("32", "XX", "14")
      val expectedErrors = error("dateOfDownsizing", "date_of_downsizing.error.only_using_numbers")

      checkForError(dateOfDownsizingForm, data, expectedErrors)
    }

    "give an error when no data is supplied" in {
      val expectedErrors = error("dateOfDownsizing.day", "error.required") ++
        error("dateOfDownsizing.month", "error.required") ++
        error("dateOfDownsizing.year", "error.required")

      checkForError(dateOfDownsizingForm, emptyForm, expectedErrors)
    }
  }
}
