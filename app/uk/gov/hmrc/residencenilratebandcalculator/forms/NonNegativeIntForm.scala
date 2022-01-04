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

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter

import scala.util.Try

object NonNegativeIntForm extends FormErrorHelper {

  def nonNegativeIntFormatter(errorKeyBlank: String, errorKeyDecimal: String, errorKeyNonNumeric: String, errorKeyTooLarge: String) = new Formatter[Int] {

    def isInt(str: String) = {
      Try {str.toInt}.isSuccess
    }



    val intRegex = """^(\d+)$""".r
    val decimalRegex = """^(\d*\.\d*)$""".r
    def numberTooLarge(input: String) = {
      Try {BigDecimal(input) > 2147483647}.getOrElse(false)
    }

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) => s.trim.replace(",", "") match {
          case input if numberTooLarge(input) => produceError(key, errorKeyTooLarge)
          case intRegex(str) if isInt(str) => Right(str.toInt)
          case decimalRegex(_) => produceError(key, errorKeyDecimal)
          case _ => produceError(key, errorKeyNonNumeric)
        }
      }
    }

    def unbind(key: String, value: Int) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String, errorKeyDecimal: String, errorKeyNonNumeric: String, errorKeyTooLarge: String): Form[Int] =
    Form(single("value" -> of(nonNegativeIntFormatter(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric, errorKeyTooLarge))))
}
