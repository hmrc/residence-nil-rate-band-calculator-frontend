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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec

class CurrencyFormatterSpec extends BaseSpec {

  "CurrencyFormatter" must {
    "format 0 GBP as £0 without a trailing fractional part" in {
      CurrencyFormatter.format(0) mustBe "£0"
    }

    "format 25 GBP as £25 without a trailing fractional part" in {
      CurrencyFormatter.format(25) mustBe "£25"
    }

    "format 1000000 GBP as £1,000,000 without a trailing fractional part" in {
      CurrencyFormatter.format(1000000) mustBe "£1,000,000"
    }

    "format -1000000 GBP as -£1,000,000 without a trailing fractional part" in {
      CurrencyFormatter.format(-1000000) mustBe "-£1,000,000"
    }

    "format \"0\" GBP as £0" in {
      CurrencyFormatter.format("0") mustBe "£0"
    }

    "format NotANumber by throwing a NumberFormatException" in {
      val exception = intercept[NumberFormatException] {
        CurrencyFormatter.format("NotANumber")
      }
      exception.getMessage mustBe "For input string: \"NotANumber\""
    }
  }

}
