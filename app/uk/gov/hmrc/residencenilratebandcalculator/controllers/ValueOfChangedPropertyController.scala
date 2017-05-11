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

import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_changed_property

@Singleton
class ValueOfChangedPropertyController @Inject()(override val appConfig: FrontendAppConfig,
                                                  val messagesApi: MessagesApi,
                                                  override val sessionConnector: SessionConnector,
                                                  override val navigator: Navigator,
                                                 implicit val application: Application) extends SimpleControllerBase[Int] {

  override val controllerId = Constants.valueOfChangedPropertyId

  override def form = () =>
    NonNegativeIntForm("value_of_changed_property.error.blank", "error.whole_pounds", "error.non_numeric")

  override def view(form: Option[Form[Int]], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    value_of_changed_property(appConfig, form, answerRows)
  }
}
