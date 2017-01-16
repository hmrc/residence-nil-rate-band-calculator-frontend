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

import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.mvc.Call

case class AnswerRow(title: String, data: String, url: String)

object AnswerRow {
  def apply(titleKey: String, amount: Int, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), NumberFormat.getCurrencyInstance.format(amount), url.url)

  def apply(titleKey: String, yesNo: Boolean, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), if (yesNo) messages("site.yes") else messages("site.no"), url.url)

  def apply(titleKey: String, date: LocalDate, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), date.toString("d MMMM yyyy"), url.url)

  def apply(titleKey: String, percent: Double, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), s"$percent%", url.url)
}
