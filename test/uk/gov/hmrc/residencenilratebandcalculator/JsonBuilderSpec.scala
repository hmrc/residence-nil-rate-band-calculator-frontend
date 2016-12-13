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

package uk.gov.hmrc.residencenilratebandcalculator

import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsNumber, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector


class JsonBuilderSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication with MockSessionConnector {

  "JsonBuilder" must {

    "return a Left with an error message" when {

      "the CacheMap does not contain a value for the chargeableTransferAmount" in {
        val cacheMap = CacheMap(id = "aaaa", data = Map(Constants.dateOfDeathId -> JsString("2017-09-10")))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property ChargeableTransferAmount missing.")
      }

      "the CacheMap does not contain a value for the dateOfDeath" in {
        val cacheMap = CacheMap(id = "bbbb", data = Map(Constants.chargeableTransferAmountId -> JsNumber(100)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property DateOfDeath missing.")
      }
    }
  }
}
