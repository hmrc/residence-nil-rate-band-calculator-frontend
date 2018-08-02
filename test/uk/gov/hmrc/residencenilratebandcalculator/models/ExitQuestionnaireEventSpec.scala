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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class ExitQuestionnaireEventSpec extends BaseSpec with WithFakeApplication with MockitoSugar {

  val serviceDifficulty = "a"
  val serviceFeel = "b"
  val comments = "c"
  val fullName = "d"
  val email = "e"
  val phoneNumber = "f"

  "Exit Questionnaire Event" must {
    "have the correct audit source" in {
      val event = new ExitQuestionnaireEvent(serviceDifficulty, serviceFeel, comments, fullName, email, phoneNumber)(new HeaderCarrier)
      event.auditSource shouldBe "residence-nil-rate-band-calculator-frontend"
    }

    "have the correct audit type" in {
      val event = new ExitQuestionnaireEvent(serviceDifficulty, serviceFeel, comments, fullName, email, phoneNumber)(new HeaderCarrier)
      event.auditType shouldBe "RNRB-Exit Questionnaire"
    }

    "have the correct detail" in {
      val event = new ExitQuestionnaireEvent(serviceDifficulty, serviceFeel, comments, fullName, email, phoneNumber)(new HeaderCarrier)
      event.detail.size shouldBe 6
      event.detail.exists(x => x._1 == "serviceDifficulty" && x._2 == serviceDifficulty) shouldBe true
      event.detail.exists(x => x._1 == "serviceFeel" && x._2 == serviceFeel) shouldBe true
      event.detail.exists(x => x._1 == "comments" && x._2 == comments) shouldBe true
      event.detail.exists(x => x._1 == "fullName" && x._2 == fullName) shouldBe true
      event.detail.exists(x => x._1 == "email" && x._2 == email) shouldBe true
      event.detail.exists(x => x._1 == "phoneNumber" && x._2 == phoneNumber) shouldBe true
    }
  }
}
