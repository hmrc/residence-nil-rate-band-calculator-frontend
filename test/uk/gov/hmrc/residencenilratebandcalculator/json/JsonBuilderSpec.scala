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

package uk.gov.hmrc.residencenilratebandcalculator.json

import com.eclipsesource.schema.SchemaType
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsNumber, JsString, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.{JsonInvalidException, NoCacheMapException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class JsonBuilderSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication with MockSessionConnector {

  val rnrbConnector = mock[RnrbConnector]
  when(rnrbConnector.getSuccessfulResponseSchema) thenReturn Future.successful(
    Success[SchemaType](Json.fromJson[SchemaType](Json.parse("""{
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

    "return a Failure with a JasonInvalidException in it" when {

      "the CacheMap does not contain a value for the ChargeableTransferAmount" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueId -> JsNumber(200)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: Property chargeableTransferAmount missing.\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: Property dateOfDeath missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap does not contain a value for the GrossEstateValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
            "JSON error: Property grossEstateValue missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap does not contain a value for the PropertyValue" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))

        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
            "JSON error: Property propertyValue missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap does not contain a value for the PercentageCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
            "JSON error: Property percentageCloselyInherited missing.\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
            "JSON error: -100 is smaller than required minimum value of 0.\n"

          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a[JsonInvalidException]
            exception.getMessage shouldBe
            "JSON error: -200 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
            "JSON error: -200 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: -1 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: 'xxxx-yy-zz' does not match pattern ^\\d{4}-\\d{2}-\\d{2}$.\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: Date of death is before eligibility date\n"
          }
          case Success(json) => fail
        }
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
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: 101 exceeds maximum value of 100.\n"
          }
          case Success(json) => fail
        }
      }
    }

    "return a Future containing a Left with an error message" when {

      "the SessionConnector does not return a CacheMap" in {
        jsonBuilder.build(mockSessionConnector)(mock[HeaderCarrier]).map {
          result => result match {
            case Failure(exception) => {
              exception shouldBe a [NoCacheMapException]
              exception.getMessage shouldBe "Unable to retrieve cache map from SessionConnector"
            }
            case Success(json) => fail
          }
        }
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
        buildResult match {
          case Failure(exception) => fail
          case Success(json) => json shouldBe Json.toJson(jsonBuilder.setKeys(cacheMap))
        }
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

  "only get the schema once" in {
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    val hc = mock[HeaderCarrier]
    jsonBuilder.build(mockSessionConnector)(hc)
    jsonBuilder.build(mockSessionConnector)(hc)

    verify(rnrbConnector, times(1)).getSuccessfulResponseSchema
  }
}
