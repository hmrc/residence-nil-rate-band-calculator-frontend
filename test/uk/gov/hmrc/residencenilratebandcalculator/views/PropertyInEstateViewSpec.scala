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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.PropertyInEstateController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.*
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewBooleanViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_in_estate

class PropertyInEstateViewSpec extends NewBooleanViewSpec {

  val messageKeyPrefix                                       = "property_in_estate"
  val property_in_estate: property_in_estate                 = inject[property_in_estate]
  def createView(form: Form[Boolean]): HtmlFormat.Appendable = property_in_estate(form)(request, messages)

  "Property In Estate View" must {

    behave.like(
      rnrbPage[Boolean](createView, messageKeyPrefix, "guidance1", "guidance2")(
        inject[PropertyInEstateController].form()
      )
    )

    behave.like(
      pageWithoutBackLink[Boolean](createView, inject[PropertyInEstateController].form())
    )

    behave.like(
      booleanPage(
        createView,
        messageKeyPrefix,
        PropertyInEstateController.onSubmit.url,
        inject[PropertyInEstateController].form(),
        true
      )
    )
  }

}
