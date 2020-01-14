/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ClaimDownsizingThresholdController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.claim_downsizing_threshold

import scala.language.reflectiveCalls

class ClaimDownsizingThresholdViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "claim_downsizing_threshold"

  def createView(form: Form[Boolean]) = claim_downsizing_threshold(form, Seq())(request, messages, mockConfig)

  "Claim Downsizing Threshold View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix,
      "guidance1",
      "guidance1.bullet1",
      "guidance1.bullet2",
      "guidance1.bullet3",
      "guidance1.bullet4")(fakeApplication.injector.instanceOf[ClaimDownsizingThresholdController].form())

    behave like pageWithoutBackLink[Boolean](createView, fakeApplication.injector.instanceOf[ClaimDownsizingThresholdController].form())

    behave like booleanPage(createView, messageKeyPrefix, ClaimDownsizingThresholdController.onSubmit().url, fakeApplication.injector.instanceOf[ClaimDownsizingThresholdController].form(), true)

    behave like pageContainingPreviousAnswers(createView, fakeApplication.injector.instanceOf[ClaimDownsizingThresholdController].form())

  }
}
