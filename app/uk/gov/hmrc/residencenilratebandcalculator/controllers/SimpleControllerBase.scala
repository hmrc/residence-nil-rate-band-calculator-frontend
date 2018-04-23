/*
 * Copyright 2018 HM Revenue & Customs
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


import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{Reads, Writes}
import play.api.mvc._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, AnswerRows, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

trait ControllerBase[A] extends FrontendController with I18nSupport {
  def onPageLoad(implicit rds: Reads[A]): Action[AnyContent]

  def onSubmit(implicit wts: Writes[A]): Action[AnyContent]
}

trait SimpleControllerBase[A] extends ControllerBase[A] {

  val appConfig: FrontendAppConfig

  val controllerId: String

  def sessionConnector: SessionConnector

  def form: () => Form[A]

  def view(form: Form[A], answerRows: Seq[AnswerRow], userAnswers: UserAnswers)(implicit request: Request[_]): HtmlFormat.Appendable

  val navigator: Navigator

  def answerRows(cacheMap: CacheMap, request: Request[_]): Seq[AnswerRow] = AnswerRows.constructAnswerRows(
    AnswerRows.truncateAndLocateInCacheMap(controllerId, cacheMap),
    AnswerRows.answerRowFns,
    AnswerRows.rowOrder,
    messagesApi.preferred(request)
  )

  override def onPageLoad(implicit rds: Reads[A]): Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
      case Some(cacheMap) => {
        val previousAnswers = answerRows(cacheMap, request)
        val userAnswers = new UserAnswers(cacheMap)
        Ok(view(cacheMap.getEntry(controllerId).fold(form())(value => form().fill(value)),
          previousAnswers, userAnswers))
      }
    }
  }

  def onSubmit(implicit wts: Writes[A]) = Action.async { implicit request =>

    sessionConnector.fetch().flatMap {
      case None => Future.successful(Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad()))
      case Some(cacheMap) => {
        val previousAnswers = answerRows(cacheMap, request)
        val userAnswers = new UserAnswers(cacheMap)
        val boundForm = form().bindFromRequest()
        boundForm.fold(
          (formWithErrors: Form[A]) =>
            Future.successful(BadRequest(view(formWithErrors,
              previousAnswers, userAnswers))),
          (value) => validate(value, userAnswers) match {
            case Some(error) => {
              Future.successful(BadRequest(view(form().fill(value).withError(error),
                previousAnswers, userAnswers)))
            }
            case None =>
              sessionConnector.cache[A](controllerId, value).map(cacheMap =>
                Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
          }
        )
      }
    }
  }

  def validate(value: A, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Option[FormError] = None
}
