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

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap

object JsonBuilder {

  lazy val schemaDescription: String =
    """{
        |"$$schema": "http://json-schema.org/draft-04/schema#",
        |"title": "Test RNRB Schema",
        |"description": "A simple schema to test against",
        |"type:": "object",
        |"properties": {
        |  "ChargeableTransferAmount": {"type": "integer"},
        |  "DateOfDeath": {"type": "string"}
        |},
        |"required": ["ChargeableTransferAmount", "DateOfDeath"]
      }""".stripMargin

  def build(cacheMap: CacheMap): Either[String, String] = {

    val incomingJson = Json.toJson(cacheMap.data)
    val parsedJson = Json.parse(schemaDescription)
    val schema = Json.fromJson[SchemaType](parsedJson).get
    val validator = SchemaValidator()
    val validationResult = validator.validate(schema, incomingJson).asEither
    validationResult match {
      case Left(error) => {
        val errorString = error.seq.flatMap(_._2).map(_.message).foldLeft(new StringBuilder())(_ append _).toString()
        Left(errorString) }
      case Right(json) => Right(json.toString())
    }
  }
}

