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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter

object PositivePercentForm extends FormErrorHelper {

  def positivePercentFormatter(errorKeyBlank: String, errorKeyNonNumeric: String, errorKeyOutOfRange: String) = new Formatter[BigDecimal] {

    val decimalRegex = """^(\d*\.?\d*)$""".r

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) => s.trim.replace(",", "") match {
          case decimalRegex(str) if BigDecimal(str) <= 0 || BigDecimal(str) > 100 => produceError(key, errorKeyOutOfRange)
          case decimalRegex(str) => Right(BigDecimal(str))
          case _ => produceError(key, errorKeyNonNumeric)
        }
      }
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String, errorKeyNonNumeric: String, errorKeyOutOfRange: String): Form[BigDecimal] =
    Form(single("value" -> of(positivePercentFormatter(errorKeyBlank, errorKeyNonNumeric, errorKeyOutOfRange))))

}
