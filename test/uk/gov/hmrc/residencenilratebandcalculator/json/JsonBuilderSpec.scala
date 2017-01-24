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

package uk.gov.hmrc.residencenilratebandcalculator.json

import com.eclipsesource.schema.SchemaType
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json._
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

  val schema = """{
                 |"$$schema": "http://json-schema.org/draft-04/schema#",
                 |"title": "Test RNRB Schema",
                 |"description": "A simple schema to test against",
                 |"type:": "object",
                 |"properties": {
                 |  "chargeableTransferAmount": {"type": "integer", "minimum": 0},
                 |  "dateOfDeath": {"type": "string", "pattern": "^\\d{4}-\\d{2}-\\d{2}$"},
                 |  "grossEstateValue": {"type": "integer", "minimum": 0},
                 |  "propertyValue": {"type": "integer", "minimum": 0},
                 |  "percentageCloselyInherited": {"type": "integer", "minimum": 0, "maximum": 100},
                 |  "propertyValueAfterExemption": {
                 |    "type": "object",
                 |    "properties": {
                 |      "value": {"type": "integer", "minimum": 0},
                 |      "valueCloselyInherited": {"type": "integer", "minimum": 0}
                 |    },
                 |    "required": ["value", "valueCloselyInherited"]
                 |  },
                 |  "downsizingDetails": {
                 |    "type": "object",
                 |    "properties": {
                 |       "dateOfDisposal": {"type": "string", "pattern": "^\\d{4}-\\d{2}-\\d{2}$"},
                 |       "valueOfDisposedProperty": {"type": "integer", "minimum": 0},
                 |       "valueCloselyInherited": {"type": "integer", "minimum": 0},
                 |       "broughtForwardAllowanceAtDisposal": {"type": "integer", "minimum": 0}
                 |    },
                 |    "required": ["dateOfDisposal", "valueOfDisposedProperty", "valueCloselyInherited", "broughtForwardAllowanceAtDisposal"]
                 |  }
                 |},
                 |"required": ["chargeableTransferAmount", "dateOfDeath", "grossEstateValue", "propertyValue", "percentageCloselyInherited"]
      }""".stripMargin

  val rnrbConnector = mock[RnrbConnector]
  when(rnrbConnector.getSuccessfulResponseSchema) thenReturn Future.successful(Success[SchemaType](Json.fromJson[SchemaType](Json.parse(schema)).get))

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

      "the CacheMap contains a PropertyValueAfterExemption structure which does not contain a value" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueAfterExemptionId -> JsObject(Map(
              "valueCloselyInherited" -> JsNumber(1)
            ))))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: Property value missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains a PropertyValueAfterExemption structure which does not contain a valueCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueAfterExemptionId -> JsObject(Map(
              "value" -> JsNumber(1)
            ))))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: Property valueCloselyInherited missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains a value of true for anyDownsizingDetails but date of disposal is missing" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.valueOfDisposedPropertyId -> JsNumber(100),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(false),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: Property dateOfDisposal missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains a value of true for anyDownsizingDetails but value of disposed property is missing" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.dateOfDisposalId -> JsString("2017-09-10"),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(false),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: Property valueOfDisposedProperty missing.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains a value of true for anyDownsizingDetails and anyBroughtForwardAllowanceOnDisposal, but broughtForwardAllowanceOnDisposal is missing" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.dateOfDisposalId -> JsString("2017-09-10"),
            Constants.valueOfDisposedPropertyId -> JsNumber(100),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
            Constants.assetsPassingToDirectDescendantsId -> JsNumber(100),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: Property broughtForwardAllowanceAtDisposal missing.\n"
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

      "the CacheMap contains a negative value for PropertyValueAfterExemption.value" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueAfterExemptionId -> JsObject(Map(
              "value" -> JsNumber(-1),
              "valueCloselyInherited" -> JsNumber(1)
            ))))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: -1 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail("JSON is: " + json)
        }
      }

      "the CacheMap contains a negative value for PropertyValueAfterExemption.valueCloselyInherited" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueAfterExemptionId -> JsObject(Map(
              "value" -> JsNumber(1),
              "valueCloselyInherited" -> JsNumber(-1)
            ))))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe
              "JSON error: -1 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail("JSON is: " + json)
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

      "the CacheMap contains true for anyDownsizingDetails and an unparseable date for DateOfDisposal" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.dateOfDisposalId -> JsString("xxxx-yy-zz"),
            Constants.valueOfDisposedPropertyId -> JsNumber(100),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
            Constants.assetsPassingToDirectDescendantsId -> JsNumber(100),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: 'xxxx-yy-zz' does not match pattern ^\\d{4}-\\d{2}-\\d{2}$.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains true for anyDownsizingDetails and a negative value for value of disposed property" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.dateOfDisposalId -> JsString("2017-01-01"),
            Constants.valueOfDisposedPropertyId -> JsNumber(-1),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
            Constants.assetsPassingToDirectDescendantsId -> JsNumber(100),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: -1 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains true for anyDownsizingDetails and a negative value for assets passing to direct descendant" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.dateOfDisposalId -> JsString("2017-01-01"),
            Constants.valueOfDisposedPropertyId -> JsNumber(100),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
            Constants.assetsPassingToDirectDescendantsId -> JsNumber(-1),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: -1 is smaller than required minimum value of 0.\n"
          }
          case Success(json) => fail
        }
      }

      "the CacheMap contains true for anyDownsizingDetails and a negative value for brought forward allowance on disposal" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.anyDownsizingAllowanceId -> JsBoolean(true),
            Constants.dateOfDisposalId -> JsString("2017-01-01"),
            Constants.valueOfDisposedPropertyId -> JsNumber(100),
            Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
            Constants.assetsPassingToDirectDescendantsId -> JsNumber(100),
            Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true),
            Constants.broughtForwardAllowanceOnDisposalId -> JsNumber(-1)))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => {
            exception shouldBe a [JsonInvalidException]
            exception.getMessage shouldBe "JSON error: -1 is smaller than required minimum value of 0.\n"
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

      "the CacheMap contains a valid value for all mandatory properties" in {
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
          case Success(json) => json shouldBe Json.toJson(jsonBuilder.constructDataFromCacheMap(cacheMap))
        }
      }

      "the CacheMap contains a valid value for all properties" in {
        val cacheMap = CacheMap(id = cacheMapId, data =
          Map(
            Constants.chargeableTransferAmountId -> JsNumber(100),
            Constants.dateOfDeathId -> JsString("2017-09-10"),
            Constants.grossEstateValueId -> JsNumber(200),
            Constants.propertyValueId -> JsNumber(200),
            Constants.percentageCloselyInheritedId -> JsNumber(50),
            Constants.propertyValueAfterExemptionId -> JsObject(Map(
              "value" -> JsNumber(1),
              "valueCloselyInherited" -> JsNumber(1)
            ))))
        val buildResult = await(jsonBuilder.buildFromCacheMap(cacheMap))
        buildResult match {
          case Failure(exception) => fail
          case Success(json) => json shouldBe Json.toJson(jsonBuilder.constructDataFromCacheMap(cacheMap))
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

  "Construct Data From Cache Map" must {

    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return a map with the correct JSON keys when all of the keys are recognised" in {
      val cacheMap = CacheMap(id = cacheMapId, data =
        Map(
          Constants.dateOfDeathId -> JsString("2017-09-10"),
          Constants.grossEstateValueId -> JsNumber(200)))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.dateOfDeathId) -> JsString("2017-09-10"),
        Constants.jsonKeys(Constants.grossEstateValueId) -> JsNumber(200))
    }

    "return a map with the correct translation of the recognised keys, and no entry for unrecognised keys," + "" +
      " when both recognised and unrecognised keys are supplied" in {
      val madeUpKey = "A made-up key"

      val cacheMap = CacheMap(id = cacheMapId, data =
        Map(
          Constants.dateOfDeathId -> JsString("2017-09-10"),
          Constants.grossEstateValueId -> JsNumber(200),
          madeUpKey -> JsNumber(200)))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
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

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map()
    }

    "return an empty map when given an empty cache map" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map())
      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map()
    }

    "return a map with a brought forward allowance of 0 when 'any brought forward allowance' is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyBroughtForwardAllowanceId -> JsBoolean(false)
      ))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.broughtForwardAllowanceId) -> JsNumber(0)
      )
    }

    "return a map with the actual brought forward allowance when 'any brought forward allowance' is true" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyBroughtForwardAllowanceId -> JsBoolean(true),
        Constants.broughtForwardAllowanceId -> JsNumber(1)
      ))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.broughtForwardAllowanceId) -> JsNumber(1)
      )
    }

    "return a map with downsizing details when they are present" in {
      val cacheMap = CacheMap(id = cacheMapId, data =
        Map(
          Constants.dateOfDeathId -> JsString("2017-09-10"),
          Constants.grossEstateValueId -> JsNumber(200),
          Constants.anyDownsizingAllowanceId -> JsBoolean(true),
          Constants.valueOfDisposedPropertyId -> JsNumber(100)))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.dateOfDeathId) -> JsString("2017-09-10"),
        Constants.jsonKeys(Constants.grossEstateValueId) -> JsNumber(200),
        Constants.downsizingDetails -> Json.toJson(Map(
          Constants.downsizingKeys(Constants.valueOfDisposedPropertyId )-> JsNumber(100))
        )
      )
    }

    "return a map with propertyValue and percentageCloselyInherited of 0 when estateHasProperty is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.estateHasPropertyId -> JsBoolean(false)
      ))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.propertyValueId) -> JsNumber(0),
        Constants.jsonKeys(Constants.percentageCloselyInheritedId) -> JsNumber(0)
      )
    }

    "return a map with the actual propertyValue and percentageCloselyInherited when estateHasProperty is true" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.estateHasPropertyId -> JsBoolean(true),
        Constants.propertyValueId -> JsNumber(1),
        Constants.percentageCloselyInheritedId -> JsNumber(2)
      ))

      jsonBuilder.constructDataFromCacheMap(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.propertyValueId) -> JsNumber(1),
        Constants.jsonKeys(Constants.percentageCloselyInheritedId) -> JsNumber(2)
      )
    }
  }

  "only get the schema once when it is successfully retrieved" in {
    val hc = mock[HeaderCarrier]
    val rnrbConnector = mock[RnrbConnector]
    when(rnrbConnector.getSuccessfulResponseSchema) thenReturn Future.successful(Success[SchemaType](Json.fromJson[SchemaType](Json.parse(schema)).get))

    val jsonBuilder = new JsonBuilder(rnrbConnector)

    val x = jsonBuilder.build(mockSessionConnector)(hc)
    val y = jsonBuilder.build(mockSessionConnector)(hc)

    verify(rnrbConnector, times(1)).getSuccessfulResponseSchema
  }

  "try to get the schema again when the first call to retrieve it fails" in {
    val hc = mock[HeaderCarrier]
    val rnrbConnector = mock[RnrbConnector]
    when(rnrbConnector.getSuccessfulResponseSchema).thenReturn(Failure(new JsonInvalidException("An error")))

    val jsonBuilder = new JsonBuilder(rnrbConnector)

    val x = jsonBuilder.build(mockSessionConnector)(hc)
    val y = jsonBuilder.build(mockSessionConnector)(hc)

    verify(rnrbConnector, times(2)).getSuccessfulResponseSchema
  }

  "handleEstateHasProperty" must {
    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return an empty map when estateHasPropertyId is not present in the cache map" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map())
      jsonBuilder.handleEstateHasProperty(cacheMap) shouldBe Map()
    }

    "return an empty map when estateHasPropertyId is true" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.estateHasPropertyId -> JsBoolean(true)
      ))
      jsonBuilder.handleEstateHasProperty(cacheMap) shouldBe Map()
    }

    "return a map containing propertyValue and percentageCloselyInherited set to 0 when estateHasProperty is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.estateHasPropertyId -> JsBoolean(false)
      ))
      jsonBuilder.handleEstateHasProperty(cacheMap) shouldBe Map(
        Constants.jsonKeys(Constants.propertyValueId) -> JsNumber(0),
        Constants.jsonKeys(Constants.percentageCloselyInheritedId) -> JsNumber(0)
      )
    }
  }

  "handleAssetsPassingToDirectDescendants" must {
    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return an empty map when anyAssetsPassingToDirectDescendantsId is not present in the cache map" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map())
      jsonBuilder.handleAssetsPassingToDirectDescendants(cacheMap) shouldBe Map()
    }

    "return an empty map when anyAssetsPassingToDirectDescendantsId is true" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true)
      ))
      jsonBuilder.handleAssetsPassingToDirectDescendants(cacheMap) shouldBe Map()
    }

    "return a map containing assetsPassingToDirectDescendant with a value of 0 when anyAssetsPassingToDirectDescendantsId is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(false)
      ))
      jsonBuilder.handleAssetsPassingToDirectDescendants(cacheMap) shouldBe Map(Constants.downsizingKeys(Constants.assetsPassingToDirectDescendantsId) -> JsNumber(0))
    }
  }

  "handleBroughtForwardAllowanceOnDisposal" must {
    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return an empty map when anyBroughtForwardAllowanceOnDisposal is not present in the cache map" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map())
      jsonBuilder.handleBroughtForwardAllowanceOnDisposal(cacheMap) shouldBe Map()
    }

    "return an empty map when anyAssetsPassingToDirectDescendantsId is true" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true)
      ))
      jsonBuilder.handleBroughtForwardAllowanceOnDisposal(cacheMap) shouldBe Map()
    }

    "return a map containing assetsPassingToDirectDescendant with a value of 0 when anyAssetsPassingToDirectDescendantsId is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)
      ))
      jsonBuilder.handleBroughtForwardAllowanceOnDisposal(cacheMap) shouldBe Map(Constants.downsizingKeys(Constants.broughtForwardAllowanceOnDisposalId) -> JsNumber(0))
    }
  }

  "constructDownsizingDetails" must {
    val cacheMapId = "aaaa"
    val jsonBuilder = new JsonBuilder(rnrbConnector)

    "return an empty map when anyDownsizingDetails is not present in the cache map" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map())
      jsonBuilder.constructDownsizingDetails(cacheMap) shouldBe Map()
    }

    "return an empty map when anyDownsizingDetails is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyDownsizingAllowanceId -> JsBoolean(false)
      ))
      jsonBuilder.constructDownsizingDetails(cacheMap) shouldBe Map()
    }

    "return the actual value for assets passing to direct descendant when it is present" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyDownsizingAllowanceId -> JsBoolean(true),
        Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
        Constants.assetsPassingToDirectDescendantsId -> JsNumber(100)
      ))
      jsonBuilder.constructDownsizingDetails(cacheMap) shouldBe Map(
        Constants.downsizingDetails -> Json.toJson(Map(
          Constants.downsizingKeys(Constants.assetsPassingToDirectDescendantsId) -> JsNumber(100)))
      )
    }

    "return values of 0 for assets passing to direct descendant and brought forward allowance on disposal when anyAssetsPassingToDirectDescendant is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyDownsizingAllowanceId -> JsBoolean(true),
        Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(false)
      ))
      jsonBuilder.constructDownsizingDetails(cacheMap) shouldBe Map(
        Constants.downsizingDetails -> Json.toJson(Map(
          Constants.downsizingKeys(Constants.assetsPassingToDirectDescendantsId) -> JsNumber(0),
          Constants.downsizingKeys(Constants.broughtForwardAllowanceOnDisposalId) -> JsNumber(0)))
      )
    }

    "return the actual value for brought forward allowance on disposal when it is present" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyDownsizingAllowanceId -> JsBoolean(true),
        Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true),
        Constants.broughtForwardAllowanceOnDisposalId -> JsNumber(100)
      ))
      jsonBuilder.constructDownsizingDetails(cacheMap) shouldBe Map(
        Constants.downsizingDetails -> Json.toJson(Map(
          Constants.downsizingKeys(Constants.broughtForwardAllowanceOnDisposalId) -> JsNumber(100)))
      )
    }

    "return a value of 0 for brought forward allowance on disposal when anyBroughtForwardAllowanceOnDisposal is false" in {
      val cacheMap = CacheMap(id = cacheMapId, data = Map(
        Constants.anyDownsizingAllowanceId -> JsBoolean(true),
        Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(false)
      ))
      jsonBuilder.constructDownsizingDetails(cacheMap) shouldBe Map(
        Constants.downsizingDetails -> Json.toJson(Map(
          Constants.downsizingKeys(Constants.broughtForwardAllowanceOnDisposalId) -> JsNumber(0)))
      )
    }
  }
}
