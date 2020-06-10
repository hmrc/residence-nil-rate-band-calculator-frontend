/*
 * Copyright 2020 HM Revenue & Customs
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
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.functional.FunctionalBuilder
import play.api.libs.json.Writes.StringWrites
import play.api.libs.json.{JodaReads, JodaWrites, JsValue, Json}
import play.api.{Configuration, Logger}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.api.{Cursor, DefaultDB}
import reactivemongo.bson.{BSONDocument, _}
import reactivemongo.play.json.ImplicitBSONHandlers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC))

object DatedCacheMap extends JodaReads with JodaWrites {
  import play.api.libs.functional.syntax._
  import play.api.libs.json.OWrites._
  import play.api.libs.json.Reads._
  import play.api.libs.json._

  val datedCacheMapReads: Reads[DatedCacheMap] =
    ((JsPath \ "id").read[String] and
    (JsPath \ "data").read[Map[String, JsValue]] and
    (JsPath \ "lastUpdated").read[DateTime]) { (id, data, lastUpdated) =>
      new DatedCacheMap(id, data, lastUpdated)
    }
  val writeBuilder: FunctionalBuilder[OWrites]#CanBuild3[String, Map[String, JsValue], DateTime] =
    (JsPath \ "id").write[String] and
    (JsPath \ "data").write[Map[String, JsValue]] and
    (JsPath \ "lastUpdated").write[DateTime]

  val datedCacheMapWrites: OWrites[DatedCacheMap] =
    writeBuilder.apply { datedCacheMap: DatedCacheMap =>
      (datedCacheMap.id, datedCacheMap.data, datedCacheMap.lastUpdated)
    }

  implicit val formats: OFormat[DatedCacheMap] = OFormat(datedCacheMapReads, datedCacheMapWrites)

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(config: Configuration, mongo: () => DefaultDB)
  extends ReactiveRepository[DatedCacheMap, BSONObjectID](config.get[String]("appName"), mongo, DatedCacheMap.formats) {

  val fieldName = "lastUpdated"
  val createdIndexName = "userAnswersExpiry"
  val expireAfterSeconds = "expireAfterSeconds"
  val timeToLiveInSeconds: Int = config.get[Int]("mongodb.timeToLiveInSeconds")

  createIndex(fieldName, createdIndexName, timeToLiveInSeconds)

  private def createIndex(field: String, indexName: String, ttl: Int): Future[Boolean] = {
    collection.indexesManager.ensure(Index(Seq((field, IndexType.Ascending)), Some(indexName),
      options = BSONDocument(expireAfterSeconds -> ttl))) map {
      result => {
        Logger.debug(s"set [$indexName] with value $ttl -> result : $result")
        result
      }
    } recover {
      case e => Logger.error("Failed to set TTL index", e)
        false
    }
  }

  def upsert(cm: CacheMap): Future[Boolean] = {
    val selector = BSONDocument("id" -> cm.id)
    val cmDocument = Json.toJson(DatedCacheMap(cm))
    val modifier = BSONDocument("$set" -> cmDocument.as[BSONValue])

    collection.update(selector, modifier, upsert = true).map { lastError =>
      lastError.ok
    }
  }

  def removeAll(id: String): Future[Boolean] = {
    collection.remove(Json.obj("id" -> id)).map { lastError =>
      lastError.ok
    }
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(Json.obj("id" -> id)).cursor[CacheMap]().collect[Seq](Int.MaxValue, Cursor.FailOnError[Seq[CacheMap]]()).map { (cmSeq: Seq[CacheMap]) =>
      if (cmSeq.length != 1) {
        None
      } else {
        Some(cmSeq.head)
      }
    }
  }
}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongoComponent: ReactiveMongoComponent) {

  private lazy val sessionRepository = new ReactiveMongoRepository(
    config, mongoComponent.mongoConnector.db)

  def apply(): ReactiveMongoRepository = sessionRepository
}
