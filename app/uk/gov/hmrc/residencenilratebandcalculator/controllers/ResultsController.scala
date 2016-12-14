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
import play.api.mvc._
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, JsonBuilder}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.models.CalculationResult
import uk.gov.hmrc.residencenilratebandcalculator.views.html.results

import scala.concurrent.Future

@Singleton
class ResultsController @Inject()(appConfig: FrontendAppConfig, val messagesApi: MessagesApi,
                                  rnrbConnector: RnrbConnector, sessionConnector: SessionConnector, jsonBuilder: JsonBuilder)
  extends FrontendController with I18nSupport  {

//  def onPageLoad = Action.async { implicit request =>
//    val jsonEither = jsonBuilder.build(sessionConnector)
//    jsonEither.map {
//      case Left(error) => {
//        // TODO: Logging
//        InternalServerError(error)
//      }
//      case Right(json) => {
//        val xx: Future[Option[CalculationResult]] = rnrbConnector.send(json).map{
//          (eitherResult: Either[String, CalculationResult]) => eitherResult match {
//            case Left(error) => None
//            case Right(calculationResult) => Some(calculationResult)
//          }
//        }
//        xx.map{ option => Ok(results(appConfig, option))}
//      }
//    }
//  }
  def onPageLoad = Action.async {implicit request => Future.successful(Ok(""))}

}