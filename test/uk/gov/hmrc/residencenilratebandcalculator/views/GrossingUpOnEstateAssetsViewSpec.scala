/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.GrossingUpOnEstateAssetsController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.views.html.grossing_up_on_estate_assets

class GrossingUpOnEstateAssetsViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "grossing_up_on_estate_assets"

  def createView(form: Form[Boolean]) = grossing_up_on_estate_assets(frontendAppConfig, form, Seq())(request, messages, applicationProvider)

  "Grossing Up On Estate AssetsView" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix, "guidance1", "guidance2")(fakeApplication.injector.instanceOf[GrossingUpOnEstateAssetsController].form())

    behave like pageWithoutBackLink[Boolean](createView, fakeApplication.injector.instanceOf[GrossingUpOnEstateAssetsController].form())

    behave like booleanPage(createView, messageKeyPrefix, GrossingUpOnEstateAssetsController.onSubmit().url, fakeApplication.injector.instanceOf[GrossingUpOnEstateAssetsController].form(), true)

    behave like pageContainingPreviousAnswers(createView, fakeApplication.injector.instanceOf[GrossingUpOnEstateAssetsController].form())

  }
}
