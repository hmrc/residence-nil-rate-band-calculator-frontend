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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import play.api.http.Status
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents}
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession

class FeedbackSurveyControllerSpec extends MockSessionConnector with CommonPlaySpec with WithCommonFakeApplication{

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.sessionId -> "id")
  lazy val injector: Injector = fakeApplication.injector
  implicit lazy val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]
  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  "Feedback Survey controller" must {
    "return 303 for a GET" in {
      val testController = new FeedbackSurveyController(messagesControllerComponents, mockConfig, mockValidatedSession)
      val result = testController.redirectExitSurvey(request)
      status(result) shouldBe Status.SEE_OTHER
    }
  }
}