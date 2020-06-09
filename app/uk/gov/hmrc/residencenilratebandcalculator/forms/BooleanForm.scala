/*
 * Copyright 2020 HM Revenue & Customs
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

object BooleanForm extends FormErrorHelper {
  def booleanFormat(errorKey: String): Formatter[Boolean] = new Formatter[Boolean] {

    override val format = Some(("format.boolean", Nil))

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case Some("true") => Right(true)
        case Some("false") => Right(false)
        case Some(_) => produceError(key, errorKey)
        case None => produceError(key, errorKey)
      }
    }

    def unbind(key: String, value: Boolean) = Map(key -> value.toString)
  }

  def apply(errorKey: String): Form[Boolean] = Form(single("value" -> of(booleanFormat(errorKey))))
}
