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

package uk.gov.hmrc.residencenilratebandcalculator.models

import uk.gov.hmrc.play.test.UnitSpec
import play.api.libs.json._

class PropertyValueAfterExemptionSpec extends UnitSpec {

  "Proeprty Values After Exemption model" must {

    "be parsable as JSON" in {
      Json.toJson(PropertyValueAfterExemption(1, 2)) shouldBe Json.parse("""{"propertyValue":1,"propertyValueCloselyInherited":2}""")
    }

    "give an error when trying to contruct itself from invalid JSON" in {
      Json.fromJson[PropertyValueAfterExemption](JsString("invalid data")) shouldBe a [JsError]
    }
  }
}
