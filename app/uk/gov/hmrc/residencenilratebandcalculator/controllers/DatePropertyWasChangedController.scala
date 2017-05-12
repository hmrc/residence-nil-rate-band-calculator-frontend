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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import javax.inject.{Inject, Singleton}

import com.google.inject.Provider
import play.api.Application
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, Date, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_property_was_changed
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class DatePropertyWasChangedController @Inject()(override val appConfig: FrontendAppConfig,
                                         val messagesApi: MessagesApi,
                                         override val sessionConnector: SessionConnector,
                                         override val navigator: Navigator,
                                                 implicit val application: Provider[Application]) extends SimpleControllerBase[Date]{

  val controllerId: String = Constants.datePropertyWasChangedId

  def form = () => DateForm("error.date.day_invalid", "error.date.month_invalid", "error.date.year_invalid", "error.invalid_date")

  def view(form: Option[Form[Date]], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    date_property_was_changed(appConfig, form, answerRows)
  }
}
