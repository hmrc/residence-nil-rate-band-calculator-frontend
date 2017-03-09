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
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_value_of_residence_closely_inherited

import scala.concurrent.Future

class ChargeableValueOfResidenceCloselyInheritedController @Inject()(override val appConfig: FrontendAppConfig,
                                                                     val messagesApi: MessagesApi,
                                                                     override val sessionConnector: SessionConnector,
                                                                     override val navigator: Navigator) extends SimpleControllerBase[Int] {

  override val controllerId: String = Constants.chargeableValueOfResidenceCloselyInheritedId

  override def form: () => Form[Int] = () => NonNegativeIntForm()

  override def view(form: Option[Form[Int]], backUrl: String, answerRows: Seq[AnswerRow])
                   (implicit request: Request[_]): _root_.play.twirl.api.HtmlFormat.Appendable =
    chargeable_value_of_residence_closely_inherited(appConfig, backUrl, form, answerRows)

  override def validate(value: Int)(implicit hc: HeaderCarrier): Future[Option[FormError]] = {
    sessionConnector.fetchAndGetEntry[Int](Constants.chargeableValueOfResidenceId).map {
      case None => Some(FormError("value", "chargeable_value_of_residence_closely_inherited.greater_than_chargeable_value_of_residence.error"))
      case Some(g) if value > g => Some(FormError("value", "chargeable_value_of_residence_closely_inherited.greater_than_chargeable_value_of_residence.error"))
      case _ => None
    }
  }
}
