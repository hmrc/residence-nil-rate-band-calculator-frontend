/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Request}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models._

import scala.concurrent.ExecutionContext

trait TransitionController extends FrontendController with I18nSupport {
  val sessionConnector: SessionConnector

  val getReason: GetReason
  def getControllerId(reason: Reason): String

  implicit val ec: ExecutionContext

  def answerRows(cacheMap: CacheMap, request: Request[_]): Seq[AnswerRow] = {
    val controllerId = getControllerId(getReason(new UserAnswers(cacheMap)))
    AnswerRows.constructAnswerRows(
      AnswerRows.truncateAndAddCurrentLocateInCacheMap(controllerId, cacheMap),
      AnswerRows.answerRowFns,
      AnswerRows.rowOrder,
      messagesApi.preferred(request)
    )
  }

  def createView(reason: Reason, userAnswers: UserAnswers)(implicit request: Request[_]): HtmlFormat.Appendable

  def onPageLoad: Action[AnyContent] = Action.async {
    implicit request =>
      sessionConnector.fetch().map {
        case Some(cacheMap) => {
          val userAnswers = new UserAnswers(cacheMap)
          Ok(createView(getReason(userAnswers), userAnswers))
        }
        case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      }
  }
}
