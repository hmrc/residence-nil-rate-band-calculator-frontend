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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.controllers.{ValueAvailableWhenPropertyChangedController, routes}
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_available_when_property_changed

class ValueAvailableWhenPropertyChangedViewSpec extends NewIntViewSpecBase {

  val messageKeyPrefix = "value_available_when_property_changed"
  val navigator: Navigator = injector.instanceOf[Navigator]
  var mockSessionConnector: SessionConnector = _
  val mockRnrbConnector : RnrbConnector = mock[RnrbConnector]
  val mockValidatedSession: ValidatedSession = mock[ValidatedSession]
  val value_available_when_property_changed: value_available_when_property_changed = injector.instanceOf[value_available_when_property_changed]

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  val controller: Form[Int] = new ValueAvailableWhenPropertyChangedController(messagesControllerComponents, mockSessionConnector, navigator, mockRnrbConnector, mockValidatedSession, value_available_when_property_changed).form()

  def createView(form: Form[Int]): HtmlFormat.Appendable = value_available_when_property_changed("100000", form)(request, messages)

  "Value Available When Property Changed View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix, "guidance1")(controller)

    behave like pageWithoutBackLink[Int](createView, controller)

    behave like intPage(createView, messageKeyPrefix, routes.ValueAvailableWhenPropertyChangedController.onSubmit.url, NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage), controller)

    "contain the appropriate maximum value of transferable residence nil rate band" in {
      val doc = asDocument(createView(controller))
      val maxValue = "100000"
      assertContainsText(doc, maxValue)
    }

  }
}
