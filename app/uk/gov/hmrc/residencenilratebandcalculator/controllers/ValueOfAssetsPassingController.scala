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

import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.data.{Form, FormError}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_assets_passing

@Singleton
class ValueOfAssetsPassingController @Inject()(override val appConfig: FrontendAppConfig,
                                               val messagesApi: MessagesApi,
                                               override val sessionConnector: SessionConnector,
                                               override val navigator: Navigator) extends SimpleControllerBase[Int] {

  override val controllerId = Constants.valueOfAssetsPassingId

  override def form = () =>
    NonNegativeIntForm("value_of_assets_passing.error.blank", "error.whole_pounds", "error.non_numeric")

  override def view(form: Option[Form[Int]], backUrl: String, answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    value_of_assets_passing(appConfig, backUrl, form, answerRows)
  }

  override def validate(value: Int, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Option[FormError] = {
    userAnswers.valueOfEstate match {
      case None => throw new RuntimeException("Value of estate was not answered")
      case Some(v) if value > v => Some(FormError("value", "value_of_assets_passing.greater_than_estate_value.error", Seq(v)))
      case _ => None
    }
  }
}
