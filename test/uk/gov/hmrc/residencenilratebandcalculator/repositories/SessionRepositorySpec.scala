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

package uk.gov.hmrc.residencenilratebandcalculator.repositories

import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.{Configuration, Play}
import reactivemongo.api.{CursorProducer, DefaultDB}
import reactivemongo.api.indexes.{CollectionIndexesManager, Index}
import reactivemongo.json.collection.JSONCollection
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import org.mockito.Mockito._
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.commands.{DefaultWriteResult, UpdateWriteResult, WriteConcern, WriteResult}
import reactivemongo.bson.BSONDocument
import reactivemongo.json.ImplicitBSONHandlers._
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  lazy val configuration: Configuration = fakeApplication.injector.instanceOf[Configuration]
  val mockCollection = mock[JSONCollection]
  val mockConnection = mock[DefaultDB]

  def setupIndexMocks(response: Boolean, errors: Boolean = false) = {
      val mockIndexesManager = mock[CollectionIndexesManager]

      when(mockCollection.indexesManager(Matchers.any[ExecutionContext]))
      .thenReturn(mockIndexesManager)

      when(mockIndexesManager.ensure(Matchers.any[Index]))
      .thenReturn(if (errors) throw new Exception("Error message") else response)
  }

  def setupUpsertMocks(result: Future[UpdateWriteResult]) = {
    setupIndexMocks(response = true)

    when(mockCollection.update(Matchers.any[BSONDocument], Matchers.any[BSONDocument], Matchers.any[WriteConcern],
      Matchers.eq(true), Matchers.any[Boolean])(Matchers.any(), Matchers.any(), Matchers.any()))
      .thenReturn(result)
  }

  def setupRemoveMocks(result: Future[WriteResult]) = {
    setupIndexMocks(response = true)

    when(mockCollection.remove(Matchers.any[JsObject], Matchers.any[WriteConcern], Matchers.any[Boolean])(Matchers.any(), Matchers.any()))
      .thenReturn(result)
  }

  def testRepository() = new ReactiveMongoRepository(configuration, () => mockConnection) {
    override lazy val collection: JSONCollection = mockCollection
  }

  "Calling upsert" should {

    "return a successful true if the write result was ok" in {
      val cacheMap = CacheMap("id", Map("string" -> Json.toJson("testData")))
      val response = UpdateWriteResult(ok = true, 1, 1, Seq(), Seq(), None, None, None)

      setupUpsertMocks(Future.successful(response))
      await(testRepository().upsert(cacheMap)) shouldBe true
    }

    "return a successful false if the write result has an issue" in {
      val cacheMap = CacheMap("id", Map("string" -> Json.toJson("testData")))
      val response = UpdateWriteResult(ok = false, 1, 1, Seq(), Seq(), None, None, None)

      setupUpsertMocks(Future.successful(response))
      await(testRepository().upsert(cacheMap)) shouldBe false
    }

    "return a failure if an exception occurs" in {
      val cacheMap = CacheMap("id", Map("string" -> Json.toJson("testData")))

      setupUpsertMocks(Future.failed(new Exception("test message")))
      the[Exception] thrownBy await(testRepository().upsert(cacheMap)) should have message "test message"
    }
  }

  "Calling removeAll" should {

    "return a successful true if the write result was ok" in {
      val response = new DefaultWriteResult(ok = true, 1, Seq(), None, None, None)

      setupRemoveMocks(Future.successful(response))
      await(testRepository().removeAll("id")) shouldBe true
    }

    "return a successful false if the write result has an issue" in {
      val response = new DefaultWriteResult(ok = false, 1, Seq(), None, None, None)

      setupRemoveMocks(Future.successful(response))
      await(testRepository().removeAll("id")) shouldBe false
    }

    "return a failure if an exception occurs" in {
      setupRemoveMocks(Future.failed(new Exception("test message")))
      the[Exception] thrownBy await(testRepository().removeAll("id")) should have message "test message"
    }
  }
}
