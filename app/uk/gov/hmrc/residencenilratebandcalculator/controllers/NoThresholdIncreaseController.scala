/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.mvc.{DefaultMessagesControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoThresholdIncreaseReason.{DateOfDeath, DirectDescendant}
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_threshold_increase
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

@Singleton
class NoThresholdIncreaseController  @Inject()(cc: DefaultMessagesControllerComponents,
                                               override val sessionConnector: SessionConnector,
                                               implicit val appConfig: FrontendAppConfig)
  extends FrontendController(cc) with TransitionController {

  val getReason = GetNoThresholdIncreaseReason

  def getControllerId(reason: Reason) =
    reason match {
      case DateOfDeath => Constants.dateOfDeathId
      case DirectDescendant => Constants.partOfEstatePassingToDirectDescendantsId
    }

  def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: scala.Seq[AnswerRow])(implicit request: Request[_]) = {
    val prefix = reason match {
      case DateOfDeath => "no_threshold_increase.date_of_death"
      case DirectDescendant => "no_threshold_increase.direct_descendant"
    }

    no_threshold_increase(prefix, previousAnswers)
  }
}
