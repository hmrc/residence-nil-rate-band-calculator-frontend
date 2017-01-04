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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.residencenilratebandcalculator.forms.PropertyValueAfterExemptionForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{Value, PropertyValueAfterExemption, ValueCloselyInherited}

class PropertyValueAfterExemptionFormHelperSpec extends UnitSpec {

  "Get Value By Key" when {

    "asking for Value" must {

      "return an empty string when given nothing" in {
        PropertyValueAfterExemptionFormHelper.getValueByKey(None, Value) shouldBe ""
      }

      "return an empty string when given a form with no value" in {
        PropertyValueAfterExemptionFormHelper.getValueByKey(Some(PropertyValueAfterExemptionForm()), Value) shouldBe ""
      }

      "return the value when one is present in the form" in {
        val values = PropertyValueAfterExemption(1, 2)
        val form = PropertyValueAfterExemptionForm().fill(values)
        PropertyValueAfterExemptionFormHelper.getValueByKey(Some(form), Value) shouldBe 1
      }
    }

    "asking for Value Closely Inherited" must {

      "return an empty string when given nothing" in {
        PropertyValueAfterExemptionFormHelper.getValueByKey(None, ValueCloselyInherited) shouldBe ""
      }

      "return an empty string when given a form with no value" in {
        PropertyValueAfterExemptionFormHelper.getValueByKey(Some(PropertyValueAfterExemptionForm()), ValueCloselyInherited) shouldBe ""
      }

      "return the value closely inherited when one is present in the form" in {
        val values = PropertyValueAfterExemption(1, 2)
        val form = PropertyValueAfterExemptionForm().fill(values)
        PropertyValueAfterExemptionFormHelper.getValueByKey(Some(form), ValueCloselyInherited) shouldBe 2
      }
    }
  }
}
