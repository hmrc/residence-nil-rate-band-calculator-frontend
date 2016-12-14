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
import play.api.libs.json.{JsNumber, JsString, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector

import scala.concurrent.ExecutionContext.Implicits.global

class JsonBuilderSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication with MockSessionConnector {

  "JsonBuilder" must {

    val cacheMapId = "aaaa"

    "return a Left with an error message" when {

      "the CacheMap does not contain a value for the ChargeableTransferAmount" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property ChargeableTransferAmount missing.")
      }

      "the CacheMap does not contain a value for the DateOfDeath" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property DateOfDeath missing.")
      }

      "the CacheMap does not contain a value for the GrossEstateValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property GrossEstateValue missing.")
      }

      "the CacheMap does not contain a value for the PropertyValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property PropertyValue missing.")
      }

      "the CacheMap does not contain a value for the PercentageCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Property PercentageCloselyInherited missing.")
      }

      "the CacheMap contains a negative value for ChargeableTransferAmount" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(-100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("-100 is smaller than required minimum value of 0.")
      }

      "the CacheMap contains a negative value for GrossEstateValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(-200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("-200 is smaller than required minimum value of 0.")
      }

      "the CacheMap contains a negative value for PropertyValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(-200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("-200 is smaller than required minimum value of 0.")
      }

      "the CacheMap contains a negative value for PercentageCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(-1)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("-1 is smaller than required minimum value of 0.")
      }

      "the CacheMap contains an unparseable date for DateOfDeath" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("xxxx-yy-zz"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("'xxxx-yy-zz' does not match pattern ^\\d{4}-\\d{2}-\\d{2}$.")
      }

      "the CacheMap contains a date prior to the eligibility date for DateOfDeath" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("1996-01-01"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("Date of death is before eligibility date")
      }

      "the CacheMap contains a value greater than 100 for PercentageCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(101)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Left("101 exceeds maximum value of 100.")
      }
    }

    "return a Future containing a Left with an error message" when {

      "the SessionConnector does not return a CacheMap" in {
        JsonBuilder(mockSessionConnector)(mock[HeaderCarrier]).map(result => result shouldBe Left("could not find a cache map"))
      }

    }

    "return a Right containing Json" when {

      "the CacheMap contains a valid value for all properties" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = JsonBuilder.build(cacheMap)
        buildResult shouldBe Right(Json.toJson(cacheMap.data))
      }
    }

    "return a Future containing a Right with Json" when {

      "the CacheMap is present" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        setCacheMap(cacheMap)
        JsonBuilder(mockSessionConnector)(mock[HeaderCarrier]).map(result =>
          result shouldBe Right(Json.toJson(cacheMap.data)))
      }
    }

  }
}
