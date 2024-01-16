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

package uk.gov.hmrc.residencenilratebandcalculator.models

import play.api.libs.json._

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

case class Date(date: LocalDate)

object Date {

  val dateReads: Reads[Date] = (json: JsValue) => {
    Try(LocalDate.parse(json.as[JsString].value)) match {
      case Success(javaLocalDate) => JsSuccess(Date(javaLocalDate))
      case Failure(e) => JsError(JsonValidationError(e.getMessage))
    }
  }

  val dateWrites: Writes[Date] = (date: Date) => {
    JsString(date.date.toString)
  }

  implicit val dateFormat: Format[Date] = Format(dateReads, dateWrites)
}
