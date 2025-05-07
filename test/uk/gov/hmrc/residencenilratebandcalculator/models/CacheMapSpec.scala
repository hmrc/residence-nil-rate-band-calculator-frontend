/*
 * Copyright 2025 HM Revenue & Customs
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

import uk.gov.hmrc.residencenilratebandcalculator.models.{CacheMap, Date}
import play.api.libs.json.*
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks

class CacheMapSpec
    extends CommonPlaySpec
    with HttpResponseMocks
    with MockSessionConnector
    with WithCommonFakeApplication {

  private val model = CacheMap("id1", Map("key1" -> Json.toJson("value1")))

  private val expectedJson = Json.parse("""{"id":"id1","data":{"key1":"value1"}}""".stripMargin)

  "CacheMap" when {

    "formats" should {
      "successfully serialize CacheMap with empty data" in {
        val emptyDataModel = CacheMap("empty", Map.empty)
        val expectedEmptyJson = Json.obj(
          "id"   -> "empty",
          "data" -> Json.obj()
        )

        Json.toJson(emptyDataModel) mustBe expectedEmptyJson
      }

      "successfully deserialize JSON with empty data" in {
        val jsonWithEmptyData = Json.obj(
          "id"   -> "empty",
          "data" -> Json.obj()
        )

        jsonWithEmptyData.as[CacheMap] mustBe CacheMap("empty", Map.empty)
      }

      "successfully serialize CacheMap with complex nested data" in {
        val complexData = CacheMap(
          "complex",
          Map(
            "string"  -> JsString("value"),
            "number"  -> JsNumber(123),
            "boolean" -> JsBoolean(true),
            "array"   -> JsArray(Seq(JsString("item1"), JsString("item2"))),
            "object" -> JsObject(
              Map(
                "nestedKey"   -> JsString("nestedValue"),
                "nestedArray" -> JsArray(Seq(JsNumber(1), JsNumber(2)))
              )
            )
          )
        )

        val json = Json.toJson(complexData)

        (json \ "id").as[String] mustBe "complex"
        (json \ "data" \ "string").as[String] mustBe "value"
        (json \ "data" \ "number").as[Int] mustBe 123
        (json \ "data" \ "boolean").as[Boolean] mustBe true
        (json \ "data" \ "array").as[Seq[String]] mustBe Seq("item1", "item2")
        (json \ "data" \ "object" \ "nestedKey").as[String] mustBe "nestedValue"
        (json \ "data" \ "object" \ "nestedArray").as[Seq[Int]] mustBe Seq(1, 2)
      }

      "successfully deserialize complex nested JSON" in {
        val complexJson = Json.obj(
          "id" -> "complex",
          "data" -> Json.obj(
            "string"  -> "value",
            "number"  -> 123,
            "boolean" -> true,
            "array"   -> Json.arr("item1", "item2"),
            "object" -> Json.obj(
              "nestedKey"   -> "nestedValue",
              "nestedArray" -> Json.arr(1, 2)
            )
          )
        )

        val result = complexJson.as[CacheMap]

        result.id mustBe "complex"
        result.data("string").as[String] mustBe "value"
        result.data("number").as[Int] mustBe 123
        result.data("boolean").as[Boolean] mustBe true
        result.data("array").as[Seq[String]] mustBe Seq("item1", "item2")
        result.data("object").as[JsObject].value("nestedKey").as[String] mustBe "nestedValue"
        result.data("object").as[JsObject].value("nestedArray").as[Seq[Int]] mustBe Seq(1, 2)
      }

      "fail to deserialize when id is missing" in {
        val invalidJson = Json.obj(
          "data" -> Json.obj("key" -> "value")
        )

        intercept[JsResultException] {
          invalidJson.as[CacheMap]
        }
      }

      "fail to deserialize when data is missing" in {
        val invalidJson = Json.obj(
          "id" -> "missing-data"
        )

        intercept[JsResultException] {
          invalidJson.as[CacheMap]
        }
      }

      "fail to deserialize when data is not an object" in {
        val invalidJson = Json.obj(
          "id"   -> "invalid-data",
          "data" -> "not an object"
        )

        intercept[JsResultException] {
          invalidJson.as[CacheMap]
        }
      }

      "handle round-trip conversion" in {
        val original = CacheMap(
          "round-trip",
          Map("key1" -> JsString("value1"), "key2" -> JsNumber(42))
        )

        val json         = Json.toJson(original)
        val deserialized = json.as[CacheMap]

        deserialized mustBe original
      }
    }

    "getEntry" should {
      "return expected object" in {
        model.getEntry[String]("key1") mustBe Some("value1")
      }

      "return None when missing key" in {
        model.getEntry[String]("key_test") mustBe None
      }

      "throw an exception when provided result type does not match" in {
        val exception = intercept[KeyStoreEntryValidationException] {
          model.getEntry[Date]("key1")
        }

        exception.getMessage mustBe "KeyStore entry for key 'key1' was '\"value1\"'. Attempt to convert to " +
          "uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap$ gave errors: List((,List(JsonValidationError(List(Text 'value1' could not be parsed at index 0),ArraySeq()))))"
      }
    }
  }

}
