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
import play.api.mvc.DefaultMessagesControllerComponents
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.Navigator
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ValueBeingTransferredController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_being_transferred

class ValueBeingTransferredViewSpec extends NewIntViewSpecBase {

  val messageKeyPrefix                                 = "value_being_transferred"
  val navigator: Navigator                             = injector.instanceOf[Navigator]
  var mockSessionConnector: SessionConnector           = _
  val mockRnrbConnector: RnrbConnector                 = mock[RnrbConnector]
  val mockValidatedSession: ValidatedSession           = mock[ValidatedSession]
  val value_being_transferred: value_being_transferred = injector.instanceOf[value_being_transferred]

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  val controller: Form[Int] = new ValueBeingTransferredController(
    messagesControllerComponents,
    mockSessionConnector,
    navigator,
    mockRnrbConnector,
    mockValidatedSession,
    value_being_transferred
  ).form()

  def createView(form: Form[Int]): HtmlFormat.Appendable = value_being_transferred("100000", form)(request, messages)

  "Value Being Transferred View" must {

    behave.like(rnrbPage[Int](createView, messageKeyPrefix, "guidance1", "guidance2")(controller))

    behave.like(pageWithoutBackLink[Int](createView, controller))

    behave.like(
      intPage(
        createView,
        messageKeyPrefix,
        ValueBeingTransferredController.onSubmit.url,
        NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage),
        controller
      )
    )
  }

}
