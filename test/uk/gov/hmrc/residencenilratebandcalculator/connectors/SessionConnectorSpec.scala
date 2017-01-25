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

import org.mockito.ArgumentCaptor
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.{verify, when}
import org.mockito.Matchers._
import play.api.libs.json.{JsNumber, JsValue, Json, Writes}
import play.api.test.FakeRequest
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.repositories.{ReactiveMongoRepository, SessionRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//class SessionConnectorSpec extends UnitSpec with WithFakeApplication with MockitoSugar {
//  val sessionId = "an-id"
//  val exampleCacheMap = CacheMap(sessionId, Map[String, JsValue]("a-number" -> JsNumber(42)))
//
//
//  def getMockSessionRepository() = {
//    val mockSessionRepository = mock[SessionRepository]
//    mockSessionRepository
//  }
//
//  "SessionConnector" must {
//    "When fetching the session details where a sessionId is known the SessionConnector must return a cachemap and the session repository should be called correctly" in {
//      val repository = getMockSessionRepository()
//
//      val connector = new SessionConnector(repository)
//      val result = await(connector.fetch()(any[HeaderCarrier]))
//      verify(repository()).get(sessionId)
//
//      result shouldBe(Some(exampleCacheMap))
//    }
//  }
//}
