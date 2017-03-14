/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_estate_passed_to_descendants
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

import scala.language.reflectiveCalls

class AnyEstatePassedToDescendantsViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "any_estate_passed_to_descendants"

  def createView(form: Option[Form[Boolean]] = None) = any_estate_passed_to_descendants(frontendAppConfig, backUrl, form, Seq())(request, messages)

  "Any Estate Passed To Descendants View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix,
      "guidance1",
      "reveal_title",
      "guidance2",
      "guidance2.bullet1",
      "guidance2.bullet2",
      "guidance2.bullet3",
      "guidance2.bullet4",
      "guidance2.bullet5",
      "guidance2.bullet6",
      "guidance2.bullet7",
      "guidance3"
    )

    behave like pageWithBackLink[Boolean](createView)

    behave like booleanPage(createView, messageKeyPrefix, AnyEstatePassedToDescendantsController.onSubmit().url)

    behave like pageContainingPreviousAnswers(createView)
  }

}
