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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import uk.gov.hmrc.residencenilratebandcalculator.models.PropertyValueAfterExemption
import uk.gov.hmrc.residencenilratebandcalculator.forms.LargeIntFormatter.largeIntFormat

object PropertyValueAfterExemptionForm {

  def apply(): Form[PropertyValueAfterExemption] = Form(
    mapping(
      "value" -> of[Int].verifying(Constraints.min(0)),
      "valueCloselyInherited" -> of[Int].verifying(Constraints.min(0))
    )(PropertyValueAfterExemption.apply)(PropertyValueAfterExemption.unapply)
      verifying("property_value_after_exemption.value_closely_inherited_greater_than_value.error", x => x.valueCloselyInherited <= x.value)
  )
}
