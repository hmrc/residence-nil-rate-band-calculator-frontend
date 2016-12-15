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

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsValue
import play.api.mvc._
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.results
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, JsonBuilder}

import scala.concurrent.Future

@Singleton
class ResultsController @Inject()(appConfig: FrontendAppConfig, val messagesApi: MessagesApi,
                                  rnrbConnector: RnrbConnector, sessionConnector: SessionConnector, jsonBuilder: JsonBuilder)
  extends FrontendController with I18nSupport {

  def onPageLoad = Action.async { implicit request => {
    val jsonEither: Future[Either[String, JsValue]] = jsonBuilder.build(sessionConnector)

    jsonEither.flatMap {
      case Left(error) => {
        //TODO: Logging
        Future.successful(InternalServerError(error))
      }
      case Right(json) => {
        rnrbConnector.send(json).map {
          rnrbEither => Ok(results(appConfig, rnrbEither))
        }
      }
    }
  }}
}
