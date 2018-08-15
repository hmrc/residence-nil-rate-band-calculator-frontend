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
import uk.gov.hmrc.residencenilratebandcalculator.Navigator
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ValueBeingTransferredController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_being_transferred

import scala.language.reflectiveCalls

class ValueBeingTransferredViewSpec extends IntViewSpecBase {

  val messageKeyPrefix = "value_being_transferred"
  val navigator = injector.instanceOf[Navigator]
  var mockSessionConnector: SessionConnector = _
  val mockRnrbConnector : RnrbConnector = mock[RnrbConnector]
  val controller = new ValueBeingTransferredController(messagesApi, mockSessionConnector, navigator, mockRnrbConnector, applicationProvider).form()

  def createView(form: Form[Int]) = value_being_transferred("100000", form, Seq())(request, messages, applicationProvider)

  "Value Being Transferred View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix, "guidance1", "guidance2")(controller)

    behave like pageWithoutBackLink[Int](createView, controller)

    behave like intPage(createView, messageKeyPrefix, ValueBeingTransferredController.onSubmit().url, NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage), controller)

    behave like pageContainingPreviousAnswers(createView, controller)

  }
}
