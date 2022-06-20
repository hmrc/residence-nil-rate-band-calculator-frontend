/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ValueOfEstateController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_of_estate

import scala.language.reflectiveCalls

class ValueOfEstateViewSpec extends NewIntViewSpecBase {

  val messageKeyPrefix = "value_of_estate"
  val value_of_estate = injector.instanceOf[value_of_estate]
  def createView(form: Form[Int]) = value_of_estate(form)(request, messages)

  "Value of Estate View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix,
      "guidance1",
      "guidance1.bullet1",
      "guidance1.bullet2",
      "guidance1.bullet3",
      "guidance1.bullet4",
      "guidance1.bullet5",
      "guidance1.bullet6",
      "guidance2"
    )(fakeApplication.injector.instanceOf[ValueOfEstateController].form())

    behave like pageWithoutBackLink[Int](createView, fakeApplication.injector.instanceOf[ValueOfEstateController].form())

    behave like intPage(createView, messageKeyPrefix, ValueOfEstateController.onSubmit.url, NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage), fakeApplication.injector.instanceOf[ValueOfEstateController].form())
  }
}
