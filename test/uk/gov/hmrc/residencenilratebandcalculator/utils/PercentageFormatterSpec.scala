/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec
import org.scalatest.Matchers.convertToAnyShouldWrapper

class PercentageFormatterSpec extends BaseSpec {
  "PercentageFormatter" must {
    "format 0  as 0% without a trailing fractional part" in {
      PercentageFormatter.format(0) shouldBe "0%"
    }

    "format 25 as 25 without a trailing fractional part" in {
      PercentageFormatter.format(25) shouldBe "25%"
    }

    "format 50.0001 as 50.0001%" in {
      PercentageFormatter.format(50.0001) shouldBe "50.0001%"
    }

    "format 50.00019999999 as 50.0002%" in {
      PercentageFormatter.format(50.00019999999) shouldBe "50.0002%"
    }
  }
}
