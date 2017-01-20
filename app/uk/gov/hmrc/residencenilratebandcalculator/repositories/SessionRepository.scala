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

import play.api.libs.json.Json
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DefaultDB
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.{BSON, BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.Future

class SessionRepository(implicit mongo: () => DefaultDB)
  extends ReactiveRepository[CacheMap, BSONObjectID]("residenceNilRateBand", mongo, CacheMap.formats) {

  def upsert(cm: CacheMap): Future[Boolean] = {
    val selector = BSONDocument("id" -> cm.id)
    val cmDocument = Json.toJson(cm)
    val modifier = BSONDocument("$set" -> cmDocument)

    collection.update(selector, modifier, upsert = true).map { lastError =>
      lastError.ok
    }
  }

  def get(id: String): Future[Seq[CacheMap]] = {
    collection.find(Json.obj("id" -> id)).cursor[CacheMap]().collect[Seq]()
  }
}

object SessionRepositoryPrime extends MongoDbConnection {

  private lazy val sessionRepository = new SessionRepository

  def apply(): SessionRepository = sessionRepository
}


