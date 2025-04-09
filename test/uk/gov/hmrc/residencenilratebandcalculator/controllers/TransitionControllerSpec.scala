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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{GetReason, Reason, UserAnswers}

import scala.concurrent.{ExecutionContext, Future}

class TransitionControllerSpec extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val injector: Injector = fakeApplication.injector

  def mockMessagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  val injectedMessagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  def messages: Messages = mockMessagesApi.preferred(fakeRequest)

  private[controllers] class TestTransitionController
      extends FrontendController(injectedMessagesControllerComponents)
      with TransitionController {
    val sessionConnector: SessionConnector = mockSessionConnector
    val getReason: GetReason = new GetReason { def apply(userAnswers: UserAnswers): Reason = new Reason {} }
    override implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
    def getControllerId(reason: Reason)        = ""

    def createView(reason: Reason, userAnswers: UserAnswers)(implicit request: Request[_]): HtmlFormat.Appendable =
      HtmlFormat.empty

  }

  def createController = new TestTransitionController()

  "Transition controller" must {
    "return 200 for a GET" in {
      val result = createController.onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the Unable To Calculate Threshold Increase view for a GET" in {
      val result = createController.onPageLoad(fakeRequest)
      contentAsString(result) mustBe ""
    }

    "redirect to the SessionExpiredController when no CacheMap can be found" in {
      when(mockSessionConnector.fetch()(any[HeaderCarrier])).thenReturn(Future.successful(None))
      val result = createController.onPageLoad(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

}
