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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import org.scalatestplus.play.PlaySpec
import org.scalatest.matchers
import matchers.should.Matchers.convertToAnyShouldWrapper

class FormValidatorsSpec extends PlaySpec {

  "The isValidDate method" must {

    "return a response of true" when{

      "provided with a valid date" in {
        FormValidators.isValidDate(4,6,2017) shouldBe true
      }
    }

    "return a response of false" when {

      "provided with an invalid day" in {
        FormValidators.isValidDate(80,6,2017) shouldBe false
      }

      "provided with an invalid month" in {
        FormValidators.isValidDate(4,13,2017) shouldBe false
      }
    }
  }
}
