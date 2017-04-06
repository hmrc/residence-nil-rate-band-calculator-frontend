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

import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.PositivePercentForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_passed_to_direct_descendants

class PercentagePassedToDirectDescendantsController  @Inject()(override val appConfig: FrontendAppConfig,
                                                               val messagesApi: MessagesApi,
                                                               override val sessionConnector: SessionConnector,
                                                               override val navigator: Navigator) extends SimpleControllerBase[BigDecimal] {


  override val controllerId = Constants.percentagePassedToDirectDescendantsId

  override def form = () => PositivePercentForm("percentage_passed_to_direct_descendants.error.required",
    "percentage_passed_to_direct_descendants.error.non_numeric", "percentage_passed_to_direct_descendants.error.out_of_range")

  override def view(form: Option[Form[BigDecimal]], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    percentage_passed_to_direct_descendants(appConfig, form, answerRows)
  }
}
