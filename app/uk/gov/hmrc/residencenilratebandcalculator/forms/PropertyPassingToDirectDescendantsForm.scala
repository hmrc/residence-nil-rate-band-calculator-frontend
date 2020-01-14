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
import play.api.data.format.Formats._
import play.api.data.format.Formatter
import uk.gov.hmrc.residencenilratebandcalculator.forms.FormValidators._

object PropertyPassingToDirectDescendantsForm extends FormErrorHelper {

  def propertyPassingToDirectDescendantsFormatter = new Formatter[String] {

    def bind(key: String, data: Map[String, String]) = data.get(key) match {
        case Some(s) if isValidPropertyPassingToDirectDescendantsOption(s) => Right(s)
        case _ => produceError(key, "error.invalid_property_passing_to_direct_descendants_option")
      }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] =
    Form(single("value" -> of(propertyPassingToDirectDescendantsFormatter)))
}
