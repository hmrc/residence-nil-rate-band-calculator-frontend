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

import javax.inject.{Inject, Singleton}

import com.google.inject.Provider
import play.api.Application
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.logging.SessionId
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.{Date, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.utils.LocalPartialRetriever
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

import scala.concurrent.Future

@Singleton
class DateOfDeathController @Inject()(val messagesApi: MessagesApi,
                                      val sessionConnector: SessionConnector,
                                      val navigator: Navigator,
                                      implicit val applicationProvider: Provider[Application],
                                      implicit val localPartialRetriever: LocalPartialRetriever) extends ControllerBase[Date] {

  val controllerId = Constants.dateOfDeathId

  def form: Form[Date] = dateOfDeathForm

  def view(form: Form[Date])(implicit request: Request[_]) = date_of_death(form)

  def onPageLoad(implicit rds: Reads[Date]) = Action.async { implicit request =>
    sessionConnector.fetch().map(
      optionalCacheMap => {
        val cacheMap: CacheMap = optionalCacheMap.getOrElse(CacheMap(hc.sessionId.getOrElse(SessionId("")).value, Map()))
        val dateOfDeath = cacheMap.getEntry[Date](controllerId)

        Ok(view(dateOfDeath.fold(form)(value => form.fill(value))))
      })
  }

  def onSubmit(implicit wts: Writes[Date]) = Action.async { implicit request =>
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
