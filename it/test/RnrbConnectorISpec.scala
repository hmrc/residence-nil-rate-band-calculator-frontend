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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.{Application, Configuration}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}
import uk.gov.hmrc.residencenilratebandcalculator.models.CalculationInput.formats
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector

import java.time.LocalDate
import java.net.URL
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class RnrbConnectorISpec extends AnyWordSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  val wireMockPort               = 11111
  val wireMockServer             = new WireMockServer(WireMockConfiguration.wireMockConfig().port(wireMockPort))
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeAll(): Unit = wireMockServer.start()

  override def afterAll(): Unit   = wireMockServer.stop()
  override def beforeEach(): Unit = wireMockServer.resetAll()

  val testConfig: Configuration = Configuration(
    "microservice.services.residence-nil-rate-band-calculator.port" -> wireMockPort
  )

  implicit val app: Application = new GuiceApplicationBuilder().configure(testConfig).build()
  val connector: RnrbConnector  = app.injector.instanceOf[RnrbConnector]

  val baseUrl = s"/residence-nil-rate-band-calculator"
  val sendUrl = s"$baseUrl/calculate"

  def await[T](f: Future[T]): T = Await.result(f, 5.seconds)

  val testInput = CalculationInput(
    dateOfDeath = LocalDate.of(2020, 1, 1),
    valueOfEstate = 1,
    chargeableEstateValue = 2,
    propertyValue = 0,
    percentagePassedToDirectDescendants = BigDecimal(100),
    valueBeingTransferred = 0,
    propertyValueAfterExemption = None,
    downsizingDetails = None
  )

  val expectedResult = CalculationResult(100, 200, 300, 400, 500)

  "RnrbConnector.send" should {

    "return a CalculationResult when microservice returns valid JSON" in {
      wireMockServer.stubFor(
        post(urlEqualTo(sendUrl))
          .withRequestBody(equalToJson(Json.toJson(testInput).toString()))
          .willReturn(okJson(Json.toJson(expectedResult).toString()))
      )
      val result = await(connector.send(testInput))
      result mustBe Success(expectedResult)
    }

    "fail with JsonInvalidException when microservice returns unexpected JSON" in {
      wireMockServer.stubFor(
        post(urlEqualTo(sendUrl))
          .withRequestBody(equalToJson(Json.toJson(testInput).toString()))
          .willReturn(ok("""{ "not": "expected" }"""))
      )

      val result = await(connector.send(testInput))
      result.isFailure mustBe true
      result.failed.get mustBe a[JsonInvalidException]

    }
  }

  "RnrbConnector.sendJson" should {
    val testJson = Json.obj("some" -> "json")

    "return a CalculationResult when valid JSON is returned" in {
      wireMockServer.stubFor(
        post(urlEqualTo(sendUrl))
          .withRequestBody(equalToJson(testJson.toString()))
          .willReturn(okJson(Json.toJson(expectedResult).toString()))
      )

      val result = await(connector.sendJson(testJson))
      result mustBe Success(expectedResult)
    }

    "fail with JsonInvalidException when invalid JSON is returned" in {
      wireMockServer.stubFor(
        post(urlEqualTo(sendUrl))
          .withRequestBody(equalToJson(testJson.toString()))
          .willReturn(ok("""{ "unexpected": "format" }"""))
      )

      val result = await(connector.sendJson(testJson))
      result.isFailure mustBe true
      result.failed.get mustBe a[JsonInvalidException]

    }

    "send correct JSON with content-type header" in {
      wireMockServer.stubFor(
        post(urlEqualTo(sendUrl))
          .withHeader("Content-Type", equalTo("application/json"))
          .withRequestBody(equalToJson(testJson.toString()))
          .willReturn(okJson(Json.toJson(expectedResult).toString()))
      )

      val result = await(connector.sendJson(testJson))
      result mustBe Success(expectedResult)

    }
  }

}
