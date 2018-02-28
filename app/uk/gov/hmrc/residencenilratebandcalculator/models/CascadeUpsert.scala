/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.Singleton

import org.joda.time.LocalDate
import play.api.Logger
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants

import scala.util.{Failure, Success, Try}

@Singleton
class CascadeUpsert {

  def apply[A](key: String, value: A, originalCacheMap: CacheMap)(implicit wts: Writes[A]): CacheMap =
    funcMap.get(key).fold(store(key, value, originalCacheMap)) { fn => fn(Json.toJson(value), originalCacheMap)}

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      Constants.propertyInEstateId -> ((v, cm) => propertyInEstate(v, cm)),
      Constants.exemptionsAndReliefClaimedId -> ((v, cm) => exemptionsAndReliefClaimedClearance(v, cm)),
      Constants.transferAnyUnusedThresholdId -> ((v, cm) => transferAnyUnusedThreshold(v, cm)),
      Constants.claimDownsizingThresholdId -> ((v, cm) => claimDownsizingThreshold(v, cm)),
      Constants.assetsPassingToDirectDescendantsId -> ((v, cm) => assetsPassingToDirectDescendants(v, cm)),
      Constants.transferAvailableWhenPropertyChangedId -> ((v, cm) => transferAvailableWhenPropertyChanged(v, cm)),
      Constants.propertyPassingToDirectDescendantsId -> ((v, cm) => propertyPassingToDirectDescendants(v, cm)),
      Constants.datePropertyWasChangedId -> ((v, cm) => datePropertyWasChanged(v, cm))
    )

  private def store[A](key:String, value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]) =
    cacheMap copy (data = cacheMap.data + (key -> Json.toJson(value)))

  private def clearIfFalse[A](key: String, value: A, keysToRemove: Set[String], cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data.filterKeys(s => !keysToRemove.contains(s)))
      case _ => cacheMap
    }
    store(key, value, mapToStore)
  }

  private def propertyInEstate[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearIfFalse(Constants.propertyInEstateId, value,
      Set(
        Constants.propertyValueId,
        Constants.propertyPassingToDirectDescendantsId,
        Constants.percentagePassedToDirectDescendantsId,
        Constants.exemptionsAndReliefClaimedId,
        Constants.chargeablePropertyValueId,
        Constants.chargeableInheritedPropertyValueId),
      cacheMap)

  private def propertyPassingToDirectDescendants[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap = {
    val keysToRemoveWhenNone = Set(
      Constants.percentagePassedToDirectDescendantsId,
      Constants.exemptionsAndReliefClaimedId,
      Constants.chargeablePropertyValueId,
      Constants.chargeableInheritedPropertyValueId
    )

    val mapToStore = value match {
      case JsString(Constants.none) => cacheMap copy (data = cacheMap.data.filterKeys(s => !keysToRemoveWhenNone.contains(s)))
      case JsString(Constants.all) => cacheMap copy (data = cacheMap.data - Constants.percentagePassedToDirectDescendantsId)
      case _ => cacheMap
    }
    store(Constants.propertyPassingToDirectDescendantsId, value, mapToStore)
  }

  private def exemptionsAndReliefClaimedClearance[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearIfFalse(Constants.exemptionsAndReliefClaimedId, value,
      Set(
        Constants.chargeablePropertyValueId,
        Constants.chargeableInheritedPropertyValueId,
        Constants.grossingUpOnEstatePropertyId),
      cacheMap)

  private def transferAnyUnusedThreshold[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearIfFalse(Constants.transferAnyUnusedThresholdId,
      value,
      Set(
        Constants.valueBeingTransferredId,
        Constants.transferAvailableWhenPropertyChangedId,
        Constants.valueAvailableWhenPropertyChangedId),
      cacheMap)

  private def claimDownsizingThreshold[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearIfFalse(Constants.claimDownsizingThresholdId,
      value,
      Set(
        Constants.datePropertyWasChangedId,
        Constants.valueOfChangedPropertyId,
        Constants.assetsPassingToDirectDescendantsId,
        Constants.valueOfAssetsPassingId,
        Constants.transferAvailableWhenPropertyChangedId,
        Constants.grossingUpOnEstateAssetsId,
        Constants.valueAvailableWhenPropertyChangedId),
      cacheMap)

  private def assetsPassingToDirectDescendants[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearIfFalse(Constants.assetsPassingToDirectDescendantsId, value,
      Set(
        Constants.valueOfAssetsPassingId,
        Constants.transferAvailableWhenPropertyChangedId,
        Constants.grossingUpOnEstateAssetsId,
        Constants.valueAvailableWhenPropertyChangedId),
      cacheMap)

  private def transferAvailableWhenPropertyChanged[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearIfFalse(Constants.transferAvailableWhenPropertyChangedId, value, Set(Constants.valueAvailableWhenPropertyChangedId), cacheMap)

  private def datePropertyWasChanged[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap = {
    val keysToRemoveWhenDateBeforeDownsizingDate = Set(
      Constants.valueOfChangedPropertyId,
      Constants.assetsPassingToDirectDescendantsId,
      Constants.grossingUpOnEstateAssetsId,
      Constants.valueOfAssetsPassingId,
      Constants.transferAvailableWhenPropertyChangedId,
      Constants.valueAvailableWhenPropertyChangedId
    )

    val keysToRemoveWhenDateBeforeEligibilityDate = Set(
      Constants.transferAvailableWhenPropertyChangedId,
      Constants.valueAvailableWhenPropertyChangedId
    )

    val mapToStore = value match {
      case JsString(d) =>
        Try(LocalDate.parse(d)) match {
          case Success(parsedDate) if parsedDate isBefore Constants.downsizingEligibilityDate =>
            cacheMap copy (data = cacheMap.data.filterKeys(s => !keysToRemoveWhenDateBeforeDownsizingDate.contains(s)))
          case Success(parsedDate) if parsedDate isBefore Constants.eligibilityDate =>
            cacheMap copy (data = cacheMap.data.filterKeys(s => !keysToRemoveWhenDateBeforeEligibilityDate.contains(s)))
          case Failure(e) =>
            val msg = s"Unable to parse value $value as the Date Property Was Changed"
            Logger.error(msg, e)
            throw new RuntimeException(msg)
          case _ => cacheMap
        }
      case _ => cacheMap
    }

    store(Constants.datePropertyWasChangedId, value, mapToStore)
  }
}
