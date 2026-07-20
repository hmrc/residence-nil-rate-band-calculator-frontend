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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.controllers.TransferAvailableWhenPropertyChangedController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.*
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewBooleanViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.transfer_available_when_property_changed

class TransferAvailableWhenPropertyChangedViewSpec extends NewBooleanViewSpec {

  val messageKeyPrefix = "transfer_available_when_property_changed"

  val transfer_available_when_property_changed: transfer_available_when_property_changed =
    inject[transfer_available_when_property_changed]

  def createView(form: Form[Boolean]): HtmlFormat.Appendable =
    transfer_available_when_property_changed(form)(request, messages)

  "Transfer Available When Property Changed View" must {

    behave.like(
      rnrbPage[Boolean](createView, messageKeyPrefix, "guidance")(
        inject[TransferAvailableWhenPropertyChangedController].form()
      )
    )

    behave.like(
      pageWithoutBackLink[Boolean](
        createView,
        inject[TransferAvailableWhenPropertyChangedController].form()
      )
    )

    behave.like(
      booleanPage(
        createView,
        messageKeyPrefix,
        TransferAvailableWhenPropertyChangedController.onSubmit.url,
        inject[TransferAvailableWhenPropertyChangedController].form(),
        useNewValues = true
      )
    )
  }

}
