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

import play.api.libs.json.JsNumber
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class CascadeUpsertSpec extends UnitSpec {

  val cacheMapKey = "a"
  val testNumber = 123

  "Cascade Upsert" when {

    "asked to save for Estate Has Property" must {

      "not delete existing 'Property Value' when saving the value 'true'" in {
        val originalCacheMap = CacheMap(cacheMapKey, Map(Constants.propertyValueId -> JsNumber(testNumber)))
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, true, originalCacheMap)
        updatedCacheMap.data should contain (Constants.propertyValueId -> JsNumber(testNumber))
      }

      "delete existing 'Property Value' when saving the value 'false'" in {
        val originalCacheMap = CacheMap(cacheMapKey, Map(Constants.propertyValueId -> JsNumber(testNumber)))
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, originalCacheMap)
        updatedCacheMap.data.keys should not contain Constants.propertyValueId
      }
    }
  }
}
