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
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Request, _}
import play.twirl.api.HtmlFormat._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.PurposeOfUseForm
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.views.html.purpose_of_use
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class PurposeOfUseController @Inject()(val appConfig: FrontendAppConfig,
                                       val messagesApi: MessagesApi,
                                       val sessionConnector: SessionConnector,
                                       val navigator: Navigator) extends LoadableSubmittable[String] {

  val controllerId: String = Constants.purposeOfUseId

  def form: () => Form[String] = () => PurposeOfUseForm()

  def view(form: Option[Form[String]], backUrl: String)(implicit request: Request[_]): Appendable = {
    purpose_of_use(appConfig, backUrl, form)
  }

  def onPageLoad(implicit rds: Reads[String]) = Action.async { implicit request =>
    sessionConnector.fetch().map(
      optionalCacheMap => {
        val cacheMap = optionalCacheMap.getOrElse(CacheMap(hc.sessionId.getOrElse(SessionId("")).value, Map()))
        Ok(view(cacheMap.getEntry(controllerId).map(value => form().fill(value)), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url))
      })
  }

  def onSubmit(implicit wts: Writes[String]) = Action.async { implicit request =>
    val boundForm = form().bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[String]) => {
        sessionConnector.fetch().map {
          optionalCacheMap => {
            val cacheMap = optionalCacheMap.getOrElse(CacheMap(hc.sessionId.getOrElse(SessionId("")).value, Map()))
            BadRequest(view(Some(formWithErrors), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url))
          }
        }
      },
      (value) =>
          sessionConnector.cache[String](controllerId, value).map(cacheMap =>
            Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
    )
  }
}
