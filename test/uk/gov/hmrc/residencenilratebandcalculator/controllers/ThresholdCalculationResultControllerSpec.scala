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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.joda.time.LocalDate
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.Matchers
import play.api.http.Status
import play.api.libs.json._
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter

import scala.concurrent.Future
import scala.util.Success
import uk.gov.hmrc.http.HeaderCarrier

class ThresholdCalculationResultControllerSpec extends SimpleControllerSpecBase with MockitoSugar with Matchers {

  val testJsNumber = JsNumber(10)

  val dateOfDeath = new LocalDate(2020, 1, 1)
  val dateOfDeathString = "2020-01-01"
  val valueOfEstate = 1
  val chargeableEstateValue = 2

  val expectedResidenceNilRateAmount = 77796325
  val expectedApplicableNilRateBandAmount = 88881
  val expectedCarriedForwardAmount = 9999
  val expectedDefaultAllowance = 3333
  val adjustedAllowance = 7777
  val calculationResult = CalculationResult(expectedResidenceNilRateAmount, expectedApplicableNilRateBandAmount,
    expectedCarriedForwardAmount, expectedDefaultAllowance, adjustedAllowance)

  val expectedCalculationInput = CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None)

  val cacheMap = CacheMap("id", Map(
    Constants.dateOfDeathId -> JsString(dateOfDeathString),
    Constants.valueOfEstateId -> JsNumber(valueOfEstate),
    Constants.chargeableEstateValueId -> JsNumber(chargeableEstateValue),
    Constants.propertyInEstateId -> JsBoolean(false),
    Constants.transferAnyUnusedThresholdId -> JsBoolean(false),
    Constants.claimDownsizingThresholdId -> JsBoolean(false)
  ))

  def mockRnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.send(any[CalculationInput])) thenReturn Future.successful(Success(calculationResult))
    mockConnector
  }

  def thresholdCalculationResultController(rnrbConnector: RnrbConnector = mockRnrbConnector) =
    new ThresholdCalculationResultController(messagesApi, rnrbConnector, mockSessionConnector, applicationProvider)

  "Threshold Calculation Result Controller" must {

    "return 200 for a GET" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad()(fakeRequest)
      contentAsString(result) should include("<title>Final calculation</title>")
    }

    "returns an Internal Server Error when the cache is in an unusable state" in {
      setCacheMap(CacheMap("id", Map()))
      val result = thresholdCalculationResultController().onPageLoad()(fakeRequest)
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "send the input to the Microservice if the cache is in a valid state" in {
      setCacheMap(cacheMap)
      val connector = mockRnrbConnector
      val jsonNapper = ArgumentCaptor.forClass(classOf[CalculationInput])
      await(thresholdCalculationResultController(connector).onPageLoad()(fakeRequest))
      verify(connector).send(jsonNapper.capture)
      jsonNapper.getValue shouldBe expectedCalculationInput
    }

    "display the calculation result if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad()(fakeRequest)
      val contents = contentAsString(result)
      contents should include(CurrencyFormatter.format(expectedResidenceNilRateAmount))
    }

    "redirect to the SessionExpiredController if no CacheMap can be found" in {
      when(mockSessionConnector.fetch()(any[HeaderCarrier])) thenReturn Future.successful(None)
      val result = thresholdCalculationResultController().onPageLoad()(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
