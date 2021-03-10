/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoAdditionalThresholdAvailableReason.{NoProperty, NotCloselyInherited}
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_additional_threshold_available
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class NoAdditionalThresholdAvailableController @Inject()(cc: DefaultMessagesControllerComponents,
                                                         override val sessionConnector: SessionConnector,
                                                         val navigator: Navigator,
                                                         implicit val appConfig: FrontendAppConfig)
  extends FrontendController(cc) with TransitionController {

  val getReason = GetNoAdditionalThresholdAvailableReason

  def getControllerId(reason: Reason) =
    reason match {
      case NotCloselyInherited => Constants.propertyPassingToDirectDescendantsId
      case _ => Constants.propertyInEstateId
    }

  def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: Seq[AnswerRow])(implicit request: Request[_]) = {
    val reasonKey = reason match {
      case NotCloselyInherited => "no_additional_threshold_available.not_closely_inherited_reason"
      case NoProperty => "no_additional_threshold_available.no_property_reason"
    }
    no_additional_threshold_available(reasonKey, navigator.nextPage(Constants.noAdditionalThresholdAvailableId)(userAnswers), previousAnswers)
  }
}
