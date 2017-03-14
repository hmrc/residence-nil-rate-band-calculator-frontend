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
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, AnswerRows, GetTransitionOutPrefix, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.not_possible_to_use_service

@Singleton
class TransitionOutController @Inject()(appConfig: FrontendAppConfig,
                                        val messagesApi: MessagesApi,
                                        val sessionConnector: SessionConnector)
  extends FrontendController with I18nSupport {

  private def answerRows(cacheMap: CacheMap, request: Request[_]): Seq[AnswerRow] = {
    val userAnswers = new UserAnswers(cacheMap)
    val controllerId = (userAnswers.dateOfDeath, userAnswers.anyEstatePassedToDescendants, userAnswers.doesGrossingUpApplyToResidence) match {
      case (Some(dateOfDeath), _, _) if dateOfDeath isBefore Constants.eligibilityDate => Constants.dateOfDeathId
      case (_, Some(false), _) => Constants.anyEstatePassedToDescendantsId
      case (_, _, Some(true)) => Constants.doesGrossingUpApplyToResidenceId
      case _ => Constants.doesGrossingUpApplyToOtherPropertyId
    }
    AnswerRows.constructAnswerRows(
      AnswerRows.truncateAndAddCurrentLocateInCacheMap(controllerId, cacheMap),
      AnswerRows.answerRowFns,
      AnswerRows.rowOrder,
      messagesApi.preferred(request)
    )
  }

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    sessionConnector.fetch().map {
      case Some(cacheMap) => {
        val transitionOutPrefix = GetTransitionOutPrefix(new UserAnswers(cacheMap))
        Ok(not_possible_to_use_service(appConfig, transitionOutPrefix, answerRows(cacheMap, request)))
      }
      case None => throw new RuntimeException("No cacheMap available")
    }
  }
}
