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

package uk.gov.hmrc.residencenilratebandcalculator.repositories

import javax.inject.{Inject, Singleton}

import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class SessionRepository(mongo: () => DefaultDB)
  extends ReactiveRepository[CacheMap, BSONObjectID]("residenceNilRateBand", mongo, CacheMap.formats) {

  def upsert(body: CacheMap): Future[Boolean] = {
    collection.insert(body).map { lastError =>
      lastError.ok
    }
  }

  def get(sessionId: String): Future[Option[CacheMap]] = {
    Try {
      BSONObjectID(sessionId)
    }.map { id: BSONObjectID =>
      findById(id)
    }.recover { case _: IllegalArgumentException => Future.successful(None) }.get
  }
}

@Singleton
class SessionRepositoryPrime @Inject()() {

  class DbConnection extends MongoDbConnection

  private lazy val sessionRepository = new SessionRepository(new DbConnection().db)

  def apply(): SessionRepository = sessionRepository
}


