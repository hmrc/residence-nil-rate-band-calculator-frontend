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

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import play.api.data.validation.ValidationError
import play.api.libs.json._
import uk.gov.hmrc.play.test.UnitSpec

/**
  * Created by andy on 19/12/2016.
  */
class JsonErrorProcessorSpec extends UnitSpec {
  val schema = Json.fromJson[SchemaType](Json.parse("""{
                                |"$$schema": "http://json-schema.org/draft-04/schema#",
                                |"title": "Test JsonErrorProcessor",
                                |"description": "A simple schema to test against",
                                |"type:": "object",
                                |"properties": {
                                |  "aNumber": {"type": "integer", "minimum": 0},
                                |  "aString": {"type": "string", "pattern": "a+"}
                                |},
                                |"required": ["aNumber","aString"]
                                |}""".stripMargin))
  val validator = SchemaValidator()

  "JsonErrorProcessor" must {
    "handle a single error" in {
      val p = Json.toJson(Map(
        "aString" -> JsString("aaaaaa")))
      val result = validator.validate(schema.get, p).asEither
      result match {
        case Left(error: Seq[(JsPath, Seq[ValidationError])]) => JsonErrorProcessor(error) shouldBe "JSON error: Property aNumber missing.\n"
        case Right(success) => fail
      }
    }

    "handle a multiple errors" in {
      val p = Json.toJson(Map(
        "aString" -> JsString("bbbbb")))
      val result = validator.validate(schema.get, p).asEither
      result match {
        case Left(error: Seq[(JsPath, Seq[ValidationError])]) => JsonErrorProcessor(error) shouldBe
          "JSON error: 'bbbbb' does not match pattern a+.\nJSON error: Property aNumber missing.\n"
        case Right(success) => fail
      }
    }
  }
}
