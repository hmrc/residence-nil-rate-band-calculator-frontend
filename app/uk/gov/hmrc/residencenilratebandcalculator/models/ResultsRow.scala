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

import java.text.NumberFormat
import java.util.Locale

import play.api.i18n.Messages
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter

case class ResultsRow(summary: String, data: String)

object ResultsRow {
  def apply(summaryKey: String, amount: Int)(messages: Messages): ResultsRow = {
    ResultsRow(messages(summaryKey), CurrencyFormatter.format(amount))
  }
}
