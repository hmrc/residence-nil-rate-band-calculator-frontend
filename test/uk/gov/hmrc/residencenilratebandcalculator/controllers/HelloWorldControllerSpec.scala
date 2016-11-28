/*
 * Copyright 2016 HM Revenue & Customs
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
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks


class HelloWorldControllerSpec extends ControllerSpecBase with MockitoSugar with HttpResponseMocks {

  val fakeRequest = FakeRequest("GET", "/")
  val response = mockResponse(200, "Some text")

  val mockConnector = mock[RnrbConnector]
  when(mockConnector.getHelloWorld) thenReturn response

  "GET /" should {
    "return 200" in {
      val result = new HelloWorld(frontendAppConfig, messagesApi, mockConnector).helloWorld()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = new HelloWorld(frontendAppConfig, messagesApi, mockConnector).helloWorld()(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "include text from the microservice in the page" in {
      val result = new HelloWorld(frontendAppConfig, messagesApi, mockConnector).helloWorld()(fakeRequest)
      contentAsString(result) should include("Some text")
    }
  }
}
