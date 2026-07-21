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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import play.api.http.Status
import play.api.libs.json.*
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.helpers.RnrbControllerSpec
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.models.{CacheMap, CalculationInput, CalculationResult}
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter
import uk.gov.hmrc.residencenilratebandcalculator.views.html.threshold_calculation_result

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success

class ThresholdCalculationResultControllerSpec extends RnrbControllerSpec {

  val testJsNumber: JsNumber = JsNumber(10)

  val dateOfDeath: LocalDate = LocalDate.of(2020, 1, 1)
  val dateOfDeathString      = "2020-01-01"
  val valueOfEstate          = 1
  val chargeableEstateValue  = 2

  val expectedResidenceNilRateAmount      = 77796325
  val expectedApplicableNilRateBandAmount = 88881
  val expectedCarriedForwardAmount        = 9999
  val expectedDefaultAllowance            = 3333
  val adjustedAllowance                   = 7777

  val calculationResult: CalculationResult = CalculationResult(
    expectedResidenceNilRateAmount,
    expectedApplicableNilRateBandAmount,
    expectedCarriedForwardAmount,
    expectedDefaultAllowance,
    adjustedAllowance
  )

  val mockValidatedSession: ValidatedSession = inject[ValidatedSession]

  val expectedCalculationInput: CalculationInput =
    CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None)

  val threshold_calculation_result: threshold_calculation_result = inject[threshold_calculation_result]

  val cacheMap: CacheMap = CacheMap(
    "id",
    Map(
      Constants.dateOfDeathId                -> JsString(dateOfDeathString),
      Constants.valueOfEstateId              -> JsNumber(valueOfEstate),
      Constants.chargeableEstateValueId      -> JsNumber(chargeableEstateValue),
      Constants.propertyInEstateId           -> JsBoolean(false),
      Constants.transferAnyUnusedThresholdId -> JsBoolean(false),
      Constants.claimDownsizingThresholdId   -> JsBoolean(false)
    )
  )

  def mockRnrbConnector: RnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.send(any[CalculationInput])(any[HeaderCarrier]))
      .thenReturn(Future.successful(Success(calculationResult)))
    mockConnector
  }

  def thresholdCalculationResultController(rnrbConnector: RnrbConnector = mockRnrbConnector) =
    new ThresholdCalculationResultController(
      messagesControllerComponents,
      rnrbConnector,
      mockSessionConnector,
      mockValidatedSession,
      threshold_calculation_result
    )

  "Threshold Calculation Result Controller" must {

    "return 200 for a GET" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the View for a GET" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      contentAsString(result) must include(
        "<title>You can claim residence nil rate band - Calculate the available RNRB - GOV.UK</title>"
      )
    }

    "return 400 Bad Request when 'Transfer Any Unused Threshold' is missing from cache" in {
      val incompleteCacheMap: CacheMap = cacheMap.copy(data = cacheMap.data - Constants.transferAnyUnusedThresholdId)
      setCacheMap(incompleteCacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("requirement failed: Transfer Any Unused Allowance was not answered")
    }

    "return 400 Bad Request when 'Date Of Death' is missing from cache" in {
      val incompleteCacheMap: CacheMap = cacheMap.copy(data = cacheMap.data - Constants.dateOfDeathId)
      setCacheMap(incompleteCacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("requirement failed: Date of Death was not answered")
    }

    "return 400 Bad Request when 'Value Of Estate' is missing from cache" in {
      val incompleteCacheMap: CacheMap = cacheMap.copy(data = cacheMap.data - Constants.valueOfEstateId)
      setCacheMap(incompleteCacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("requirement failed: Value Of Estate was not answered")
    }

    "return 400 Bad Request when 'Chargeable Estate Value' is missing from cache" in {
      val incompleteCacheMap: CacheMap = cacheMap.copy(data = cacheMap.data - Constants.chargeableEstateValueId)
      setCacheMap(incompleteCacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("requirement failed: Chargeable Estate Value was not answered")
    }

    "return 400 Bad Request when 'Claim Downsizing Threshold' is missing from cache" in {
      val incompleteCacheMap: CacheMap = cacheMap.copy(data = cacheMap.data - Constants.claimDownsizingThresholdId)
      setCacheMap(incompleteCacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("requirement failed: Claim Downsizing Threshold was not answered")
    }
    "return 400 Bad Request when 'Property In Estate' is missing from cache" in {
      val incompleteCacheMap: CacheMap = cacheMap.copy(data = cacheMap.data - Constants.propertyInEstateId)
      setCacheMap(incompleteCacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("requirement failed: Property In Estate was not answered")
    }

    "send the input to the Microservice if the cache is in a valid state" in {
      setCacheMap(cacheMap)
      val connector  = mockRnrbConnector
      val jsonNapper = ArgumentCaptor.forClass(classOf[CalculationInput])
      await(thresholdCalculationResultController(connector).onPageLoad(fakeRequest))
      verify(connector).send(jsonNapper.capture)(any[HeaderCarrier])
      jsonNapper.getValue mustBe expectedCalculationInput
    }

    "display the calculation result if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result   = thresholdCalculationResultController().onPageLoad(fakeRequest)
      val contents = contentAsString(result)
      contents must include(CurrencyFormatter.format(expectedResidenceNilRateAmount))
    }

    "redirect to the SessionExpiredController if no CacheMap can be found" in {
      when(mockSessionConnector.fetch()(any[HeaderCarrier])).thenReturn(Future.successful(None))
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }

}
