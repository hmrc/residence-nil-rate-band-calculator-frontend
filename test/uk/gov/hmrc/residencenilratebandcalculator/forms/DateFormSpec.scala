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

class DateFormSpec extends FormSpec {

  val errorKeyInvalidDay = "invalid day"
  val errorKeyInvalidMonth = "invalid month"
  val errorKeyInvalidYear = "invalid year"
  val errorKeyInvalidDate = "invalid date"

  def date(day: String, month: String, year: String) = Map("day" -> day, "month" -> month, "year" -> year)

  def dateOfDeath(day: String, month: String, year: String) =
    Map("dateOfDeath.day" -> day, "dateOfDeath.month" -> month, "dateOfDeath.year" -> year)

  lazy val completeDateOfDeath = dateOfDeath("01", "01", "2015")

  "Date of Death form" must {

    "not give an error for a valid date" in {
      dateOfDeathForm.bind(completeDateOfDeath).get shouldBe Date(new LocalDate(2015, 1, 1))
    }

    "give an error when the day is blank" in {
      val data = dateOfDeath("", "01", "2014")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveFull")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day is not supplied" in {
      val data = completeDateOfDeath - "dateOfDeath.day"
      val expectedErrors = error("dateOfDeath.day", "error.required")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day is invalid" in {
      val data = dateOfDeath("INVALID", "01", "2014")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectDateUsingOnlyNumbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the day is too high for the month" in {
      val data = dateOfDeath("29", "02", "2013")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectDayForMonth")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is blank" in {
      val data = dateOfDeath("01", "", "2014")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveFull")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is not supplied" in {
      val data = completeDateOfDeath - "dateOfDeath.month"
      val expectedErrors = error("dateOfDeath.month", "error.required")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is invalid" in {
      val data = dateOfDeath("01", "INVALID", "2014")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectDateUsingOnlyNumbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the month is too high" in {
      val data = dateOfDeath("01", "13", "2013")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectMonth")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is blank" in {
      val data = dateOfDeath("01", "01", "")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveFull")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is not supplied" in {
      val data = completeDateOfDeath - "dateOfDeath.year"
      val expectedErrors = error("dateOfDeath.year", "error.required")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is invalid" in {
      val data = dateOfDeath("01", "01", "INVALID")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectDateUsingOnlyNumbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when the year is supplied as a two-digit number" in {
      val data = dateOfDeath("01", "01", "14")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectYear")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give only one error when two fields are invalid" in {
      val data = dateOfDeath("32", "XX", "14")
      val expectedErrors = error("dateOfDeath", "error.dateOfDeath.giveCorrectDateUsingOnlyNumbers")

      checkForError(dateOfDeathForm, data, expectedErrors)
    }

    "give an error when no data is supplied" in {
      val expectedErrors = error("dateOfDeath.day", "error.required") ++
        error("dateOfDeath.month", "error.required") ++
        error("dateOfDeath.year", "error.required")

      checkForError(dateOfDeathForm, emptyForm, expectedErrors)
    }
  }


 /* "Date Form" must {

    "bind valid values" in {
      val form =
        DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate).bind(date("01", "01", "2000"))
      form.get shouldBe Date(1, 1, 2000)
    }

    "fail to bind a negative day value" in {
      val expectedError = error("day", errorKeyInvalidDay)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("-1", "01", "2000"), expectedError)
    }

    "fail to bind a negative month value" in {
      val expectedError = error("month", errorKeyInvalidMonth)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("1", "-1", "2000"), expectedError)
    }

    "fail to bind a negative year value" in {
      val expectedError = error("year", errorKeyInvalidYear)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("1", "1", "-1"), expectedError)
    }

    "fail to bind a non existant date" in {
      val expectedError = error("", errorKeyInvalidDate)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("30", "2", "2000"), expectedError)
    }

    "fail to bind a non-numeric day" in {
      val expectedError = error("day", errorKeyInvalidDay)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("one", "1", "2000"), expectedError)
    }

    "fail to bind a non-numeric month" in {
      val expectedError = error("month", errorKeyInvalidMonth)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("1", "one", "2000"), expectedError)
    }

    "fail to bind a non-numeric year" in {
      val expectedError = error("year", errorKeyInvalidYear)
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("1", "1", "one"), expectedError)
    }

    "fail to bind a date with a blank day" in {
      val expectedError = error("day", "error.date.day_blank")
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("", "1", "2000"), expectedError)
    }

    "fail to bind a date with a blank month" in {
      val expectedError = error("month", "error.date.month_blank")
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("1", "", "2000"), expectedError)
    }

    "fail to bind a date with a blank year" in {
      val expectedError = error("year", "error.date.year_blank")
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), date("1", "1", ""), expectedError)
    }

    "fail to bind when day is omitted" in {
      val data = Map[String, String]("month" -> "1", "year" -> "2000")
      val expectedError = error("day", "error.date.day_blank")
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), data, expectedError)
    }

    "fail to bind when month is omitted" in {
      val data = Map[String, String]("day" -> "1", "year" -> "2000")
      val expectedError = error("month", "error.date.month_blank")
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), data, expectedError)
    }

    "fail to bind when year is omitted" in {
      val data = Map[String, String]("day" -> "1", "month" -> "1")
      val expectedError = error("year", "error.date.year_blank")
      checkForError(DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate), data, expectedError)
    }
  }*/
}
