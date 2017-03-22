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
import uk.gov.hmrc.residencenilratebandcalculator.models.GetCannotClaimRNRBReason.{NoProperty, NotCloselyInherited}
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.cannot_claim_RNRB
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class CannotClaimRNRBController @Inject()(val appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          override val sessionConnector: SessionConnector,
                                          val navigator: Navigator) extends TransitionController {

  val getReason = GetCannotClaimRNRBReason

  def getControllerId(reason: Reason) =
    reason match {
      case NotCloselyInherited => Constants.anyPropertyCloselyInheritedId
      case _ => Constants.propertyInEstateId
    }

  def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: Seq[AnswerRow])(implicit request: Request[_]) = {
    val reasonKey = reason match {
      case NotCloselyInherited => "cannot_claim_RNRB.not_closely_inherited_reason"
      case NoProperty => "cannot_claim_RNRB.no_property_reason"
    }
    cannot_claim_RNRB(appConfig, reasonKey, navigator.nextPage(Constants.cannotClaimRNRB)(userAnswers), previousAnswers)
  }
}
