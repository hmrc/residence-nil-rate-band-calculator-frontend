/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.PartOfEstatePassingToDirectDescendantsController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.views.html.part_of_estate_passing_to_direct_descendants

import scala.language.reflectiveCalls

class PartOfEstatePassingToDirectDescendantsViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "part_of_estate_passing_to_direct_descendants"

  def createView(form: Form[Boolean]) = part_of_estate_passing_to_direct_descendants(form, Seq())(request, messages, mockConfig)

  "Part Of Estate Passing To Direct Descendants View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix,
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
    )(fakeApplication.injector.instanceOf[PartOfEstatePassingToDirectDescendantsController].form())

    behave like pageWithoutBackLink[Boolean](createView, fakeApplication.injector.instanceOf[PartOfEstatePassingToDirectDescendantsController].form())

    behave like booleanPage(createView, messageKeyPrefix, routes.PartOfEstatePassingToDirectDescendantsController.onSubmit().url, fakeApplication.injector.instanceOf[PartOfEstatePassingToDirectDescendantsController].form(), true)

    behave like pageContainingPreviousAnswers(createView, fakeApplication.injector.instanceOf[PartOfEstatePassingToDirectDescendantsController].form())
  }

}
