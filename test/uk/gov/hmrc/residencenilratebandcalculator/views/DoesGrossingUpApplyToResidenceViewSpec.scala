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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.does_grossing_up_apply_to_residence
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

class DoesGrossingUpApplyToResidenceViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "does_grossing_up_apply_to_residence"

  def createView(form: Option[Form[Boolean]] = None) = does_grossing_up_apply_to_residence(frontendAppConfig, backUrl, form)(request, messages)

  "Does Grossing Up Apply To Residence View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix, "guidance", "guidance.bullet1", "guidance.bullet2")

    behave like pageWithBackLink[Boolean](createView)

    behave like booleanPage(createView, messageKeyPrefix, DoesGrossingUpApplyToResidenceController.onSubmit().url)
  }
}
