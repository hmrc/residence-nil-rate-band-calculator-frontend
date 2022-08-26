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

package uk.gov.hmrc.residencenilratebandcalculator.repositories

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.functional.FunctionalBuilder
import play.api.libs.json.Writes.StringWrites
import play.api.libs.json.{JodaReads, JodaWrites, JsValue, Json}
import play.api.Configuration
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.api.{Cursor, DefaultDB}
import reactivemongo.bson.{BSONDocument, _}
import reactivemongo.play.json.ImplicitBSONHandlers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC))

object DatedCacheMap extends JodaReads with JodaWrites {
  implicit val dateFormat = ReactiveMongoFormats.dateTimeFormats
  implicit val formats = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(config: Configuration, mongo: () => DefaultDB)(implicit executionContext: ExecutionContext)
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
        logger.debug(s"set [$indexName] with value $ttl -> result : $result")
        result
      }
    } recover {
      case e => logger.error("Failed to set TTL index", e)
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

  def resetLastUpdated(): Future[Int] = {

    val now = System.currentTimeMillis()

    val selector = BSONDocument()
    val modifier = BSONDocument("$set" -> BSONDocument("lastUpdated" -> BSONDateTime(now)))

    collection.update(selector, modifier, multi=true) map {
      result =>
        result.ok match {
          case false => -1
          case true => result.nModified
        }
    }
  }

  val actorSystem = ActorSystem()
  actorSystem.scheduler.scheduleOnce(FiniteDuration(10, TimeUnit.SECONDS)) {
    resetLastUpdated() map { n =>
      logger.info(s"Updated $n documents with a new lastUpdated timestamp")
    }
    actorSystem.terminate()
  }

}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongoComponent: ReactiveMongoComponent)(implicit ex: ExecutionContext) {

  lazy val sessionRepository = new ReactiveMongoRepository(
    config, mongoComponent.mongoConnector.db)

  def apply(): ReactiveMongoRepository = sessionRepository
}
