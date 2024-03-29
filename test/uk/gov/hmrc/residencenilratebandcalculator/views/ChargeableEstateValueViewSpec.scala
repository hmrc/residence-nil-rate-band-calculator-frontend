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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ChargeableEstateValueController
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_estate_value

class ChargeableEstateValueViewSpec extends NewIntViewSpecBase {

  val messageKeyPrefix = "chargeable_estate_value"
  val chargeable_estate_value: chargeable_estate_value = injector.instanceOf[chargeable_estate_value]
  def createView(form: Form[Int]): HtmlFormat.Appendable = chargeable_estate_value(form)(request, messages)

  "Chargeable Estate Value View" must {

    behave like rnrbPage[Int](createView, messageKeyPrefix, "guidance")(fakeApplication().injector.instanceOf[ChargeableEstateValueController].form())

    behave like pageWithoutBackLink[Int](createView, fakeApplication().injector.instanceOf[ChargeableEstateValueController].form())

    behave like intPage(createView, messageKeyPrefix, ChargeableEstateValueController.onSubmit.url, NonNegativeIntForm(errorMessage, errorMessage, errorMessage, errorMessage), fakeApplication().injector.instanceOf[ChargeableEstateValueController].form())
  }
}
