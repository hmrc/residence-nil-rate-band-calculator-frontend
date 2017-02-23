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
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.Constants.{downsizingKeys, jsonKeys}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.{JsonInvalidException, NoCacheMapException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class JsonBuilder @Inject()(rnrbConnector: RnrbConnector) {

  private val validator = SchemaValidator()
  private var cachedSchema: Option[SchemaType] = None

  private def getSchema = cachedSchema match {
    case Some(schema) => Future.successful(Success(schema))
    case None =>
      rnrbConnector.getSuccessfulResponseSchema.map {
        case Success(schema) =>
          cachedSchema = Some(schema)
          Success(schema)
        case Failure(exception) => Failure(exception)
    }
  }


  private def dateOfDeathIsIneligible(cacheMap: CacheMap): Boolean = {
    val optionDod = cacheMap.getEntry[String](Constants.dateOfDeathId)
    optionDod.fold(true) {
      dod => LocalDate.parse(dod).isBefore(Constants.eligibilityDate)
    }
  }

  private def dateOfDisposalIsIneligible(cacheMap: CacheMap): Boolean = {
    val optionDod = cacheMap.getEntry[String](Constants.dateOfDisposalId)
    optionDod.fold(true) {
      dod => LocalDate.parse(dod).isBefore(Constants.downsizingEligibilityDate)
    }
  }

  def buildFromCacheMap(cacheMap: CacheMap): Future[Try[JsValue]] = getSchema.map {
    case Success(schema) =>
      val incomingJson = Json.toJson(constructDataFromCacheMap(cacheMap))
      println("%%%%%%%%%%%%%%%JSON IS%%%%%%%%%%%%%%%%")
      println(incomingJson)
      val validationResult = validator.validate(schema, incomingJson).asEither
      validationResult match {
        case Left(error) =>
          Failure(new JsonInvalidException(JsonErrorProcessor(error)))
        case Right(json) =>
          if (dateOfDeathIsIneligible(cacheMap)) {
            Failure(new JsonInvalidException("JSON error: Date of death is before eligibility date\n"))
          } else {
            Success(json)
          }
      }
    case Failure(exception) => Failure(exception)
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

    dataWithCorrectKeys ++
      handleBroughtForwardAllowance(cacheMap) ++
      handleEstateHasProperty(cacheMap) ++
      handlePropertyCloselyInherited(cacheMap) ++
      constructDownsizingDetails(cacheMap)
  }

  def handleBroughtForwardAllowance(cacheMap: CacheMap) = cacheMap.data.get(Constants.anyBroughtForwardAllowanceId) match {
    case Some(JsBoolean(false)) => Map(jsonKeys(Constants.broughtForwardAllowanceId) -> JsNumber(0))
    case _ => Map()
  }

  def handleEstateHasProperty(cacheMap: CacheMap) = cacheMap.data.get(Constants.estateHasPropertyId) match {
    case Some(JsBoolean(false)) => Map(
      jsonKeys(Constants.propertyValueId) -> JsNumber(0),
      jsonKeys(Constants.percentageCloselyInheritedId) -> JsNumber(0))
    case _ => Map()
  }

  def handlePropertyCloselyInherited(cacheMap: CacheMap) = cacheMap.data.get(Constants.anyPropertyCloselyInheritedId) match {
    case Some(JsBoolean(false)) => Map(jsonKeys(Constants.percentageCloselyInheritedId) -> JsNumber(0))
    case _ => Map()
  }

  def constructDownsizingDetails(cacheMap: CacheMap) = cacheMap.data.get(Constants.anyDownsizingAllowanceId) match {
    case Some(JsBoolean(true)) =>
      def keyIsRecognised(key: String) = downsizingKeys.keySet.contains(key)

      val usableEntries = for {
        recognisedEntries <- cacheMap.data.filterKeys(keyIsRecognised)
      } yield recognisedEntries

      val dataWithCorrectKeys = usableEntries map {
        case (key, value) => (downsizingKeys(key), value)
      }

      val allData = dataWithCorrectKeys ++ handleAssetsPassingToDirectDescendants(cacheMap) ++ handleBroughtForwardAllowanceOnDisposal(cacheMap)

      Map(Constants.downsizingDetails -> Json.toJson(allData))
    case _ => Map()
  }

  def handleAssetsPassingToDirectDescendants(cacheMap: CacheMap) = cacheMap.data.get(Constants.anyAssetsPassingToDirectDescendantsId) match {
    case Some(JsBoolean(false)) => Map(downsizingKeys(Constants.assetsPassingToDirectDescendantsId) -> JsNumber(0))
    case _ => Map()
  }

  def handleBroughtForwardAllowanceOnDisposal(cacheMap: CacheMap) = {
    if (cacheMap.data.get(Constants.anyAssetsPassingToDirectDescendantsId).contains(JsBoolean(false))
        || cacheMap.data.get(Constants.anyBroughtForwardAllowanceOnDisposalId).contains(JsBoolean(false))) {
      Map(downsizingKeys(Constants.broughtForwardAllowanceOnDisposalId) -> JsNumber(0))
    } else {
      Map()
    }
  }
}
