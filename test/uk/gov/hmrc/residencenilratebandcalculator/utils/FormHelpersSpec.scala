/*
 * Copyright 2016 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.forms.{DateForm, NonNegativeIntForm}
import uk.gov.hmrc.residencenilratebandcalculator.models.{Date, Day, Month, Year}

class FormHelpersSpec extends UnitSpec {

  val number = 123
  val day = 1
  val month = 2
  val year = 2000

  "Get Value" must {

    "return an empty string when given nothing" in {
      FormHelpers.getValue(None) shouldBe ""
    }

    "return an empty string when given an Int form with no value" in {
      FormHelpers.getValue[Int](Some(NonNegativeIntForm())) shouldBe ""
    }

    "return the correct value when given an Int form with no value" in {

      val form = NonNegativeIntForm().fill(number)
      FormHelpers.getValue[Int](Some(form)) shouldBe number
    }
  }

  "Get Date Part" must {

    "return an empty string when given nothing" in {
      FormHelpers.getDatePart(None, Day) shouldBe ""
    }

    "return an empty string when given a form with no value" in {
      FormHelpers.getDatePart(Some(DateForm()), Day) shouldBe ""
    }

    "return the day when asked for it" in {
      val date = Date(day, month, year)
      val form = DateForm().fill(date)
      FormHelpers.getDatePart(Some(form), Day) shouldBe day
    }

    "return the month when asked for it" in {
      val date = Date(day, month, year)
      val form = DateForm().fill(date)
      FormHelpers.getDatePart(Some(form), Month) shouldBe month
    }

    "return the year when asked for it" in {
      val date = Date(day, month, year)
      val form = DateForm().fill(date)
      FormHelpers.getDatePart(Some(form), Year) shouldBe year
    }
  }
}
