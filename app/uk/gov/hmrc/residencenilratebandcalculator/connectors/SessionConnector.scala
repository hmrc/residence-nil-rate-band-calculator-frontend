/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json._
import play.api.Logging
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.models.CascadeUpsert
import uk.gov.hmrc.residencenilratebandcalculator.repositories.SessionRepository
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class SessionConnector @Inject()(val sessionRepository: SessionRepository, val cascadeUpsert: CascadeUpsert)(implicit ec: ExecutionContext) extends Logging {

  def cache[A](key: String, value: A)(implicit wts: Writes[A], hc: HeaderCarrier) = {
    hc.sessionId match {
      case None =>
       val msg = "Unable to find session with id " + hc.sessionId + "while caching " + key + " = " + value
       logger.error(msg)
       throw new RuntimeException(msg)
      case Some(id) =>
        sessionRepository().get(id.toString).flatMap { optionalCacheMap =>
          val updatedCacheMap = cascadeUpsert(key, value, optionalCacheMap.getOrElse(new CacheMap(id.toString, Map())))
          sessionRepository().upsert(updatedCacheMap).map {_ => updatedCacheMap}
        }
    }
  }

  def delete(key: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    hc.sessionId match {
      case None => Future(false)
      case Some(id) =>
        sessionRepository().get(id.toString).flatMap { optionalCacheMap =>
          optionalCacheMap.fold(Future(false)) { cm =>
            val newCacheMap: CacheMap = cm copy (data = cm.data - key)
            sessionRepository().upsert(newCacheMap)
          }
        }
    }
  }

  def removeAll(implicit hc: HeaderCarrier): Future[Boolean] =
    hc.sessionId match {
      case None => Future(false)
      case Some(id) =>
        sessionRepository().removeAll(id.toString)
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
}
