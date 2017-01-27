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

import play.api.libs.json.Reads
import uk.gov.hmrc.http.cache.client.{CacheMap, SessionCache}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.repositories.SessionRepository
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class SessionConnector @Inject()(val sessionRepository: SessionRepository) {
  var funcMap: Map[String, CacheMap => CacheMap] = Map()

  private def updateCacheMap[A](key: String, value: A, originalCacheMap: CacheMap)(implicit wts: Writes[A]): Future[CacheMap] = {
    val newCacheMap = funcMap.get(key).fold(originalCacheMap copy (data = originalCacheMap.data + (key -> Json.toJson(value)))) { fn => fn(originalCacheMap)}
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

  def associateFunc(key: String, fn: CacheMap => CacheMap) = {
    funcMap = funcMap + (key -> fn)
  }
}

