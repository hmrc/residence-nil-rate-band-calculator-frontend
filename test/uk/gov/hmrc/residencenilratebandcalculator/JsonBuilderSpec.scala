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

import com.eclipsesource.schema.SchemaType
import org.mockito.Mockito.when
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsNumber, JsString, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JsonBuilderSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication with MockSessionConnector {

  val rnrbConnector = mock[RnrbConnector]
  when(rnrbConnector.getSuccessfulResponseSchema) thenReturn Future.successful(
    Right[String, SchemaType](Json.fromJson[SchemaType](Json.parse("""{
        |"$$schema": "http://json-schema.org/draft-04/schema#",
        |"title": "Test RNRB Schema",
        |"description": "A simple schema to test against",
        |"type:": "object",
        |"properties": {
        |  "chargeableTransferAmount": {"type": "integer", "minimum": 0},
        |  "dateOfDeath": {"type": "string", "pattern": "^\\d{4}-\\d{2}-\\d{2}$"},
        |  "grossEstateValue": {"type": "integer", "minimum": 0},
        |  "propertyValue": {"type": "integer", "minimum": 0},
        |  "percentageCloselyInherited": {"type": "integer", "minimum": 0, "maximum": 100}
        |},
        |"required": ["chargeableTransferAmount", "dateOfDeath", "grossEstateValue", "propertyValue", "percentageCloselyInherited"]
      }""".stripMargin)).get))

  "JsonBuilder" must {

    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return a Left with an error message" when {

      "the CacheMap does not contain a value for the ChargeableTransferAmount" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueId -> JsNumber(200)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Left("Property chargeableTransferAmount missing.")
      }

      "the CacheMap does not contain a value for the DateOfDeath" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueId -> JsNumber(200)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Left("Property dateOfDeath missing.")
      }

      "the CacheMap does not contain a value for the GrossEstateValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Left("Property grossEstateValue missing.")
      }

      "the CacheMap does not contain a value for the PropertyValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Left("Property propertyValue missing.")
      }

      "the CacheMap does not contain a value for the PercentageCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Left("Property percentageCloselyInherited missing.")
      }

      "the CacheMap contains a negative value for ChargeableTransferAmount" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(-100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Left("101 exceeds maximum value of 100.")
      }
    }

    "return a Future containing a Left with an error message" when {

      "the SessionConnector does not return a CacheMap" in {
        jsonBuilder.build(mockSessionConnector)(mock[HeaderCarrier]).map(result => result shouldBe Left("could not find a cache map"))
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
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult shouldBe Right(Json.toJson(jsonBuilder.setKeys(cacheMap)))
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
        jsonBuilder.build(mockSessionConnector)(mock[HeaderCarrier]).map(result =>
          result shouldBe Right(Json.toJson(cacheMap.data)))
      }
    }
  }

  "Set Keys" must {

    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return a map with the correct JSON keys when all of the keys are recognised" in {
      val cacheMap = CacheMap(id = cacheMapId, data =
        Map(
          Constants.dateOfDeathId -> JsString("2017-09-10"),
          Constants.grossEstateValueId -> JsNumber(200)))

      jsonBuilder.setKeys(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.dateOfDeathId) -> JsString("2017-09-10"),
        Constants.jsonKeys(Constants.grossEstateValueId) -> JsNumber(200))
    }

    "return a map with the correct translation of the recognised keys, and no entry for unrecognised keys, when both recognised and unrecognised keys are supplied" in {
      val madeUpKey = "A made-up key"

      val cacheMap = CacheMap(id = cacheMapId, data =
        Map(
          Constants.dateOfDeathId -> JsString("2017-09-10"),
          Constants.grossEstateValueId -> JsNumber(200),
          madeUpKey -> JsNumber(200)))

      jsonBuilder.setKeys(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.dateOfDeathId) -> JsString("2017-09-10"),
        Constants.jsonKeys(Constants.grossEstateValueId) -> JsNumber(200))
    }

    "return an empty map when all keys are unrecognised" in {
      val madeUpKey = "A made-up key"
      val anotherMadeUpKey = "Another made-up key"

      val cacheMap = CacheMap(id = cacheMapId, data =
        Map(
          anotherMadeUpKey -> JsNumber(200),
          madeUpKey -> JsNumber(200)))

      jsonBuilder.setKeys(cacheMap) shouldBe Map()
    }

    "return an empty map when given an empty cache map" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map())
      jsonBuilder.setKeys(cacheMap) shouldBe Map()
    }
  }
}
