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

import scala.util.parsing.json.JSON

//import uk.gov.hmrc.play.config.{AppName, ServicesConfig}
//import uk.gov.hmrc.play.http.HeaderCarrier
//import uk.gov.hmrc.residencenilratebandcalculator.WSHttp
//import uk.gov.hmrc.residencenilratebandcalculator.repositories.SessionRepository
//
//class SessionConnector @Inject()(wshttp: WSHttp) extends SessionCache with ServicesConfig with AppName {
//  override lazy val http: WSHttp = wshttp
//  override lazy val defaultSource: String = appName
//  override lazy val baseUri: String = baseUrl("session")
//  override lazy val domain: String =
//    getConfString("session.domain", throw new Exception(s"Could not find config 'session.domain'"))
//
//}

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

  def cache[A](key: String, value: A)(implicit wts: Writes[A], hc: HeaderCarrier) = {
    hc.sessionId match {
      case None => {
        val newcacheMap = new CacheMap(hc.sessionId.toString, Map(key -> Json.toJson(value)))
        sessionRepository().upsert(newcacheMap).map {_ => newcacheMap}

      }
      case Some(id) => {
        sessionRepository().get(id.toString).flatMap { optionalCacheMap =>
          optionalCacheMap.fold(Future(new CacheMap(id.toString, Map()))) { cm =>
            //val newCacheMap: CacheMap = cm copy (data = cm.data + (key -> Json.toJson(value)))
            val newCacheMap = funcMap.get(key).fold(cm copy (data = cm.data + (key -> Json.toJson(value)))) { fn => fn(cm)}
            sessionRepository().upsert(newCacheMap).map { _ => newCacheMap}
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
    sessionRepository().get(hc.sessionId.toString)
  }

  def fetchAndGetEntry[A](key: String)(implicit hc: HeaderCarrier, rds: Reads[A]): Future[Option[A]] = {
    val futureOptionCacheMap = fetch()
    futureOptionCacheMap.map { (optionalCacheMap: Option[CacheMap]) =>
      optionalCacheMap.flatMap { cm =>
        cm.getEntry(key)
      }
    }
  }

  def associateFunc(key: String, fn: CacheMap => CacheMap) = {
    funcMap = funcMap + (key -> fn)
  }
}

//@Singleton
//class AltSessionConnector @Inject()(val sessionRepository: SessionRepository){
//  var funcMap: Map[String, CacheMap => CacheMap] = Map()
//
//  def update[A](key: String, value: A)(implicit write: Writes[A], hc: HeaderCarrier): Future[Boolean] = {
//    hc.sessionId match {
//      case None => Future(false)
//      case Some(id) => {
//        sessionRepository().get(id.toString).flatMap { cmSeq =>
//          if (cmSeq.length != 1) {
//            Future(false)
//          } else {
//            val originalCacheMap = cmSeq.head
//            val newCacheMap = funcMap.get(key).fold(originalCacheMap copy (data = originalCacheMap.data + (key -> Json.toJson(value)))) { fn =>
//              fn(originalCacheMap)
//            }
//            sessionRepository().upsert(newCacheMap)
//          }
//        }
//      }
//    }
//  }
//
//  //Keys that do not exist are ignored
//  def delete(key: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
//    hc.sessionId match {
//      case None => Future(false)
//      case Some(id) => {
//        sessionRepository().get(id.toString).flatMap { cmSeq =>
//          if (cmSeq.length != 1) {
//            Future(false)
//          } else {
//            val originalCacheMap = cmSeq.head
//            if (originalCacheMap.data.isDefinedAt(key)) {
//              val newCacheMap = originalCacheMap copy (data = originalCacheMap.data - key)
//              sessionRepository().upsert(newCacheMap)
//            } else {
//              Future(true)
//            }
//          }
//        }
//      }
//    }
//  }
//
//  def associateFunc(key: String, fn: CacheMap => CacheMap) = {
//    funcMap = funcMap + (key -> fn)
//  }
//}

