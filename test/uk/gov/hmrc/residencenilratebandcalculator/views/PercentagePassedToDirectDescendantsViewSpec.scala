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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_passed_to_direct_descendants
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.PositivePercentForm

import scala.language.reflectiveCalls

class PercentagePassedToDirectDescendantsViewSpec extends IntViewSpecBase {

  val messageKeyPrefix = "percentage_passed_to_direct_descendants"

  def createView(form: Option[Form[Int]] = None) = percentage_passed_to_direct_descendants(frontendAppConfig, backUrl, form, Seq())(request, messages)

  "Percentage Passed To Direct Descendants View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix, "guidance")

    behave like pageWithBackLink[Int](createView)

    behave like intPage(createView, messageKeyPrefix, PercentagePassedToDirectDescendantsController.onSubmit().url, PositivePercentForm())

    behave like pageContainingPreviousAnswers(createView)

  }
}