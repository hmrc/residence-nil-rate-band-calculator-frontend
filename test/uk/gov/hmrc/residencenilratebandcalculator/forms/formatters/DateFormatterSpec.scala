/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.formatters

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.forms.FormSpec
import uk.gov.hmrc.residencenilratebandcalculator.forms.formatters.DateFormatter

class DateFormatterSpec extends FormSpec {

  object Errors {
    val dateRequiredError      = s"$testKey.error.required"
    val dayRequiredError       = s"$testKey.error.required.day"
    val dayMonthRequiredError  = s"$testKey.error.required.dayMonth"
    val dayYearRequiredError   = s"$testKey.error.required.dayYear"
    val monthRequiredError     = s"$testKey.error.required.month"
    val monthYearRequiredError = s"$testKey.error.required.monthYear"
    val yearRequiredError      = s"$testKey.error.required.year"

    val dateInvalidError      = s"$testKey.error.invalid"
    val dayInvalidError       = s"$testKey.error.invalid.day"
    val dayMonthInvalidError  = s"$testKey.error.invalid.dayMonth"
    val dayYearInvalidError   = s"$testKey.error.invalid.dayYear"
    val monthInvalidError     = s"$testKey.error.invalid.month"
    val monthYearInvalidError = s"$testKey.error.invalid.monthYear"
    val yearInvalidError      = s"$testKey.error.invalid.year"

    val dateNotRealError  = s"$testKey.error.notReal"
    val dayNotRealError   = s"$testKey.error.notReal.day"
    val monthNotRealError = s"$testKey.error.notReal.month"
    val yearNotRealError  = s"$testKey.error.notReal.year"
  }

  val testKey  = "testKey"
  val dayKey   = s"$testKey.day"
  val monthKey = s"$testKey.month"
  val yearKey  = s"$testKey.year"

  val testDate: LocalDate    = LocalDate.of(2000, 2, 1)
  val testMinDate: LocalDate = testDate.minusYears(1)
  val testMaxDate: LocalDate = testDate.plusYears(1)

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(SessionKeys.sessionId -> "id")
  def messages: Messages = fakeApplication().injector.instanceOf[MessagesApi].preferred(fakeRequest)

  val testFormatter: DateFormatter     = DateFormatter(testKey)(messages)
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", messages.lang.toLocale)

