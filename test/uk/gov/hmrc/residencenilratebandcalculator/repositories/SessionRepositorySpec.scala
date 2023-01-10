/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}

class SessionRepositorySpec extends CommonPlaySpec with DefaultPlayMongoRepositorySupport[DatedCacheMap] with WithCommonFakeApplication{

  lazy val repository =  new ReactiveMongoRepository(
    config = fakeApplication.injector.instanceOf[Configuration],
    mongo = mongoComponent
  )

  val cacheMap = CacheMap("id", Map("string" -> Json.toJson("testData")))

  "SessionRepository" should {
    "return None for a missing user session" when {
      "the repository is empty" in {
        await(repository.get(cacheMap.id)) shouldBe None
      }
    }

    "return true" when {
      "populating repository" in {
        await(repository.upsert(cacheMap)) shouldBe true
      }
      "deleting data from repository" in {
        await(repository.removeAll(cacheMap.id)) shouldBe true
      }
    }

    "return the users answers" when {
      "the repository is populated" in {
        await(repository.upsert(cacheMap)) shouldBe true
        await(repository.get(cacheMap.id)).nonEmpty shouldBe true
      }
    }

    "remove the user's answers" when {
      "the repository is empty" in {
        await(repository.removeAll(cacheMap.id)) shouldBe true
        await(repository.get(cacheMap.id)) shouldBe None
      }
      "they're in the repository" in {
        await(repository.upsert(cacheMap)) shouldBe true
        await(repository.get(cacheMap.id)).nonEmpty shouldBe true
        await(repository.removeAll(cacheMap.id)) shouldBe true
        await(repository.get(cacheMap.id)) shouldBe None
      }
    }

  }
}
