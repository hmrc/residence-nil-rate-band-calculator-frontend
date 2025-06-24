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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}

import java.net.URL
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.writeableOf_JsValue
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RnrbConnectorSpec
    extends CommonPlaySpec
    with MockitoSugar
    with BeforeAndAfterEach
    with WithCommonFakeApplication {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(httpMock)
    reset(requestBuilderMock)
    super.beforeEach()
  }

  val httpMock: HttpClientV2   = mock[HttpClientV2]
  val configMock: FrontendAppConfig = mock[FrontendAppConfig]
  val requestBuilderMock = mock[RequestBuilder]

    def getHttpMock(returnedData: JsValue): HttpClientV2 = {
      val httpClientV2Mock = mock[HttpClientV2]
      val requestBuilderMock = mock[RequestBuilder]

      when(httpClientV2Mock.post(any[URL])(any[HeaderCarrier]))
        .thenReturn(requestBuilderMock)

      when(httpClientV2Mock.get(any[URL])(any[HeaderCarrier]))
        .thenReturn(requestBuilderMock)

      when(requestBuilderMock.setHeader(any[(String, String)]))
        .thenReturn(requestBuilderMock)

      when(requestBuilderMock.withBody(any[JsValue]))
        .thenReturn(requestBuilderMock)

      // Use doReturn to avoid Scala 3 using inference issues
      val stubResult = Future.successful(HttpResponse(Status.OK, returnedData.toString()))
      doAnswer(_ => stubResult)
        .when(requestBuilderMock)
        .execute[HttpResponse](using any(), any())


      httpClientV2Mock

  }

  val minimalJson: JsObject = JsObject(Map[String, JsValue]())

  val dateOfDeath: LocalDate = LocalDate.of(2020, 1, 1)
  val valueOfEstate          = 1
  val chargeableEstateValue  = 2

  val calculationInput: CalculationInput =
    CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None)

  "RNRB Connector" when {

    "provided with a Calculation Input" must {
      "call the Microservice with the given JSON" in {
        implicit val headerCarrierNapper: ArgumentCaptor[HeaderCarrier] =
          ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[?]]).asInstanceOf[ArgumentCaptor[HttpReads[HttpResponse]]]
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[JsValue]])
        val urlCaptor                 = ArgumentCaptor.forClass(classOf[URL])
        val bodyCaptor                = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor             = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock                  = getHttpMock(minimalJson)

        val connector = new RnrbConnector(httpMock, configMock)
        await(connector.send(calculationInput))

        verify(httpMock).post(urlCaptor.capture())(headerCarrierNapper.capture())
        verify(requestBuilderMock).setHeader(headersCaptor.capture()*)
        verify(requestBuilderMock).withBody(bodyCaptor.capture())
        verify(requestBuilderMock).execute[HttpResponse](
          using httpReadsNapper.capture(),
          any[ExecutionContext]
        )

        urlCaptor.getValue.toString must endWith(s"${connector.baseSegment}calculate")
        bodyCaptor.getValue mustBe Json.toJson(calculationInput)
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val residenceNilRateAmount      = 100
        val applicableNilRateBandAmount = 100
        val carryForwardAmount          = 100
        val defaultAllowanceAmount      = 100
        val adjustedAllowanceAmount     = 100
        val calculationResult = CalculationResult(
          residenceNilRateAmount,
          applicableNilRateBandAmount,
          carryForwardAmount,
          defaultAllowanceAmount,
          adjustedAllowanceAmount
        )

        val result = await(new RnrbConnector(httpMock, configMock) {
          getHttpMock(Json.toJson(calculationResult))
        }.send(calculationInput))

        result.get mustBe calculationResult
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        val result = await(new RnrbConnector(httpMock, configMock) {
          getHttpMock(errorResponse)
        }.send(calculationInput))

        result match {
          case Failure(exception) =>
            exception mustBe a[JsonInvalidException]
          case Success(_) => fail()
        }
      }
    }

    "provided with JSON directly" must {

      "call the Microservice with the given JSON" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[?]]).asInstanceOf[ArgumentCaptor[HttpReads[HttpResponse]]]
        implicit val jsonWritesNapper    = ArgumentCaptor.forClass(classOf[Writes[JsValue]])
        val urlCaptor                    = ArgumentCaptor.forClass(classOf[URL])
        val bodyCaptor                   = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor                = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock                     = getHttpMock(minimalJson)

        val connector = new RnrbConnector(httpMock, configMock)
        await(connector.sendJson(minimalJson))

        verify(httpMock).post(urlCaptor.capture())(headerCarrierNapper.capture())
        verify(requestBuilderMock).setHeader(headersCaptor.capture()*)
        verify(requestBuilderMock).withBody(bodyCaptor.capture())
        verify(requestBuilderMock).execute[HttpResponse](
          using httpReadsNapper.capture(),
          any[ExecutionContext]
        )

        urlCaptor.getValue.toString must endWith(s"${connector.baseSegment}calculate")
        bodyCaptor.getValue mustBe minimalJson
        headersCaptor.getValue mustBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val residenceNilRateAmount      = 100
        val applicableNilRateBandAmount = 100
        val carryForwardAmount          = 100
        val defaultAllowanceAmount      = 100
        val adjustedAllowanceAmount     = 100
        val calculationResult = CalculationResult(
          residenceNilRateAmount,
          applicableNilRateBandAmount,
          carryForwardAmount,
          defaultAllowanceAmount,
          adjustedAllowanceAmount
        )

        val result = await(new RnrbConnector(httpMock, configMock) {
          getHttpMock(Json.toJson(calculationResult))
        }.sendJson(minimalJson))

        result.get mustBe calculationResult
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        val result = await(new RnrbConnector(httpMock, configMock) {
          getHttpMock(errorResponse)
        }.sendJson(minimalJson))

        result match {
          case Failure(exception) =>
            exception mustBe a[JsonInvalidException]
          case Success(_) => fail()
        }
      }
    }
  }

}
