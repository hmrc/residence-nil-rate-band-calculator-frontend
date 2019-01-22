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

import com.google.inject.Provider
import play.api.Application
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.twirl.api.HtmlFormat._
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyPassingToDirectDescendantsForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_passing_to_direct_descendants

@Singleton
class PropertyPassingToDirectDescendantsController @Inject()(val messagesApi: MessagesApi,
                                                             override val sessionConnector: SessionConnector,
                                                             override val navigator: Navigator,
                                                             implicit val appConfig: FrontendAppConfig,
                                                             implicit val applicationProvider: Provider[Application]) extends SimpleControllerBase[String] {

  override val controllerId: String = Constants.propertyPassingToDirectDescendantsId

  override def form: () => Form[String] = () => PropertyPassingToDirectDescendantsForm()

  override def view(form: Form[String], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]) = {
    property_passing_to_direct_descendants(form, answerRows)
  }
}
