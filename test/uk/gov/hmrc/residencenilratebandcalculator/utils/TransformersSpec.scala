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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import org.joda.time.LocalDate
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec
import uk.gov.hmrc.residencenilratebandcalculator.utils.Transformers._
import org.scalatest.matchers
import matchers.should.Matchers.convertToAnyShouldWrapper

class TransformersSpec extends BaseSpec {

  "intToString" must {
    "convert an int to a string" in {
      intToString(1) shouldBe Some("1")
    }
  }

  "stringToInt" must {
    "convert a numeric string to an integer" in {
      stringToInt(Some("1")) shouldBe 1
    }

    "convert a non-numeric string to 0" in {
      stringToInt(Some("not a number")) shouldBe 0
    }

    "convert None to 0" in {
      stringToInt(None) shouldBe 0
    }

    "convert an empty string to 0" in {
      stringToInt(Some("")) shouldBe 0
    }
  }

  "constructDate" must {
    "return a date when given valid inputs" in {
      constructDate(1, 1, 2000) shouldBe new LocalDate(2000, 1, 1)
    }

    "throw an exception when given invalid inputs" in {
      intercept[Exception] {
        constructDate(30, 2, 2000)
      }
    }
  }

  "transformDateFormat" must {
    "behave correctly for a valid date where there are no quotes" in {
      transformDateFormat("2017-5-12") shouldBe "12052017"
    }
    "behave correctly for a valid date where month has no leading zero" in {
      transformDateFormat("\"2017-5-12\"") shouldBe "12052017"
    }
    "behave correctly for a valid date where day has no leading zero" in {
      transformDateFormat("\"2017-05-03\"") shouldBe "03052017"
    }
    "throw an exception where day is empty" in {
      a[RuntimeException] shouldBe thrownBy {
        transformDateFormat("\"2017--03\"")
      }
    }
    "throw an exception where month is empty" in {
      a[RuntimeException] shouldBe thrownBy {
        transformDateFormat("\"2017-03-\"")
      }
    }
    "throw an exception where year is empty" in {
      a[RuntimeException] shouldBe thrownBy {
        transformDateFormat("\"--03-04\"")
      }
    }
    "throw an exception where day is too long" in {
      a[RuntimeException] shouldBe thrownBy {
        transformDateFormat("2017-5-124")
      }
    }
    "throw an exception where month is too long" in {
      a[RuntimeException] shouldBe thrownBy {
        transformDateFormat("2017-124-5")
      }
    }
  }

  "stripOffQuotesIfPresent" must {
    "strip off quotes" in {
      stripOffQuotesIfPresent("\"abc\"") shouldBe "abc"
    }
    "work where no quotes" in {
      stripOffQuotesIfPresent("abc") shouldBe "abc"
    }
  }

  "transformDecimalFormat" must {
    "transform correctly where decimal number of less than maximum size" in {
      transformDecimalFormat("34.8899") shouldBe " 348899"
    }

    "transform correctly where decimal number of maximum size" in {
      transformDecimalFormat("234.8899") shouldBe "2348899"
    }

    "transform correctly where decimal number with less than max mantissa" in {
      transformDecimalFormat("234.889") shouldBe "234889 "
    }

    "transform correctly where decimal number with no decimal point where max" in {
      transformDecimalFormat("234") shouldBe "234    "
    }

    "transform correctly where decimal number with no decimal point where less than max" in {
      transformDecimalFormat("34") shouldBe " 34    "
    }

    "transform correctly where empty string" in {
      transformDecimalFormat("") shouldBe "       "
    }
  }
}
