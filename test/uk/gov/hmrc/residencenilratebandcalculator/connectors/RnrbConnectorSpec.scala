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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito.{verify, when}
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.libs.json._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.WSHttp
import uk.gov.hmrc.residencenilratebandcalculator.models.CalculationResult

import scala.concurrent.Future

class RnrbConnectorSpec extends UnitSpec with WithFakeApplication with MockitoSugar {
  "RNRB Connector" must {

    "call the Microservice with the given JSON" in {
      implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
      implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
      implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
      val urlNapper = ArgumentCaptor.forClass(classOf[String])
      val bodyNapper = ArgumentCaptor.forClass(classOf[JsValue])
      val headersNapper = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
      val httpMock = mock[WSHttp]
      when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
        any[HeaderCarrier])) thenReturn Future.successful(HttpResponse(Status.OK, Some(Json.parse("{}"))))
      val data = Json.parse("{}")

      await(new RnrbConnector(httpMock).send(data))

      verify(httpMock).POST(urlNapper.capture, bodyNapper.capture, headersNapper.capture)(jsonWritesNapper.capture,
        httpReadsNapper.capture, headerCarrierNapper.capture)
      urlNapper.getValue should endWith("residence-nil-rate-band-calculator/calculate")
      bodyNapper.getValue shouldBe data
      headersNapper.getValue shouldBe Seq(("Content-Type", "application/json"))
    }

    "return a case class representing the received JSON when the send method is successful" in {
      val residenceNilRateAmount = 100
      val carryForwardAmount = 100
      val calculationResult = CalculationResult(residenceNilRateAmount, carryForwardAmount)
      val httpMock = mock[WSHttp]
      when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
        any[HeaderCarrier])) thenReturn Future.successful(HttpResponse(Status.OK, Some(Json.toJson(calculationResult))))

      val result = await(new RnrbConnector(httpMock).send(JsObject(Map[String, JsValue]())))

      result.right.get shouldBe calculationResult
    }

    "return a string representing the error when send method fails" in {
      val errorResponse = "Something went wrong!"
      val httpMock = mock[WSHttp]
      when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
        any[HeaderCarrier])) thenReturn Future.successful(HttpResponse(Status.OK, Some(JsString(errorResponse))))

      val result = await(new RnrbConnector(httpMock).send(JsObject(Map[String, JsValue]())))

      result.left.get shouldBe s""""$errorResponse""""
    }
  }
}
