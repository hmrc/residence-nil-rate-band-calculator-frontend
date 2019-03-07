/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.BooleanForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_property
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class GrossingUpOnEstatePropertyController @Inject()(cc: DefaultMessagesControllerComponents,
                                                     override val sessionConnector: SessionConnector,
                                                     override val navigator: Navigator,
                                                     implicit val appConfig: FrontendAppConfig) extends FrontendController(cc) with SimpleControllerBase[Boolean] {

  override val controllerId: String = Constants.grossingUpOnEstatePropertyId

  override def form: () => Form[Boolean] = () => BooleanForm("grossing_up_on_estate_property.error.required")

  override def view(form: Form[Boolean], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    grossing_up_on_estate_property(form, answerRows)
  }
}
