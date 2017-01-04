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

import javax.inject.{Inject, Singleton}

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import org.joda.time.LocalDate
import play.api.data.validation.ValidationError
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.Constants.jsonKeys
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.{JsonInvalidException, NoCacheMapException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class JsonBuilder @Inject()(rnrbConnector: RnrbConnector) {

  private lazy val futureSchema = rnrbConnector.getSuccessfulResponseSchema
  private val validator = SchemaValidator()

  private def dateOfDeathIsIneligible(cacheMap: CacheMap): Boolean = {
    val optionDod = cacheMap.getEntry[String](Constants.dateOfDeathId)
    optionDod.fold(true) {
      dod => LocalDate.parse(dod).isBefore(Constants.eligibilityDate)
    }
  }

  def buildFromCacheMap(cacheMap: CacheMap): Future[Try[JsValue]] = {
    futureSchema.map {
      case Success(schema) => {
        val incomingJson = Json.toJson(constructDataFromCacheMap(cacheMap))
        val validationResult = validator.validate(schema, incomingJson).asEither
        validationResult match {
          case Left(error) => {
            Failure(new JsonInvalidException(JsonErrorProcessor(error)))
          }
          case Right(json) => {
            if (dateOfDeathIsIneligible(cacheMap)) {
              Failure(new JsonInvalidException("JSON error: Date of death is before eligibility date\n"))
            } else {
              Success(json)
            }
          }
        }
      }
      case Failure(exception) => Failure(exception)
    }
  }

  def build(sessionConnector: SessionConnector)(implicit headerCarrier: HeaderCarrier) = {
    sessionConnector.fetch.flatMap {
      case None => Future.successful(Failure(new NoCacheMapException("Unable to retrieve cache map from SessionConnector")))
      case Some(cacheMap) => buildFromCacheMap(cacheMap)
    }
  }

  def constructDataFromCacheMap(cacheMap: CacheMap) = {
    def keyIsRecognised(key: String) = jsonKeys.keySet.contains(key)

    val usableEntries = for {
      recognisedEntries <- cacheMap.data.filterKeys(keyIsRecognised)
    } yield recognisedEntries

    val dataWithCorrectKeys = usableEntries map {
      case (key, value) => (jsonKeys(key), value)
    }

    dataWithCorrectKeys ++ constructAdditionalData(cacheMap)
  }

  def constructAdditionalData(cacheMap: CacheMap): Map[String, JsValue] = {
    if (cacheMap.data.get(Constants.anyBroughtForwardAllowanceId).contains(JsBoolean(false))) {
      Map(jsonKeys(Constants.broughtForwardAllowanceId) -> JsNumber(0))
    } else {
      Map()
    }
  }
}
