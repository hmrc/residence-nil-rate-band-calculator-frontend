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
import uk.gov.hmrc.residencenilratebandcalculator.forms.constructors.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewIntViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_changed_property

class ValueOfChangedPropertyViewSpec extends NewIntViewSpec {

  val messageKeyPrefix                                     = "value_of_changed_property"
  val value_of_changed_property: value_of_changed_property = inject[value_of_changed_property]
  def createView(form: Form[Int]): Html                    = value_of_changed_property(form)(request, messages)

  val form: Form[Int] = Forms.ValueOfChangedProperty

  "Value Of Changed Property View" must {

    behave.like(
      rnrbPage[Int](createView, messageKeyPrefix, "guidance1", "guidance2", "guidance3")(
        form
      )
    )

    behave.like(
      pageWithoutBackLink[Int](
        createView,
        form
      )
    )

    behave.like(
      intPage(
        createView,
        messageKeyPrefix,
        ValueOfChangedPropertyController.onSubmit.url,
        NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage),
        form
      )
    )
  }

}
