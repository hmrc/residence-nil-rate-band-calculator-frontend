/*
 * Copyright 2021 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{CollectionIndexesManager, Index}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.commands.{DefaultWriteResult, UpdateWriteResult, WriteConcern, WriteResult}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.{ExecutionContext, Future}

class SessionRepositorySpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  lazy val configuration: Configuration = fakeApplication.injector.instanceOf[Configuration]
  val mockCollection: JSONCollection = mock[JSONCollection]
  val mockConnection: DefaultDB = mock[DefaultDB]

  def setupIndexMocks(response: Boolean, errors: Boolean = false): OngoingStubbing[Future[Boolean]] = {
      val mockIndexesManager = mock[CollectionIndexesManager]

      when(mockCollection.indexesManager(ArgumentMatchers.any[ExecutionContext]))
      .thenReturn(mockIndexesManager)

      when(mockIndexesManager.ensure(ArgumentMatchers.any[Index]))
      .thenReturn(if (errors) throw new Exception("Error message") else response)
  }

  def setupUpsertMocks(result: Future[UpdateWriteResult]): OngoingStubbing[Future[UpdateWriteResult]] = {
    setupIndexMocks(response = true)

    when(mockCollection.update(ArgumentMatchers.any[BSONDocument], ArgumentMatchers.any[BSONDocument], ArgumentMatchers.any[WriteConcern],
      ArgumentMatchers.eq(true), ArgumentMatchers.any[Boolean])(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(result)
  }

  def setupRemoveMocks(result: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] = {
    setupIndexMocks(response = true)

    when(mockCollection.remove(ArgumentMatchers.any[JsObject], ArgumentMatchers.any[WriteConcern], ArgumentMatchers.any[Boolean])(ArgumentMatchers.any(), ArgumentMatchers.any()))
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
