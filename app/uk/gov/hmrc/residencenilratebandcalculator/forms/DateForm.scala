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

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.forms.FormValidators._

object DateForm {

  val upperBoundDays = 31
  val upperBoundMonths = 12
  val upperBoundYears = 2050

  val intRegex = """^(\d+)$""".r

  def produceError(key: String, error: String) = Left(Seq(FormError(key, error)))

  def datePartFormatter(errorKeyBlank: String, errorKeyOutOfRange: String, upperBound: Int): Formatter[Int] = new Formatter[Int] {

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(d) => d match {
          case intRegex(v) if v.toInt <= upperBound => Right(v.toInt)
          case _ => produceError(key, errorKeyOutOfRange)
        }
      }
    }

    def unbind(key: String, value: Int) = Map(key -> value.toString)
  }

  def apply(errorKeyInvalidDay: String, errorKeyInvalidMonth: String, errorKeyInvalidYear: String, errorKeyInvalidDate: String): Form[Date] = Form(
    mapping(
      "day" -> of(datePartFormatter("error.date.day_blank", errorKeyInvalidDay, upperBoundDays)),
      "month" -> of(datePartFormatter("error.date.month_blank", errorKeyInvalidMonth, upperBoundMonths)),
      "year" -> of(datePartFormatter("error.date.year_blank", errorKeyInvalidYear, upperBoundYears))
    )(Date.apply)(Date.unapply)
      .verifying(errorKeyInvalidDate, fields => isValidDate(fields.day, fields.month, fields.year))
  )
}
