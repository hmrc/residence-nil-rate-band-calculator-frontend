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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.http.SessionId
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.{Date, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DateOfDeathController @Inject()(cc: DefaultMessagesControllerComponents,
                                      val sessionConnector: SessionConnector,
                                      val navigator: Navigator,
                                      validatedSession: ValidatedSession,
                                      dateOfDeathView: date_of_death)(implicit ex: ExecutionContext) extends FrontendController(cc) with ControllerBase[Date] {

  lazy val controllerId = Constants.dateOfDeathId

  def form: Form[Date] = dateOfDeathForm

  def view(form: Form[Date])(implicit request: Request[_]) = dateOfDeathView(form)

  def onPageLoad(implicit rds: Reads[Date]) = Action.async { implicit request =>
    sessionConnector.fetch().map(
      optionalCacheMap => {
        val cacheMap: CacheMap = optionalCacheMap.getOrElse(CacheMap(hc.sessionId.getOrElse(SessionId("")).value, Map()))
        val dateOfDeath = cacheMap.getEntry[Date](controllerId)

        Ok(view(dateOfDeath.fold(form)(value => form.fill(value))))
      })
  }

  def onSubmit(implicit wts: Writes[Date]) = validatedSession.async { implicit request =>
    val boundForm = form.bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[Date]) => {
        Future.successful(BadRequest(view(formWithErrors)))
      },
      (value) =>
        sessionConnector.cache[Date](controllerId, value).map(cacheMap =>
          Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
    )
  }
}
