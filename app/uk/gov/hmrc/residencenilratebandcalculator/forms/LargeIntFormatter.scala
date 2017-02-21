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

import java.text.NumberFormat.getIntegerInstance

import play.api.data.FormError
import play.api.data.format.Formats.stringFormat
import play.api.data.format.Formatter

object LargeIntFormatter {
  private def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    stringFormat.bind(key, data).right.flatMap { s =>
      scala.util.control.Exception.allCatch[T]
        .either(parse(s))
        .left.map(_ => Seq(FormError(key, errMsg, errArgs)))
    }
  }

  private def numberFormatter[T](convert: String => T): Formatter[T] = {
    new Formatter[T] {
      override val format = Some("format.numeric" -> Nil)

      def bind(key: String, data: Map[String, String]) =
        parsing(convert, "error.number", Nil)(key, data)

      def unbind(key: String, value: T) = Map(key -> value.toString)
    }
  }

  implicit def largeIntFormat: Formatter[Int] = {
    numberFormatter(num => getIntegerInstance.parse(num).intValue())
  }

}
