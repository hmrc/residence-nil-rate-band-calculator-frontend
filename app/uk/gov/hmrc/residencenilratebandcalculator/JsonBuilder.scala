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

import javax.inject.{Inject, Singleton}

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import org.joda.time.LocalDate
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.Constants.jsonKeys
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class JsonBuilder @Inject()() {

  private val schemaDescription: String =
    """{
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
      }""".stripMargin

  private val parsedJson = Json.parse(schemaDescription)
  private val schema = Json.fromJson[SchemaType](parsedJson).get
  private val validator = SchemaValidator()

  private def dateOfDeathIsIneligible(cacheMap: CacheMap): Boolean = {
    val optionDod = cacheMap.getEntry[String](Constants.dateOfDeathId)
    optionDod.fold(true) {
      dod => LocalDate.parse(dod).isBefore(Constants.eligibilityDate)
    }
  }

  def buildFromCacheMap(cacheMap: CacheMap): Either[String, JsValue] = {
    val incomingJson = Json.toJson(setKeys(cacheMap))
    val validationResult = validator.validate(schema, incomingJson).asEither
    validationResult match {
      case Left(error) => {
        val errorString = error.seq.flatMap(_._2).map(_.message).foldLeft(new StringBuilder())(_ append _).toString()
        Left(errorString)
      }
      case Right(json) => {
        if (dateOfDeathIsIneligible(cacheMap)) {
          Left("Date of death is before eligibility date")
        } else {
          Right(json)
        }
      }
    }
  }

  def build(sessionConnector: SessionConnector)(implicit headerCarrier: HeaderCarrier): Future[Either[String, JsValue]] = {
    sessionConnector.fetch().map( optionalCacheMap => {
      optionalCacheMap.fold[Either[String, JsValue]](Left("could not find a cache map")){
        cacheMap => buildFromCacheMap(cacheMap)
      }
    })
  }

  def setKeys(cacheMap: CacheMap) = {
    def keyIsRecognised(key: String) = jsonKeys.keySet.contains(key)
    
    val usableEntries = for {
      recognisedEntries <- cacheMap.data.filterKeys(keyIsRecognised)
    } yield recognisedEntries

    usableEntries map {
      case(key, value) => (jsonKeys(key), value)
    }
  }
}

