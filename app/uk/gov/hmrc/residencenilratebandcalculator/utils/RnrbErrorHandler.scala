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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import javax.inject.Inject
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import uk.gov.hmrc.residencenilratebandcalculator.views.html.error_template

class RnrbErrorHandler @Inject()(val messagesApi: MessagesApi,
                                 errorTemplateView: error_template) extends FrontendErrorHandler {


  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]) =
    errorTemplateView(pageTitle, heading, message)

  override def internalServerErrorTemplate(implicit request: Request[_]): Html = {
    errorTemplateView(
      Messages("error.InternalServerError500.title"),
      Messages("error.InternalServerError500.heading"),
      Messages("error.InternalServerError500.message"))
  }
}
