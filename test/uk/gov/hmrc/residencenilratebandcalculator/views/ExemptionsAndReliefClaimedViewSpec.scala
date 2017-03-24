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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.views.html.exemptions_and_relief_claimed
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

import scala.language.reflectiveCalls

class ExemptionsAndReliefClaimedViewSpec  extends BooleanViewSpecBase {

  val messageKeyPrefix = "exemptions_and_relief_claimed"

  def createView(form: Option[Form[Boolean]] = None) = exemptions_and_relief_claimed(frontendAppConfig, backUrl, form, Seq())(request, messages)

  "Exemptions And Relief Claimed View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix, "guidance", "guidance.bullet1", "guidance.bullet2", "guidance.bullet3")

    behave like pageWithBackLink[Boolean](createView)

    behave like booleanPage(createView, messageKeyPrefix, ExemptionsAndReliefClaimedController.onSubmit().url)

    behave like pageContainingPreviousAnswers(createView)

  }
}