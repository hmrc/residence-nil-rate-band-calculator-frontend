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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_property
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

class GrossingUpOnEstatePropertyViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "grossing_up_on_estate_property"

  def createView(form: Option[Form[Boolean]] = None) = grossing_up_on_estate_property(frontendAppConfig, form, Seq())(request, messages)

  "Grossing Up On Estate Property View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix, "guidance1", "guidance2")

    behave like pageWithoutBackLink[Boolean](createView)

    behave like booleanPage(createView, messageKeyPrefix, GrossingUpOnEstatePropertyController.onSubmit().url)

    behave like pageContainingPreviousAnswers(createView)

  }
}
