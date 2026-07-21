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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_property

class GrossingUpOnEstatePropertyViewSpec extends NewBooleanViewSpec {

  val messageKeyPrefix = "grossing_up_on_estate_property"

  val grossing_up_on_estate_property: grossing_up_on_estate_property =
    inject[grossing_up_on_estate_property]

  def createView(form: Form[Boolean]): Html = grossing_up_on_estate_property(form)(request, messages)

  val form: Form[Boolean] = Forms.GrossingUpOnEstateProperty

  "Grossing Up On Estate Property View" must {

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
        GrossingUpOnEstatePropertyController.onSubmit.url,
        form,
        true
      )
    )
  }

}
