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
import play.api.mvc.Request
import play.twirl.api.HtmlFormat._
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.PurposeOfUseForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.purpose_of_use
import play.api.libs.json.Reads
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import play.api.mvc._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.logging.SessionId

@Singleton
class PurposeOfUseController @Inject()(override val appConfig: FrontendAppConfig,
                                       val messagesApi: MessagesApi,
                                       override val sessionConnector: SessionConnector,
                                       override val navigator: Navigator) extends SimpleControllerBase[String] {

  override val controllerId: String = Constants.purposeOfUseId

  override def form: () => Form[String] = () => PurposeOfUseForm()

  override def view(form: Option[Form[String]], backUrl: String)(implicit request: Request[_]): Appendable = {
    purpose_of_use(appConfig, backUrl, form)
  }

  override def onPageLoad(implicit rds: Reads[String]) = Action.async { implicit request =>
    sessionConnector.fetch().map(
      optionalCacheMap => {
        val cacheMap = optionalCacheMap.getOrElse(CacheMap(hc.sessionId.getOrElse(SessionId("")).value, Map()))
        Ok(view(cacheMap.getEntry(controllerId).map(value => form().fill(value)), navigator.lastPage(controllerId)(new UserAnswers(cacheMap)).url))
      })
  }
}
