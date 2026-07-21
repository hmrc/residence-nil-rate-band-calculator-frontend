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
import uk.gov.hmrc.residencenilratebandcalculator.forms.constructors.PositivePercentForm
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewBigDecimalViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_passed_to_direct_descendants

class PercentagePassedToDirectDescendantsViewSpec extends NewBigDecimalViewSpec {

  val messageKeyPrefix = "percentage_passed_to_direct_descendants"

  val percentage_passed_to_direct_descendants: percentage_passed_to_direct_descendants =
    inject[percentage_passed_to_direct_descendants]

  def createView(form: Form[BigDecimal]): Html =
    percentage_passed_to_direct_descendants(form)(request, messages)

  val form: Form[BigDecimal] = Forms.PercentagePassedToDirectDescendants

  "Percentage Passed To Direct Descendants View" must {

    behave.like(
      rnrbPage[BigDecimal](createView, messageKeyPrefix, "guidance")(
        form
      )
    )

    behave.like(
      pageWithoutBackLink[BigDecimal](
        createView,
        form
      )
    )

    behave.like(
      bigDecimalPage(
        createView,
        messageKeyPrefix,
        PercentagePassedToDirectDescendantsController.onSubmit.url,
        PositivePercentForm("", "", ""),
        form
      )
    )
  }

}
