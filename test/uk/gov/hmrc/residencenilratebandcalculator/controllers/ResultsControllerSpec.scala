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

import java.text.NumberFormat

import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.libs.json.{JsNumber, JsValue}
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.json.JsonBuilder
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationResult, DisplayResults}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.results

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ResultsControllerSpec extends SimpleControllerSpecBase with MockitoSugar with Matchers {

  val testJsNumber = JsNumber(10)

  val mockLeftJsonBuilder: JsonBuilder = mock[JsonBuilder]
  when(mockLeftJsonBuilder.buildFromCacheMap(any[CacheMap])) thenReturn Future.successful(Failure(new NoCacheMapException("Something bad happened")))

  val mockJsonBuilderThatSucceeds: JsonBuilder = mock[JsonBuilder]
  when(mockJsonBuilderThatSucceeds.buildFromCacheMap(any[CacheMap])) thenReturn Future.successful(Success(testJsNumber))

  val expectedResidenceNilRateAmount = 77796325
  val expectedApplicableNilRateBandAmount = 88881
  val expectedCarriedForwardAmount = 9999
  val expectedDefaultAllowance = 3333
  val adjustedAllowance = 7777
  val calculationResult = CalculationResult(expectedResidenceNilRateAmount, expectedApplicableNilRateBandAmount,
    expectedCarriedForwardAmount, expectedDefaultAllowance, adjustedAllowance)

  def mockRnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.send(any[JsValue])) thenReturn Future.successful(Success(calculationResult))
    mockConnector
  }

  def resultsController(jsonBuilder: JsonBuilder, rnrbConnector: RnrbConnector = mockRnrbConnector) =
    new ResultsController(frontendAppConfig, messagesApi, rnrbConnector, mockSessionConnector, jsonBuilder)

  "ResultsController" must {

    "return 200 for a GET" in {
      val result = resultsController(mockJsonBuilderThatSucceeds).onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val result = resultsController(mockJsonBuilderThatSucceeds).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe results(frontendAppConfig, DisplayResults(calculationResult, Nil)(messages))(fakeRequest, messages).toString
    }

    "returns an Internal Server Error when the JsonBuilder fails" in {
      val result = resultsController(mockLeftJsonBuilder).onPageLoad()(fakeRequest)
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "send Json to the Microservice if the JsonBuilder succeeds" in {
      val connector = mockRnrbConnector
      val jsonNapper = ArgumentCaptor.forClass(classOf[JsValue])
      await(resultsController(mockJsonBuilderThatSucceeds, connector).onPageLoad()(fakeRequest))
      verify(connector).send(jsonNapper.capture)
      jsonNapper.getValue shouldBe testJsNumber
    }

    "display the calculation result if the Microservice successfully returns it" in {
      val result = resultsController(mockJsonBuilderThatSucceeds).onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedResidenceNilRateAmount))
    }

    "display the carry forward amount if the Microservice successfully returns it" in {
      val result = resultsController(mockJsonBuilderThatSucceeds).onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedCarriedForwardAmount))
    }

    "display the applicable nil rate band if the Microservice successfully returns it" in {
      val result = resultsController(mockJsonBuilderThatSucceeds).onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedApplicableNilRateBandAmount))
    }

    "display the default allowance if the Microservice successfully returns it" in {
      val result = resultsController(mockJsonBuilderThatSucceeds).onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedDefaultAllowance))
    }
  }
}
