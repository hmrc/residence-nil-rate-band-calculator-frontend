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

package uk.gov.hmrc.residencenilratebandcalculator.handlers

import javax.inject.{Inject, Provider, Singleton}

import play.api.http.DefaultHttpErrorHandler
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Request, RequestHeader, Result, Results}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper, UsefulException}
import play.twirl.api.Html
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, FrontendAuditConnector}
import uk.gov.hmrc.play.audit.http.config.ErrorAuditingSettings
import uk.gov.hmrc.play.frontend.bootstrap.ShowErrorPage

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(env: Environment,
                             config: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router],
                             appConfig: FrontendAppConfig,
                             val messagesApi: MessagesApi,
                             frontendAuditConnector: FrontendAuditConnector)
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with I18nSupport {

  val impl = new ErrorAuditingSettings with ShowErrorPage {
    override val auditConnector = frontendAuditConnector
    lazy val appName = config.getString("appName").getOrElse("APP NAME NOT SET")

    override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: Request[_]): Html =
      uk.gov.hmrc.residencenilratebandcalculator.views.html.error_template(pageTitle, heading, message, appConfig)
  }

  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = impl.onError(request, exception)

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    statusCode match {
      case BAD_REQUEST => impl.onBadRequest(request, message)
      case NOT_FOUND => impl.onHandlerNotFound(request)
      case _ => Future.successful(Results.Status(statusCode)("A client error occurred: " + message))
    }
}
