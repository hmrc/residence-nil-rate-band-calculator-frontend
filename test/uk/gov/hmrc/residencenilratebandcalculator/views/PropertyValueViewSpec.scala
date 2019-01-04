/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.PropertyValueController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value

import scala.language.reflectiveCalls

class PropertyValueViewSpec extends IntViewSpecBase {

  val messageKeyPrefix = "property_value"

  def createView(form: Form[Int]) = property_value(form, Seq())(request, messages, applicationProvider)

  "Property Value View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix, "guidance1", "guidance2", "guidance3",
      "guidance3.bullet1", "guidance3.bullet2", "guidance4")(fakeApplication.injector.instanceOf[PropertyValueController].form())

    behave like pageWithoutBackLink[Int](createView, fakeApplication.injector.instanceOf[PropertyValueController].form())

    behave like intPage(createView, messageKeyPrefix, "/calculate-additional-inheritance-tax-threshold/property-value", NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage), fakeApplication.injector.instanceOf[PropertyValueController].form())

    behave like pageContainingPreviousAnswers(createView, fakeApplication.injector.instanceOf[PropertyValueController].form())

  }
}
