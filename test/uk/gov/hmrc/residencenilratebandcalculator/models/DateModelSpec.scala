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

import org.joda.time.LocalDate
import play.api.libs.json.{JsError, JsString, Json}
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec

class DateModelSpec extends BaseSpec {
  "Date Model" must {
    "write itself as JSON exactly as if it were a Joda LocalDate" in {
      val dateToJson = Json.toJson(Date(1, 1, 2000))
      val localDateToJson = Json.toJson(new LocalDate(2000, 1, 1))
      dateToJson shouldBe localDateToJson
    }

    "be interchangeable with Joda LocalDate" in {
      val day = 1
      val month = 6
      val year = 2017
      val date = Date(day, month, year)
      val jodaDate = new LocalDate(year, month, day)

      Json.fromJson[LocalDate](Json.toJson(date)).get shouldBe jodaDate
      Json.fromJson[Date](Json.toJson(jodaDate)).get shouldBe date
    }

    "construct itself from a valid JSON representation" in {
      Json.fromJson[Date](JsString("2000-01-01")).get shouldBe Date(1, 1, 2000)
      Json.fromJson[Date](JsString("2000-1-1")).get shouldBe Date(1, 1, 2000)
    }

    "return JsFailure when constructing itself from invalid data" in {
      Json.fromJson[Date](JsString("invalid data")) shouldBe a [JsError]
    }
  }
}
