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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.joda.time.LocalDate
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

case class DateFull(dateOfDeath: LocalDate)

object DateFull {

  val dateReads = new Reads[DateFull] {
    override def reads(json: JsValue) = {
      Try(LocalDate.parse(json.as[JsString].value)) match {
        case Success(jodaLocalDate) => JsSuccess(DateFull(jodaLocalDate))
        case Failure(e) => JsError(ValidationError(e.getMessage))
      }
    }
  }

  val dateWrites = new Writes[DateFull] {
    override def writes(date: DateFull) = {
      JsString(date.dateOfDeath.toString)
    }
  }

  implicit val dateFormat: Format[DateFull] = Format(dateReads, dateWrites)

  //implicit val formats = Json.format[DateFull]
}


