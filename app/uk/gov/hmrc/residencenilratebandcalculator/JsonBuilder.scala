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
import uk.gov.hmrc.residencenilratebandcalculator.Constants.jsonKeys
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class JsonBuilder @Inject()(rnrbConnector: RnrbConnector) {

  private lazy val futureSchema = rnrbConnector.getSuccessfulResponseSchema.map {
    schemaDescription => Json.fromJson[SchemaType](schemaDescription).get
  }
  private val validator = SchemaValidator()

  private def dateOfDeathIsIneligible(cacheMap: CacheMap): Boolean = {
    val optionDod = cacheMap.getEntry[String](Constants.dateOfDeathId)
    optionDod.fold(true) {
      dod => LocalDate.parse(dod).isBefore(Constants.eligibilityDate)
    }
  }

  def buildFromCacheMap(cacheMap: CacheMap): Future[Either[String, JsValue]] = {
    futureSchema.map {
      schema =>
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
  }

  def build(sessionConnector: SessionConnector)(implicit headerCarrier: HeaderCarrier): Future[Either[String, JsValue]] = {
    sessionConnector.fetch.flatMap {
      case None => Future.successful(Left("No cache map returned"))
      case Some(cacheMap) => buildFromCacheMap(cacheMap)
    }
  }

  def setKeys(cacheMap: CacheMap) = {
    def keyIsRecognised(key: String) = jsonKeys.keySet.contains(key)

    val usableEntries = for {
      recognisedEntries <- cacheMap.data.filterKeys(keyIsRecognised)
    } yield recognisedEntries

    usableEntries map {
      case (key, value) => (jsonKeys(key), value)
    }
  }
}

