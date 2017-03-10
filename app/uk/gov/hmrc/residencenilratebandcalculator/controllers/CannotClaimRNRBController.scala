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
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, AnswerRows, GetCannotClaimRNRBReasonKey, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.cannot_claim_RNRB
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class CannotClaimRNRBController @Inject()(val appConfig: FrontendAppConfig,
                                          val messagesApi: MessagesApi,
                                          val sessionConnector: SessionConnector,
                                          val navigator: Navigator) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async {
    implicit request =>
      sessionConnector.fetch().map {
        case Some(cacheMap) => {
          val previousAnswers = answerRows(cacheMap, request)
          val userAnswers = new UserAnswers(cacheMap)
          Ok(cannot_claim_RNRB(appConfig, GetCannotClaimRNRBReasonKey(userAnswers), navigator.nextPage(Constants.cannotClaimRNRB)(userAnswers), previousAnswers))
        }
        case None => throw new RuntimeException("No cacheMap available")
      }
  }

  def answerRows(cacheMap: CacheMap, request: Request[_]): Seq[AnswerRow] = {
    val reasonKey: String = GetCannotClaimRNRBReasonKey(userAnswers = new UserAnswers(cacheMap))
    val controllerId = reasonKey match {
      case ("cannot_claim_RNRB.not_closely_inherited_reason") => Constants.anyPropertyCloselyInheritedId
      case _ => Constants.estateHasPropertyId
    }
    AnswerRows.constructAnswerRows(
      AnswerRows.truncateAndAddCurrentLocateInCacheMap(controllerId, cacheMap),
      AnswerRows.answerRowFns,
      AnswerRows.rowOrder,
      messagesApi.preferred(request)
    )
  }


}
