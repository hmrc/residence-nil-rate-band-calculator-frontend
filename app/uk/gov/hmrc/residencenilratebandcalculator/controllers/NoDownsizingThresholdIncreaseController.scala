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

import play.api.Application
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoDownsizingThresholdIncreaseReason.{DatePropertyWasChangedTooEarly, NoAssetsPassingToDirectDescendants}
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_downsizing_threshold_increase
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class NoDownsizingThresholdIncreaseController @Inject()(val appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                override val sessionConnector: SessionConnector,
                                                val navigator: Navigator,
                                                implicit val application: Application) extends TransitionController {

  val getReason = GetNoDownsizingThresholdIncreaseReason

  def getControllerId(reason: Reason) =
    reason match {
      case NoAssetsPassingToDirectDescendants => Constants.assetsPassingToDirectDescendantsId
      case _ => Constants.datePropertyWasChangedId
    }

  def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: Seq[AnswerRow])(implicit request: Request[_]) = {
    val reasonKey = reason match {
      case NoAssetsPassingToDirectDescendants => "no_downsizing_threshold_increase.no_assets_passing_to_direct_descendants_reason"
      case DatePropertyWasChangedTooEarly => "no_downsizing_threshold_increase.date_property_was_changed_too_early_reason"
    }
    no_downsizing_threshold_increase(appConfig, reasonKey, navigator.nextPage(Constants.noDownsizingThresholdIncrease)(userAnswers), previousAnswers)
  }
}
