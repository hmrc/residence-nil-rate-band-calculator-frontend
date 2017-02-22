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

case class DisplayResults(residenceNilRateAmount: String, resultsRows: Seq[ResultsRow], answerRows: Seq[AnswerRow])

object DisplayResults {
  def apply(calculationResult: CalculationResult, answers: Seq[AnswerRow])(messages: Messages): DisplayResults = {
    val adjustedAllowanceRow =
      if (calculationResult.defaultAllowanceAmount == calculationResult.adjustedAllowanceAmount) { Seq() }
      else { Seq(ResultsRow("results.adjustedAllowanceAmount.label", calculationResult.adjustedAllowanceAmount)(messages)) }
    DisplayResults(NumberFormat.getCurrencyInstance(Locale.UK).format(calculationResult.residenceNilRateAmount),
      Seq(
        ResultsRow("results.applicableNilRateBandAmount.label", calculationResult.applicableNilRateBandAmount)(messages),
        ResultsRow("results.defaultAllowanceAmount.label", calculationResult.defaultAllowanceAmount)(messages),
        ResultsRow("results.carryForwardAmount.label", calculationResult.carryForwardAmount)(messages)
      ) ++ adjustedAllowanceRow,
      answers)
  }
}
