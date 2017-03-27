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

import play.api.i18n.MessagesApi
import play.api.mvc.Request
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.GetUnableToCalculateThresholdIncreaseReason.{GrossingUpForOtherProperty, GrossingUpForResidence}
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.unable_to_calculate_threshold_increase
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

@Singleton
class UnableToCalculateThresholdIncreaseController @Inject()(val appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        override val sessionConnector: SessionConnector) extends TransitionController {

  val getReason = GetUnableToCalculateThresholdIncreaseReason

  def getControllerId(reason: Reason) =
    reason match {
      case GrossingUpForResidence => Constants.grossingUpOnEstatePropertyId
      case GrossingUpForOtherProperty => Constants.grossingUpOnEstateAssetsId
    }

  def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: scala.Seq[AnswerRow])(implicit request: Request[_]) =
    unable_to_calculate_threshold_increase(appConfig, "unable_to_calculate_threshold_increase.grossing_up", previousAnswers)
}
