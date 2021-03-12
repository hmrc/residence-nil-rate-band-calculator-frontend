/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.data.Form
import play.api.mvc.{DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.PositivePercentForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_passed_to_direct_descendants
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

class PercentagePassedToDirectDescendantsController  @Inject()(cc: DefaultMessagesControllerComponents,
                                                               override val sessionConnector: SessionConnector,
                                                               override val navigator: Navigator,
                                                               implicit val appConfig: FrontendAppConfig) extends FrontendController(cc) with SimpleControllerBase[BigDecimal] {


  override val controllerId = Constants.percentagePassedToDirectDescendantsId

  override def form = () => PositivePercentForm("percentage_passed_to_direct_descendants.error.required",
    "percentage_passed_to_direct_descendants.error.non_numeric", "percentage_passed_to_direct_descendants.error.out_of_range")

  override def view(form: Form[BigDecimal], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    percentage_passed_to_direct_descendants(form, answerRows)
  }
}
