/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.TransferAnyUnusedThresholdController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.transfer_any_unused_threshold

import scala.language.reflectiveCalls

class TransferAnyUnusedThresholdViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "transfer_any_unused_threshold"

  def createView(form: Form[Boolean]) = transfer_any_unused_threshold(frontendAppConfig, form, Seq())(request, messages, applicationProvider, localPartialRetriever)

  "Transfer Any Unused Allowance View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix, "guidance1", "guidance2")(fakeApplication.injector.instanceOf[TransferAnyUnusedThresholdController].form())

    behave like pageWithoutBackLink[Boolean](createView, fakeApplication.injector.instanceOf[TransferAnyUnusedThresholdController].form())

    behave like booleanPage(createView, messageKeyPrefix, TransferAnyUnusedThresholdController.onSubmit().url, fakeApplication.injector.instanceOf[TransferAnyUnusedThresholdController].form(), true)

    behave like pageContainingPreviousAnswers(createView, fakeApplication.injector.instanceOf[TransferAnyUnusedThresholdController].form())

  }
}
