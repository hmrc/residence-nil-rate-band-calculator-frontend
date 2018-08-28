/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.json

import play.api.data.validation.ValidationError
import play.api.libs.json._
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec

class JsonErrorProcessorSpec extends BaseSpec {

  "JsonErrorProcessor" must {

    "handle a single error" in {
      val err: ValidationError = ValidationError(List("This thing wasn't there when it shoulda."))

      JsonErrorProcessor(Seq((JsPath(), Seq(err))))
        .shouldBe("JSON error: This thing wasn't there when it shoulda.\n")
    }

    "handle a multiple errors" in {
      val err1: ValidationError = ValidationError(List("Value missing."))
      val err2: ValidationError = ValidationError(List("String provided, Int required."))

      JsonErrorProcessor(Seq((JsPath(), Seq(err1)), (JsPath(), Seq(err2))))
        .shouldBe("JSON error: Value missing.\nJSON error: String provided, Int required.\n")
    }
  }
}
