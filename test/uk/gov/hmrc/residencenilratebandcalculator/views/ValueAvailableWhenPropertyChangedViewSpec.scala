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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.forms.Forms
import uk.gov.hmrc.residencenilratebandcalculator.forms.constructors.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewIntViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_available_when_property_changed

class ValueAvailableWhenPropertyChangedViewSpec extends NewIntViewSpec {

  val messageKeyPrefix = "value_available_when_property_changed"

  val value_available_when_property_changed: value_available_when_property_changed =
    inject[value_available_when_property_changed]

  val form: Form[Int] = Forms.ValueAvailableWhenPropertyChanged

  def createView(form: Form[Int]): Html =
    value_available_when_property_changed("100000", form)(request, messages)

  "Value Available When Property Changed View" must {

    behave.like(rnrbPage[Int](createView, messageKeyPrefix, "guidance1")(form))

    behave.like(pageWithoutBackLink[Int](createView, form))

    behave.like(
      intPage(
        createView,
        messageKeyPrefix,
        routes.ValueAvailableWhenPropertyChangedController.onSubmit.url,
        NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage),
        form
      )
    )

    "contain the appropriate maximum value of transferable residence nil rate band" in {
      val doc      = asDocument(createView(form))
      val maxValue = "100000"
      assertContainsText(doc, maxValue)
    }

  }

}
