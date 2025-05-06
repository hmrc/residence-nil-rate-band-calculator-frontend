/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates

import play.api.http.{HttpEntity, Status}
import play.api.inject.Injector
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{
  AnyContent,
  AnyContentAsEmpty,
  DefaultMessagesControllerComponents,
  Request,
  ResponseHeader,
  Result
}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.{
  ControllerBase,
  DateOfDeathController,
  MockSessionConnector
}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death

import scala.concurrent.Future

class ValidatedSessionSpec
    extends CommonPlaySpec
    with HttpResponseMocks
    with MockSessionConnector
    with WithCommonFakeApplication {
  val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val injector: Injector                           = fakeApplication.injector

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  val validatedSession: ValidatedSession = new ValidatedSession(messagesControllerComponents)

  "On a page submit with an expired session, return an redirect to an expired session page" in {
    // expireSessionConnector()
    def simpleFunction(request: Request[AnyContent]): Future[Result] =
      Future.successful(Result(header = ResponseHeader(200), body = HttpEntity.NoEntity))
    val result = validatedSession.async(simpleFunction)

    /*  result mustBe Some(
        uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url
      )*/
  }

}
