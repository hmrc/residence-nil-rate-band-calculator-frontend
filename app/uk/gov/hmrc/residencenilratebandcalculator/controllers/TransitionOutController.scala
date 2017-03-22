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
import uk.gov.hmrc.residencenilratebandcalculator.models.GetTransitionOutReason.{DateOfDeath, DirectDescendant, GrossingUpForOtherProperty, GrossingUpForResidence}
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.not_possible_to_use_service
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

@Singleton
class TransitionOutController @Inject()(val appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        override val sessionConnector: SessionConnector) extends TransitionController {

  val getReason = GetTransitionOutReason

  def getControllerId(reason: Reason) =
    reason match {
      case DateOfDeath => Constants.dateOfDeathId
      case DirectDescendant => Constants.partOfEstatePassingToDirectDescendantsId
      case GrossingUpForResidence => Constants.doesGrossingUpApplyToResidenceId
      case GrossingUpForOtherProperty => Constants.doesGrossingUpApplyToOtherPropertyId
    }

  def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: scala.Seq[AnswerRow])(implicit request: Request[_]) = {
    val prefix = reason match {
      case DateOfDeath => "not_possible_to_use_service.date_of_death"
      case DirectDescendant => "not_possible_to_use_service.direct_descendant"
      case GrossingUpForResidence => "not_possible_to_use_service.grossing_up"
      case GrossingUpForOtherProperty => "not_possible_to_use_service.grossing_up"
    }
    not_possible_to_use_service(appConfig, prefix, previousAnswers)
  }
}
