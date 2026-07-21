/*
 * Copyright 2023 HM Revenue & Customs
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
import play.twirl.api.Html
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.*
import uk.gov.hmrc.residencenilratebandcalculator.forms.Forms
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewBooleanViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.transfer_any_unused_threshold

class TransferAnyUnusedThresholdViewSpec extends NewBooleanViewSpec {

  val messageKeyPrefix                                             = "transfer_any_unused_threshold"
  val transfer_any_unused_threshold: transfer_any_unused_threshold = inject[transfer_any_unused_threshold]
  def createView(form: Form[Boolean]): Html = transfer_any_unused_threshold(form)(request, messages)

  val form: Form[Boolean] = Forms.TransferAnyUnusedThreshold

  "Transfer Any Unused Allowance View" must {

    behave.like(
      rnrbPage[Boolean](createView, messageKeyPrefix, "guidance1", "guidance2")(
        form
      )
    )

    behave.like(
      pageWithoutBackLink[Boolean](
        createView,
        form
      )
    )

    behave.like(
      booleanPage(
        createView,
        messageKeyPrefix,
        TransferAnyUnusedThresholdController.onSubmit.url,
        form,
        true
      )
    )
  }

}
