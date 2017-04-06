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

import javax.inject.Inject

import play.api.data.{Form, FormError}
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_inherited_property_value

import scala.concurrent.Future

class ChargeableInheritedPropertyValueController @Inject()(override val appConfig: FrontendAppConfig,
                                                                     val messagesApi: MessagesApi,
                                                                     override val sessionConnector: SessionConnector,
                                                                     override val navigator: Navigator) extends SimpleControllerBase[Int] {

  override val controllerId: String = Constants.chargeableInheritedPropertyValueId

  override def form: () => Form[Int] = () =>
    NonNegativeIntForm("chargeable_inherited_property_value.error.blank", "error.whole_pounds", "error.non_numeric")

  override def view(form: Option[Form[Int]], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) =
    chargeable_inherited_property_value(appConfig, form, answerRows)

  override def validate(value: Int, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Option[FormError] = {
    userAnswers.chargeablePropertyValue match {
      case None => throw new RuntimeException("Chargeable property value was not answered")
      case Some(v) if value > v => Some(FormError("value", "chargeable_inherited_property_value.greater_than_chargeable_property_value.error", Seq(v)))
      case _ => None
    }
  }
}
