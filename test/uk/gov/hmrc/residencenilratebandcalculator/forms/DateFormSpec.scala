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

import uk.gov.hmrc.residencenilratebandcalculator.models.Date

class DateFormSpec extends FormSpec {

  val errorKeyInvalidDay = "invalid day"
  val errorKeyInvalidMonth = "invalid month"
  val errorKeyInvalidYear = "invalid year"
  val errorKeyInvalidDate = "invalid date"

  def date(day: String, month: String, year: String) = Map("day" -> day, "month" -> month, "year" -> year)

  "Date Form" must {

    "bind valid values" in {
      val form = DateForm(errorKeyInvalidDay, errorKeyInvalidMonth, errorKeyInvalidYear, errorKeyInvalidDate).bind(date("01", "01", "2000"))
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
  }
}
