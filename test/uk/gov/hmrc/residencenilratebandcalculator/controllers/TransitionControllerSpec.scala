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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, FrontendAppConfig}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRow, GetReason, Reason, UserAnswers}

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

class TransitionControllerSpec extends BaseSpec with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  def mockMessagesApi = injector.instanceOf[MessagesApi]

  def messages = mockMessagesApi.preferred(fakeRequest)

  def createController = new TransitionController {
    val messagesApi: MessagesApi = mockMessagesApi
    val sessionConnector: SessionConnector = mockSessionConnector
    val getReason: GetReason = new GetReason { def apply(userAnswers: UserAnswers) = new Reason{} }

    def getControllerId(reason: Reason) = ""

    def createView(reason: Reason, userAnswers: UserAnswers, previousAnswers: Seq[AnswerRow])(implicit request: Request[_]): HtmlFormat.Appendable =
      HtmlFormat.empty
  }

  "Transition controller" must {
    "return 200 for a GET" in {
      val result = createController.onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the Unable To Calculate Threshold Increase view for a GET" in {
      val result = createController.onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe ""
    }

    "redirect to the SessionExpiredController when no CacheMap can be found" in {
      when(mockSessionConnector.fetch()(any[HeaderCarrier])) thenReturn Future.successful(None)
      val result = createController.onPageLoad(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
