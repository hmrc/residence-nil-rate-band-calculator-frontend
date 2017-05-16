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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.mockito.Mockito._
import org.mockito.{ArgumentCaptor, Matchers}
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAuditConnector}
import uk.gov.hmrc.residencenilratebandcalculator.forms.ExitQuestionnaireForm
import uk.gov.hmrc.residencenilratebandcalculator.models.{ExitQuestionnaire, ExitQuestionnaireEvent}
import uk.gov.hmrc.residencenilratebandcalculator.views.HtmlSpec
import uk.gov.hmrc.residencenilratebandcalculator.views.html.exit_questionnaire

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class ExitQuestionnaireControllerSpec extends HtmlSpec with WithFakeApplication with MockitoSugar with BeforeAndAfter {

  val fakeRequest = FakeRequest("", "")

  var mockAuditConnector = mock[FrontendAuditConnector]

  implicit val headnapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
  implicit val exenapper = ArgumentCaptor.forClass(classOf[ExecutionContext])

  before {
    mockAuditConnector = mock[FrontendAuditConnector]
  }

  "Exit Questionnaire controller" must {

    "return 200 for a GET" in {
      val result = new ExitQuestionnaireController(frontendAppConfig, messagesApi, mockAuditConnector, applicationProvider).onPageLoad()(fakeRequest)
      status(result) shouldBe 200
    }

    "return the View for a GET" in {
      val result = new ExitQuestionnaireController(frontendAppConfig, messagesApi, mockAuditConnector, applicationProvider).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe exit_questionnaire(frontendAppConfig)(fakeRequest, messages, applicationProvider).toString
    }

    "send an audit event on POST when given valid data" in {
      when(mockAuditConnector.sendEvent(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(Future.successful(AuditResult.Success))
      val exitQuestionnaire = ExitQuestionnaire(Some(ExitQuestionnaire.VERY_EASY),
        Some(ExitQuestionnaire.VERY_SATISFIED),
        Some("Comments"),
        Some("Full Name"),
        Some("email@email.com"),
        Some("0123456789"))
      val eventCaptor = ArgumentCaptor.forClass(classOf[ExitQuestionnaireEvent])
      val result = submit(exitQuestionnaire)
      verify(mockAuditConnector).sendEvent(eventCaptor.capture)(headnapper.capture, exenapper.capture)
    }

    "redirect to the thank you page on POST when given valid data" in {
      when(mockAuditConnector.sendEvent(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(Future.successful(AuditResult.Success))
      val exitQuestionnaire = ExitQuestionnaire(Some(ExitQuestionnaire.VERY_EASY),
                                                Some(ExitQuestionnaire.VERY_SATISFIED),
                                                Some("Comments"),
                                                Some("Full Name"),
                                                Some("email@email.com"),
                                                Some("0123456789"))
      val result = submit(exitQuestionnaire)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe Constants.callExitService.url
    }

    "redirect to the thank you page even when audit has failed" in {
      when(mockAuditConnector.sendEvent(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(Future.failed(new Exception()))
      val exitQuestionnaire = ExitQuestionnaire(Some(ExitQuestionnaire.VERY_EASY),
        Some(ExitQuestionnaire.VERY_SATISFIED),
        Some("Comments"),
        Some("Full Name"),
        Some("email@email.com"),
        Some("0123456789"))
      val result = submit(exitQuestionnaire)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get shouldBe Constants.callExitService.url
    }

    "return a bad request when given invalid data" in {
      val longString = Random.alphanumeric.take(ExitQuestionnaireForm.MAX_COMMENT_LENGTH + 1).mkString
      val exitQuestionnaire = ExitQuestionnaire(Some(ExitQuestionnaire.VERY_EASY),
        Some(ExitQuestionnaire.VERY_SATISFIED),
        Some(longString),
        Some("Full Name"),
        Some("email@email.com"),
        Some("0123456789"))
      val result = submit(exitQuestionnaire)

      status(result) shouldBe BAD_REQUEST
    }
  }

  private def submit(exitQuestionnaire: ExitQuestionnaire) = {
    val postData = Json.toJson(exitQuestionnaire)
    val request = fakeRequest.withJsonBody(postData)
    new ExitQuestionnaireController(frontendAppConfig, messagesApi, mockAuditConnector, applicationProvider).onSubmit()(request)
  }
}
