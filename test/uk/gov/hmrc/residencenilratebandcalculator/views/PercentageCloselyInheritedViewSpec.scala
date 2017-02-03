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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.percentage_closely_inherited
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.PercentForm

import scala.language.reflectiveCalls

class PercentageCloselyInheritedViewSpec extends IntViewSpecBase {

  val messageKeyPrefix = "percentage_closely_inherited"

  def createView(form: Option[Form[Int]] = None) = percentage_closely_inherited(frontendAppConfig, backUrl, form)(request, messages)

  "Percentage Closely Inherited View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix,
      "guidance",
      "guidance.bullet1",
      "guidance.bullet2",
      "guidance.bullet3",
      "guidance.bullet4",
      "guidance.bullet5",
      "guidance.bullet6",
      "guidance.bullet7"
    )

    behave like pageWithBackLink[Int](createView)

    behave like intPage(createView, messageKeyPrefix, PercentageCloselyInheritedController.onSubmit().url, PercentForm())
  }
}
