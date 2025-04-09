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
import play.api.data.Form
import play.api.mvc.{DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_assets
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}

import scala.concurrent.ExecutionContext

@Singleton
class GrossingUpOnEstateAssetsController @Inject() (
    cc: DefaultMessagesControllerComponents,
    override val sessionConnector: SessionConnector,
    override val navigator: Navigator,
    grossingUpOnEstateAssetsView: grossing_up_on_estate_assets
)(override implicit val ec: ExecutionContext)
    extends FrontendController(cc)
    with SimpleControllerBase[Boolean] {

  override val controllerId: String = Constants.grossingUpOnEstateAssetsId

  override def form: () => Form[Boolean] = () => BooleanForm("grossing_up_on_estate_assets.error.required")

  override def view(form: Form[Boolean], userAnswers: UserAnswers)(implicit request: Request[_]) =
    grossingUpOnEstateAssetsView(form)

}
