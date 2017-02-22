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

import org.joda.time.LocalDate
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.libs.json._
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}

import scala.concurrent.Future
import scala.util.Success

class ResultsControllerSpec extends SimpleControllerSpecBase with MockitoSugar with Matchers {

  val testJsNumber = JsNumber(10)

  val dateOfDeath = new LocalDate(2020, 1, 1)
  val dateOfDeathString = "2020-01-01"
  val grossEstateValue = 1
  val chargeableTransferValue = 2

  val expectedResidenceNilRateAmount = 77796325
  val expectedApplicableNilRateBandAmount = 88881
  val expectedCarriedForwardAmount = 9999
  val expectedDefaultAllowance = 3333
  val adjustedAllowance = 7777
  val calculationResult = CalculationResult(expectedResidenceNilRateAmount, expectedApplicableNilRateBandAmount,
    expectedCarriedForwardAmount, expectedDefaultAllowance, adjustedAllowance)

  val expectedCalculationInput = CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferValue, 0, 0, 0, None, None)

  val cacheMap = CacheMap("id", Map(
    Constants.dateOfDeathId -> JsString(dateOfDeathString),
    Constants.grossEstateValueId -> JsNumber(grossEstateValue),
    Constants.chargeableTransferAmountId -> JsNumber(chargeableTransferValue),
    Constants.estateHasPropertyId -> JsBoolean(false),
    Constants.anyBroughtForwardAllowanceId -> JsBoolean(false),
    Constants.anyDownsizingAllowanceId -> JsBoolean(false)
  ))

  def mockRnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.send(any[CalculationInput])) thenReturn Future.successful(Success(calculationResult))
    mockConnector
  }

  def resultsController(rnrbConnector: RnrbConnector = mockRnrbConnector) =
    new ResultsController(frontendAppConfig, messagesApi, rnrbConnector, mockSessionConnector)

  "ResultsController" must {

    "return 200 for a GET" in {
      setCacheMap(cacheMap)
      val result = resultsController().onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      setCacheMap(cacheMap)
      val result = resultsController().onPageLoad()(fakeRequest)
      contentAsString(result) should include("<title>Final calculation</title>")
    }

    "returns an Internal Server Error when the cache is in an unusable state" in {
      setCacheMap(CacheMap("id", Map()))
      val result = resultsController().onPageLoad()(fakeRequest)
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "send the input to the Microservice if the cache is in a valid state" in {
      setCacheMap(cacheMap)
      val connector = mockRnrbConnector
      val jsonNapper = ArgumentCaptor.forClass(classOf[CalculationInput])
      await(resultsController(connector).onPageLoad()(fakeRequest))
      verify(connector).send(jsonNapper.capture)
      jsonNapper.getValue shouldBe expectedCalculationInput
    }

    "display the calculation result if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result = resultsController().onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedResidenceNilRateAmount))
    }

    "display the carry forward amount if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result = resultsController().onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedCarriedForwardAmount))
    }

    "display the applicable nil rate band if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result = resultsController().onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedApplicableNilRateBandAmount))
    }

    "display the default allowance if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result = resultsController().onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(NumberFormat.getCurrencyInstance.format(expectedDefaultAllowance))
    }
  }
}
