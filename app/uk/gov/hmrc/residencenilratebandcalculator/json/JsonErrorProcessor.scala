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

package uk.gov.hmrc.residencenilratebandcalculator.json

import play.api.libs.json.{JsPath, JsonValidationError}

object JsonErrorProcessor {
  private def validationErrorToString(v: JsonValidationError): String = {
    v.messages.foldLeft(new StringBuilder())(_ append _).toString()
  }

  private def errorTupleToString(t: (JsPath, Seq[JsonValidationError])): String = {
    val validationErrors = t._2.map(validationErrorToString).foldLeft(new StringBuilder())(_ append _).toString()
    "JSON error: " + validationErrors + "\n"
  }

  def apply(errs: Seq[(JsPath, Seq[JsonValidationError])]): String = {
    errs.map(errorTupleToString).foldLeft(new StringBuilder())(_ append _).toString()
  }
}
