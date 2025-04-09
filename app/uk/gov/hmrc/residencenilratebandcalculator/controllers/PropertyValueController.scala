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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import javax.inject.{Inject, Singleton}
import play.api.data.{Form, FormError}
import play.api.mvc.{DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}

import scala.concurrent.ExecutionContext

@Singleton
class PropertyValueController @Inject() (
    cc: DefaultMessagesControllerComponents,
    override val sessionConnector: SessionConnector,
    override val navigator: Navigator,
    propertyValueView: property_value
)(override implicit val ec: ExecutionContext)
    extends FrontendController(cc)
    with SimpleControllerBase[Int] {

  override val controllerId = Constants.propertyValueId

  override def form = () =>
    NonNegativeIntForm(
      "property_value.error.blank",
      "error.whole_pounds",
      "property_value.error.non_numeric",
      "error.value_too_large"
    )

  override def view(form: Form[Int], userAnswers: UserAnswers)(implicit request: Request[_]) =
    propertyValueView(form)

  override def validate(value: Int, userAnswers: UserAnswers): Option[FormError] =
    userAnswers.valueOfEstate match {
      case Some(v) if value > v => Some(FormError("value", "property_value.greater_than_value_of_estate.error", Seq(v)))
      case _                    => None
    }

}
