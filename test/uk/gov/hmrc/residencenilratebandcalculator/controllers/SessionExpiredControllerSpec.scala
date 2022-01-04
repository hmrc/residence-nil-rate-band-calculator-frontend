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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.session_expired

class SessionExpiredControllerSpec extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication {

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val injector: Injector = fakeApplication.injector
  implicit lazy val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  def messages: Messages = messagesApi.preferred(request)
  val fakeRequest = FakeRequest("", "")

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val session_expired = injector.instanceOf[session_expired]
  "Session Expired controller" must {

    "return 200 for a GET" in {
      val result = new SessionExpiredController(messagesControllerComponents, mockSessionConnector, session_expired).onPageLoad()(fakeRequest)
      status(result) shouldBe 200
    }

    "return the View for a GET" in {
      val result = new SessionExpiredController(messagesControllerComponents, mockSessionConnector, session_expired).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe session_expired()(fakeRequest, messages).toString
    }
  }
}
