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

package uk.gov.hmrc.residencenilratebandcalculator.models

import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.{TimeZone, ULocale}
import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.utils.{CurrencyFormatter, PercentageFormatter}


case class AnswerRow(title: String, data: String, url: String)

object AnswerRow {
  private def formattedDate(localDate: LocalDate, msgs: Messages) = {
    val defaultTimeZone = TimeZone.getTimeZone("Europe/London")

    val langCode: String = msgs.lang.code
    val validLang: Boolean = ULocale.getAvailableLocales.contains(new ULocale(langCode))
    val locale: ULocale = if (validLang) new ULocale(langCode) else ULocale.getDefault
    val sdf = new SimpleDateFormat("d MMMM y", locale)

    sdf.setTimeZone(defaultTimeZone)
    sdf.format(localDate.toDate)
  }

  def apply(titleKey: String, amount: Int, url: Call)(messages: Messages): AnswerRow = {
    AnswerRow(messages(titleKey), CurrencyFormatter.format(amount), url.url)
  }

  def apply(titleKey: String, yesNo: Boolean, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), if (yesNo) messages("site.yes") else messages("site.no"), url.url)

  def apply(titleKey: String, date: LocalDate, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), formattedDate(date, messages), url.url)

  def apply(titleKey: String, percent: Double, url: Call)(messages: Messages): AnswerRow = {
    AnswerRow(messages(titleKey), PercentageFormatter.format(percent), url.url)
  }

  def apply(titleKey: String, valueKey: String, url: Call)(messages: Messages): AnswerRow =
    AnswerRow(messages(titleKey), messages(valueKey), url.url)
}
