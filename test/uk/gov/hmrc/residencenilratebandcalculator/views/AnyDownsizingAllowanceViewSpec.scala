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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_downsizing_allowance

import scala.language.reflectiveCalls

class AnyDownsizingAllowanceViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "any_downsizing_allowance"

  def createView(form: Option[Form[Boolean]] = None) = any_downsizing_allowance(frontendAppConfig, backUrl, form)(request, messages)

  "Any Downsizing Allowance View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix, "guidance")

    behave like pageWithBackLink[Boolean](createView)

    behave like booleanPage(createView, messageKeyPrefix, AnyDownsizingAllowanceController.onSubmit().url)
  }
}
