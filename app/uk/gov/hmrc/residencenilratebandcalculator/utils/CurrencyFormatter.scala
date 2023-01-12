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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
  def format(amount: Int): String = {
    val formatter = NumberFormat.getCurrencyInstance(Locale.UK)
    formatter.setMaximumFractionDigits(0)
    formatter.format(amount)
  }

  def format(amountStr: String): String = {
    this.format(Integer.parseInt(amountStr))
  }
}
