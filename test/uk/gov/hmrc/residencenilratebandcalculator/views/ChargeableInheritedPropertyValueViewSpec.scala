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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ChargeableInheritedPropertyValueController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_inherited_property_value

class ChargeableInheritedPropertyValueViewSpec extends NewIntViewSpecBase {

  val messageKeyPrefix = "chargeable_inherited_property_value"

  val chargeable_inherited_property_value: chargeable_inherited_property_value =
    injector.instanceOf[chargeable_inherited_property_value]

  def createView(form: Form[Int]): HtmlFormat.Appendable = chargeable_inherited_property_value(form)(request, messages)

  "Chargeable Inherited Property Value View" must {

    behave.like(
      rnrbPage[Int](createView, messageKeyPrefix, "guidance1", "guidance2")(
        fakeApplication().injector.instanceOf[ChargeableInheritedPropertyValueController].form()
      )
    )

    behave.like(
      pageWithoutBackLink[Int](
        createView,
        fakeApplication().injector.instanceOf[ChargeableInheritedPropertyValueController].form()
      )
    )

    behave.like(
      intPage(
        createView,
        messageKeyPrefix,
        ChargeableInheritedPropertyValueController.onSubmit.url,
        NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage),
        fakeApplication().injector.instanceOf[ChargeableInheritedPropertyValueController].form()
      )
    )
  }

}
