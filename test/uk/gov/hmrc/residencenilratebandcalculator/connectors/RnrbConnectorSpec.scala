/*
 * Copyright 2019 HM Revenue & Customs
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

import org.joda.time.LocalDate
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json._
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, WSHttp}
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.http.ws.{WSGet, WSPost}

class RnrbConnectorSpec extends BaseSpec with MockitoSugar with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    reset(httpMock)
    super.beforeEach()
  }

  val httpMock = mock[WSHttp]

  def getHttpMock(returnedData: JsValue) = {
    when(httpMock.POST(anyString, any[JsValue], any[Seq[(String, String)]])(any[Writes[Any]], any[HttpReads[Any]],
      any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(Status.OK, Some(returnedData)))
    when(httpMock.GET(anyString)(any[HttpReads[Any]], any[HeaderCarrier], any())) thenReturn Future.successful(HttpResponse(Status.OK, Some(returnedData)))
    httpMock
  }

  val minimalJson = JsObject(Map[String, JsValue]())

  val dateOfDeath = new LocalDate(2020, 1, 1)
  val valueOfEstate = 1
  val chargeableEstateValue = 2
  val calculationInput = CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None)

  "RNRB Connector" when {

    "provided with a Calculation Input" must {
      "call the Microservice with the given JSON" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(minimalJson)

        val connector = new RnrbConnector() {
          override lazy val http: WSHttp with WSGet with WSPost = httpMock
        }
        await(connector.send(calculationInput))

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue should endWith(s"${connector.baseSegment}calculate")
        bodyCaptor.getValue shouldBe Json.toJson(calculationInput)
        headersCaptor.getValue shouldBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val residenceNilRateAmount = 100
        val applicableNilRateBandAmount = 100
        val carryForwardAmount = 100
        val defaultAllowanceAmount = 100
        val adjustedAllowanceAmount = 100
        val calculationResult = CalculationResult(residenceNilRateAmount, applicableNilRateBandAmount, carryForwardAmount,
          defaultAllowanceAmount, adjustedAllowanceAmount)

        val result = await(new RnrbConnector {
          override lazy val http: WSHttp with WSGet with WSPost = httpMock
          getHttpMock(Json.toJson(calculationResult))
        }.send(calculationInput))

        result.get shouldBe calculationResult
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        val result = await(new RnrbConnector {
          override lazy val http: WSHttp with WSGet with WSPost = httpMock
          getHttpMock(errorResponse)
        }.send(calculationInput))

        result match {
          case Failure(exception) => {
            exception shouldBe a[JsonInvalidException]
            exception.getMessage() shouldBe List.fill(5)("JSON error: error.path.missing\n").mkString("")
          }
          case Success(_) => fail
        }
      }
    }

    "provided with JSON directly" must {

      "call the Microservice with the given JSON" in {
        implicit val headerCarrierNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
        implicit val httpReadsNapper = ArgumentCaptor.forClass(classOf[HttpReads[Any]])
        implicit val jsonWritesNapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
        val urlCaptor = ArgumentCaptor.forClass(classOf[String])
        val bodyCaptor = ArgumentCaptor.forClass(classOf[JsValue])
        val headersCaptor = ArgumentCaptor.forClass(classOf[Seq[(String, String)]])
        val httpMock = getHttpMock(minimalJson)

        val connector = new RnrbConnector() {
          override lazy val http: WSHttp with WSGet with WSPost = httpMock
        }
        await(connector.sendJson(minimalJson))

        verify(httpMock).POST(urlCaptor.capture, bodyCaptor.capture, headersCaptor.capture)(jsonWritesNapper.capture,
          httpReadsNapper.capture, headerCarrierNapper.capture, any())
        urlCaptor.getValue should endWith(s"${connector.baseSegment}calculate")
        bodyCaptor.getValue shouldBe minimalJson
        headersCaptor.getValue shouldBe Seq(connector.jsonContentTypeHeader)
      }

      "return a case class representing the received JSON when the send method is successful" in {
        val residenceNilRateAmount = 100
        val applicableNilRateBandAmount = 100
        val carryForwardAmount = 100
        val defaultAllowanceAmount = 100
        val adjustedAllowanceAmount = 100
        val calculationResult = CalculationResult(residenceNilRateAmount, applicableNilRateBandAmount, carryForwardAmount,
          defaultAllowanceAmount, adjustedAllowanceAmount)

        val result = await(new RnrbConnector {
          override lazy val http: WSHttp with WSGet with WSPost = httpMock
          getHttpMock(Json.toJson(calculationResult))
        }.sendJson(minimalJson))

        result.get shouldBe calculationResult
      }

      "return a string representing the error when send method fails" in {
        val errorResponse = JsString("Something went wrong!")

        val result = await(new RnrbConnector {
          override lazy val http: WSHttp with WSGet with WSPost = httpMock
          getHttpMock(errorResponse)
        }.sendJson(minimalJson))

        result match {
          case Failure(exception) => {
            exception shouldBe a[JsonInvalidException]
            exception.getMessage() shouldBe List.fill(5)("JSON error: error.path.missing\n").mkString("")
          }
          case Success(_) => fail
        }
      }
    }
  }
}