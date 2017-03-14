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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.cannot_claim_downsizing

@Singleton
class CannotClaimDownsizingController @Inject()(val appConfig: FrontendAppConfig,
                                                val messagesApi: MessagesApi,
                                                val sessionConnector: SessionConnector,
                                                val navigator: Navigator) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async {
    implicit request =>
      sessionConnector.fetch().map {
        case Some(cacheMap) => {
          val previousAnswers = answerRows(cacheMap, request)
          val userAnswers = new UserAnswers(cacheMap)
          Ok(cannot_claim_downsizing(appConfig, GetCannotClaimDownsizingReasonKey(userAnswers),
            navigator.nextPage(Constants.cannotClaimDownsizingId)(userAnswers), previousAnswers))
        }
        case None => throw new RuntimeException("No cacheMap available")
      }
  }

  def answerRows(cacheMap: CacheMap, request: Request[_]): Seq[AnswerRow] = {
    val reasonKey: String = GetCannotClaimDownsizingReasonKey(userAnswers = new UserAnswers(cacheMap))
    val controllerId = reasonKey match {
      case ("cannot_claim_downsizing.no_assets_passing_to_direct_descendants_reason") => Constants.anyAssetsPassingToDirectDescendantsId
      case _ => Constants.dateOfDisposalId
    }
    AnswerRows.constructAnswerRows(
      AnswerRows.truncateAndAddCurrentLocateInCacheMap(controllerId, cacheMap),
      AnswerRows.answerRowFns,
      AnswerRows.rowOrder,
      messagesApi.preferred(request)
    )
  }
}
