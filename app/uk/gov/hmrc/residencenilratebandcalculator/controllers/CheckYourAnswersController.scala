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

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRows
import uk.gov.hmrc.residencenilratebandcalculator.views.html.check_your_answers

import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject()(cc: DefaultMessagesControllerComponents,
                                           sessionConnector: SessionConnector,
                                           validatedSession: ValidatedSession,
                                           checkYourAnswersView: check_your_answers)
                                          (implicit ec: ExecutionContext)
  extends FrontendController(cc) with I18nSupport with Logging {

  def onPageLoad = {
    validatedSession.async {
      implicit request => {
        sessionConnector.fetch().map {
          case Some(answers) =>
            val requiredAnswers = Set(
              Constants.noDownsizingThresholdIncrease,
              Constants.valueAvailableWhenPropertyChangedId,
              Constants.transferAvailableWhenPropertyChangedId,
              Constants.claimDownsizingThresholdId,
              Constants.valueOfAssetsPassingId
            )
            if (requiredAnswers.intersect(answers.data.keys.toSet).nonEmpty) {
              val messages = cc.messagesApi.preferred(request)
              Ok(checkYourAnswersView(AnswerRows(answers, messages)))
            } else {
              Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
            }
          case None => Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad())
        }
      }
    }
  }
}
