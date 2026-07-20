/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.controllers.helpers

import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.common.CommonPlaySpec
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}

trait ControllerSpec extends CommonPlaySpec with HttpResponseMocks with MockSessionConnector {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(SessionKeys.sessionId -> "id")

  val navigator: Navigator = inject[Navigator]

  def messagesApi: MessagesApi = inject[MessagesApi]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  val mockConfig: FrontendAppConfig = inject[FrontendAppConfig]

}
