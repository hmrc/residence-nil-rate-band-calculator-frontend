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

import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.twirl.api.HtmlFormat._
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.AnyPropertyCloselyInheritedForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_property_closely_inherited

@Singleton
class AnyPropertyCloselyInheritedController @Inject()(override val appConfig: FrontendAppConfig,
                                                      val messagesApi: MessagesApi,
                                                      override val sessionConnector: SessionConnector,
                                                      override val navigator: Navigator) extends SimpleControllerBase[String] {

  override val controllerId: String = Constants.anyPropertyCloselyInheritedId

  override def form: () => Form[String] = () => AnyPropertyCloselyInheritedForm()

  override def view(form: Option[Form[String]], backUrl: String)(implicit request: Request[_]): Appendable = {
    any_property_closely_inherited(appConfig, backUrl, form)
  }
}
