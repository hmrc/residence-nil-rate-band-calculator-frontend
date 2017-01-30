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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import javax.inject.Inject

import play.api.libs.json.{JsValue, Json, Reads, Writes}
import uk.gov.hmrc.http.cache.client.{CacheMap, SessionCache}
import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SessionConnector @Inject()(val sessionRepository: SessionRepository) {
  var funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      Constants.estateHasPropertyId -> ((v, cm) => estateHasProperty(v, cm)),
      Constants.anyExemptionId -> ((v, cm) => anyExemptionClearance(v, cm)),
      Constants.anyBroughtForwardAllowanceId -> ((v, cm) => anyBroughtForwardAllowance(v, cm)),
      Constants.anyDownsizingAllowanceId -> ((v, cm) => anyDownsizingAllowance(v, cm)),
      Constants.anyAssetsPassingToDirectDescendantsId -> ((v, cm) => anyAssetsPassingToDirectDescendants(v, cm)),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> ((v, cm) => anyBroughtForwardAllowanceOnDisposal(v, cm)))

  private def clearance[A](key: String, value: A, keysToRemove: Set[String], cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap = {
    val mapWithDeletedKeys = cacheMap copy (data = cacheMap.data.filterKeys(s => !keysToRemove.contains(s)))
    mapWithDeletedKeys copy (data = mapWithDeletedKeys.data + (key -> Json.toJson(value)))
  }

  private def estateHasProperty[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearance(Constants.estateHasPropertyId, value, Set(Constants.propertyValueId, Constants.percentageCloselyInheritedId, Constants.anyExemptionId, Constants.propertyValueAfterExemptionId), cacheMap)

  private def anyExemptionClearance[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearance(Constants.anyExemptionId, value, Set(Constants.propertyValueAfterExemptionId), cacheMap)

  private def anyBroughtForwardAllowance[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearance(Constants.anyBroughtForwardAllowanceId, value, Set(Constants.broughtForwardAllowanceId), cacheMap)

  private def anyDownsizingAllowance[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearance(Constants.anyDownsizingAllowanceId,
     value,
     Set(
       Constants.dateOfDisposalId,
       Constants.valueOfDisposedPropertyId,
       Constants.anyAssetsPassingToDirectDescendantsId,
       Constants.assetsPassingToDirectDescendantsId,
       Constants.anyBroughtForwardAllowanceOnDisposalId,
       Constants.broughtForwardAllowanceOnDisposalId),
     cacheMap)

  private def anyAssetsPassingToDirectDescendants[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearance(Constants.anyAssetsPassingToDirectDescendantsId, value,
      Set(
        Constants.assetsPassingToDirectDescendantsId,
        Constants.anyBroughtForwardAllowanceOnDisposalId,
        Constants.broughtForwardAllowanceOnDisposalId),
      cacheMap)

  private def anyBroughtForwardAllowanceOnDisposal[A](value: A, cacheMap: CacheMap)(implicit wrts: Writes[A]): CacheMap =
    clearance(Constants.anyBroughtForwardAllowanceId, value, Set(Constants.anyBroughtForwardAllowanceOnDisposalId), cacheMap)

  private def updateCacheMap[A](key: String, value: A, originalCacheMap: CacheMap)(implicit wts: Writes[A]): Future[CacheMap] = {
    val newCacheMap = funcMap.get(key).fold(originalCacheMap copy (data = originalCacheMap.data + (key -> Json.toJson(value)))) { fn => fn(Json.toJson(value), originalCacheMap)}
    sessionRepository().upsert(newCacheMap).map {_ => newCacheMap}
  }

  def cache[A](key: String, value: A)(implicit wts: Writes[A], hc: HeaderCarrier) = {
    hc.sessionId match {
      case None => updateCacheMap(key, value, new CacheMap(hc.sessionId.toString, Map(key -> Json.toJson(value))))
      case Some(id) => {
        sessionRepository().get(id.toString).flatMap { optionalCacheMap =>
          optionalCacheMap.fold(updateCacheMap(key, value, new CacheMap(id.toString, Map()))) { cm =>
            updateCacheMap(key, value, cm)
          }
        }
      }
    }
  }

  def delete(key: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    hc.sessionId match {
      case None => Future(false)
      case Some(id) => {
        sessionRepository().get(id.toString).flatMap { optionalCacheMap =>
          optionalCacheMap.fold(Future(false)) { cm =>
            val newCacheMap: CacheMap = cm copy (data = cm.data - key)
            sessionRepository().upsert(newCacheMap)
          }
        }
      }
    }
  }

  def fetch()(implicit hc: HeaderCarrier): Future[Option[CacheMap]] = {
    hc.sessionId match {
      case None => Future.successful(None)
      case Some(id) => sessionRepository().get(id.toString)
    }
  }

  def fetchAndGetEntry[A](key: String)(implicit hc: HeaderCarrier, rds: Reads[A]): Future[Option[A]] = {
    val futureOptionCacheMap = fetch()
    futureOptionCacheMap.map {optionalCacheMap =>
      optionalCacheMap.flatMap { cm =>
        cm.getEntry(key)
      }
    }
  }

  def associateFunc(key: String, fn: (JsValue, CacheMap) => CacheMap) = {
    funcMap = funcMap + (key -> fn)
  }
}

