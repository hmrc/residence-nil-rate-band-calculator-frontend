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
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap

class SessionRepositorySpec
    extends CommonPlaySpec
    with DefaultPlayMongoRepositorySupport[DatedCacheMap]
    with WithCommonFakeApplication {

  val repository: SessionRepository = new SessionRepository(
    config = fakeApplication.injector.instanceOf[Configuration],
    mongoComponent = mongoComponent
  )

  val cacheMap: CacheMap = CacheMap("id", Map("string" -> Json.toJson("testData")))

  "SessionRepository" must {
    "return None for a missing user session" when {
      "the repository is empty" in {
        await(repository.get(cacheMap.id)) mustBe None
      }
    }

    "return true" when {
      "populating repository" in {
        await(repository.upsert(cacheMap)) mustBe true
      }
      "deleting data from repository" in {
        await(repository.removeAll(cacheMap.id)) mustBe true
      }
    }

    "return the users answers" when {
      "the repository is populated" in {
        await(repository.upsert(cacheMap)) mustBe true
        await(repository.get(cacheMap.id)).nonEmpty mustBe true
      }
    }

    "remove the user's answers" when {
      "the repository is empty" in {
        await(repository.removeAll(cacheMap.id)) mustBe true
        await(repository.get(cacheMap.id)) mustBe None
      }
      "they're in the repository" in {
        await(repository.upsert(cacheMap)) mustBe true
        await(repository.get(cacheMap.id)).nonEmpty mustBe true
        await(repository.removeAll(cacheMap.id)) mustBe true
        await(repository.get(cacheMap.id)) mustBe None
      }
    }

  }

}