  "testFormatter.bind" when {
    "there is a single empty field" must {
      "return day required error when only the day is missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "",
            monthKey -> "1",
            yearKey  -> "2000"
          )
        ) mustBe Left(List(FormError(dayKey, Errors.dayRequiredError)))
      }
      "return month required error when only the month is missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "",
            yearKey  -> "2000"
          )
        ) mustBe Left(List(FormError(monthKey, Errors.monthRequiredError)))
      }
      "return year required error when only the year is missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "1",
            yearKey  -> ""
          )
        ) mustBe Left(List(FormError(yearKey, Errors.yearRequiredError)))
      }
    }
    "there are a multiple empty fields" must {
      "return day/month required errors when the day and month are missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "",
            monthKey -> "",
            yearKey  -> "2000"
          )
        ) mustBe Left(
          List(FormError(dayKey, Errors.dayMonthRequiredError), FormError(monthKey, Errors.dayMonthRequiredError))
        )
      }
      "return day/year required errors when the day and year are missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "",
            monthKey -> "1",
            yearKey  -> ""
          )
        ) mustBe Left(
          List(FormError(dayKey, Errors.dayYearRequiredError), FormError(yearKey, Errors.dayYearRequiredError))
        )
      }
      "return month/year required errors when the month and year are missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "",
            yearKey  -> ""
          )
        ) mustBe Left(
          List(FormError(monthKey, Errors.monthYearRequiredError), FormError(yearKey, Errors.monthYearRequiredError))
        )
      }
      "return global required error when the day, month and year are missing" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "",
            monthKey -> "",
            yearKey  -> ""
          )
        ) mustBe Left(List(FormError(testKey, Errors.dateRequiredError)))
      }
    }
    "there is a single non digit field" must {
      "return invalid error on the day key when only the day is invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "a",
            monthKey -> "1",
            yearKey  -> "2000"
          )
        ) mustBe Left(List(FormError(dayKey, Errors.dayInvalidError)))
      }
      "return invalid error on the month key when only the month is invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "b",
            yearKey  -> "2000"
          )
        ) mustBe Left(List(FormError(monthKey, Errors.monthInvalidError)))
      }
      "return invalid error on the year key when only the year is invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "1",
            yearKey  -> "c"
          )
        ) mustBe Left(List(FormError(yearKey, Errors.yearInvalidError)))
      }
    }
    "there are a multiple invalid fields" must {
      "return invalid errors on day/month keys when the day and month are invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "a",
            monthKey -> "b",
            yearKey  -> "2000"
          )
        ) mustBe Left(
          List(FormError(dayKey, Errors.dayMonthInvalidError), FormError(monthKey, Errors.dayMonthInvalidError))
        )
      }
      "return invalid errors on day/year keys when the day and year are invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "a",
            monthKey -> "1",
            yearKey  -> "c"
          )
        ) mustBe Left(
          List(FormError(dayKey, Errors.dayYearInvalidError), FormError(yearKey, Errors.dayYearInvalidError))
        )
      }
      "return invalid errors on month/year keys when the month and year are invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "b",
            yearKey  -> "c"
          )
        ) mustBe Left(
          List(FormError(monthKey, Errors.monthYearInvalidError), FormError(yearKey, Errors.monthYearInvalidError))
        )
      }
      "return global invalid error when the day, month and year are invalid" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "a",
            monthKey -> "b",
            yearKey  -> "c"
          )
        ) mustBe Left(List(FormError(testKey, Errors.dateInvalidError)))
      }
    }
    "there input does not form a real date" must {
      "return invalid error on the day key when only the day is not real" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "32",
            monthKey -> "1",
            yearKey  -> "2000"
          )
        ) mustBe Left(List(FormError(dayKey, Errors.dayNotRealError)))
      }
      "return invalid error on the month key when only the month is not real" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "13",
            yearKey  -> "2000"
          )
        ) mustBe Left(List(FormError(monthKey, Errors.monthNotRealError)))
      }
      "return invalid error on the year key when only the year is not real" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "1",
            yearKey  -> "10000"
          )
        ) mustBe Left(List(FormError(yearKey, Errors.yearNotRealError)))
      }
      "return global invalid error when the day, month and year are not real" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "0",
            monthKey -> "0",
            yearKey  -> "999"
          )
        ) mustBe Left(List(FormError(testKey, Errors.dateNotRealError)))
      }
      "return global invalid error when the inputs are real but do not form a real date" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "29",
            monthKey -> "2",
            yearKey  -> "2001"
          )
        ) mustBe Left(List(FormError(testKey, Errors.dateNotRealError)))
      }
    }
    "the date is valid" must {
      "return the date" in {
        testFormatter.bind(
          testKey,
          Map(
            dayKey   -> "1",
            monthKey -> "1",
            yearKey  -> "2000"
          )
        ) mustBe Right(LocalDate.of(2000, 1, 1))
      }
    }
    "the formatter does not have min and max dates" must {
      val formatter = DateFormatter(testKey)(messages)
      "return the date when date is valid in the future" in {
        formatter.bind(
          testKey,
          Map(
            dayKey   -> "2",
            monthKey -> "2",
            yearKey  -> "2001"
          )
        ) mustBe Right(LocalDate.of(2001, 2, 2))
      }
      "return the date when date is valid in the past" in {
        formatter.bind(
          testKey,
          Map(
            dayKey   -> "30",
            monthKey -> "1",
            yearKey  -> "1999"
          )
        ) mustBe Right(LocalDate.of(1999, 1, 30))
      }
    }
  }

  "testFormatter.unbind" must {
    "return a map with the correct day, month and year" in {
      testFormatter.unbind(testKey, testDate) mustBe Map(
        dayKey   -> testDate.getDayOfMonth.toString,
        monthKey -> testDate.getMonthValue.toString,
        yearKey  -> testDate.getYear.toString
      )
    }
  }

}
