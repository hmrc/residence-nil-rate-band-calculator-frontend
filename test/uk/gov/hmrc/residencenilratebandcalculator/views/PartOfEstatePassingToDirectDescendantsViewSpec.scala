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
import uk.gov.hmrc.residencenilratebandcalculator.views.helpers.NewBooleanViewSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.part_of_estate_passing_to_direct_descendants

class PartOfEstatePassingToDirectDescendantsViewSpec extends NewBooleanViewSpec {

  val messageKeyPrefix = "part_of_estate_passing_to_direct_descendants"

  val part_of_estate_passing_to_direct_descendants: part_of_estate_passing_to_direct_descendants =
    inject[part_of_estate_passing_to_direct_descendants]

  def createView(form: Form[Boolean]): Html =
    part_of_estate_passing_to_direct_descendants(form)(request, messages)

  val form: Form[Boolean] = Forms.PartOfEstatePassingToDirectDescendants

  "Part Of Estate Passing To Direct Descendants View" must {

    behave.like(
      rnrbPage[Boolean](
        createView,
        messageKeyPrefix,
        "guidance1",
        "reveal_title",
        "guidance2",
        "guidance3",
        "guidance3.bullet1",
        "guidance3.bullet2",
        "guidance3.bullet3",
        "guidance3.bullet4",
        "guidance3.bullet5",
        "guidance3.bullet6",
        "guidance3.bullet7",
        "guidance3.bullet8",
        "guidance4"
      )(form)
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
        routes.PartOfEstatePassingToDirectDescendantsController.onSubmit.url,
        form,
        true
      )
    )
  }

}
