/*
 * Copyright 2016 HM Revenue & Customs
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

import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value

class GrossEstateValueController @Inject()(override val appConfig: FrontendAppConfig,
                                           val messagesApi: MessagesApi,
                                           override val sessionConnector: SessionConnector,
                                           override val navigator: Navigator) extends IntControllerBase {


  override val controllerId = Constants.grossEstateValueControllerId

  override def view(form: Option[Form[Int]])(implicit request: Request[_]) = gross_estate_value(appConfig, form)
}
