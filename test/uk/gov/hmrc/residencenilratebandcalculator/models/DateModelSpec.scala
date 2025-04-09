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

package uk.gov.hmrc.residencenilratebandcalculator.models

import play.api.libs.json._
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec

import java.time.LocalDate

class DateSpec extends BaseSpec {

  "Date Model" must {
    "write itself as JSON exactly as if it were a Java LocalDate" in {
      val day             = 1
      val month           = 1
      val year            = 2000
      val dateToJson      = Json.toJson(Date(LocalDate.of(year, month, day)))
      val localDateToJson = Json.toJson(LocalDate.of(year, month, day))
      dateToJson mustBe localDateToJson
    }

    "be interchangeable with Java LocalDate" in {
      val day      = 1
      val month    = 6
      val year     = 2017
      val date     = Date(LocalDate.of(year, month, day))
      val javaDate = LocalDate.of(year, month, day)

      Json.fromJson[LocalDate](Json.toJson(date)).get mustBe javaDate
      Json.fromJson[Date](Json.toJson(javaDate)).get mustBe date
    }

    "construct itself from a valid JSON representation" in {
      val day   = 1
      val month = 1
      val year  = 2000
      Json.fromJson[Date](JsString("2000-01-01")).get mustBe Date(LocalDate.of(year, month, day))
//      Json.fromJson[Date](JsString("2000-1-1")).get mustBe Date(LocalDate.of(year, month, day))
    }

    "return JsFailure when constructing itself from invalid data" in {
      Json.fromJson[Date](JsString("invalid data")) mustBe a[JsError]
    }
  }

}
