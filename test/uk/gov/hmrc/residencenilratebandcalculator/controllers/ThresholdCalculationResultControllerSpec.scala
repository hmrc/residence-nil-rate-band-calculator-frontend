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
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json._
import play.api.mvc.DefaultMessagesControllerComponents
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.models.{CacheMap, CalculationInput, CalculationResult}
import uk.gov.hmrc.residencenilratebandcalculator.utils.CurrencyFormatter
import uk.gov.hmrc.residencenilratebandcalculator.views.html.threshold_calculation_result

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success

class ThresholdCalculationResultControllerSpec extends NewSimpleControllerSpecBase with MockitoSugar with Matchers {

  val testJsNumber: JsNumber = JsNumber(10)

  val dateOfDeath: LocalDate = LocalDate.of(2020, 1, 1)
  val dateOfDeathString = "2020-01-01"
  val valueOfEstate = 1
  val chargeableEstateValue = 2

  val expectedResidenceNilRateAmount = 77796325
  val expectedApplicableNilRateBandAmount = 88881
  val expectedCarriedForwardAmount = 9999
  val expectedDefaultAllowance = 3333
  val adjustedAllowance = 7777
  val calculationResult: CalculationResult = CalculationResult(expectedResidenceNilRateAmount, expectedApplicableNilRateBandAmount,
    expectedCarriedForwardAmount, expectedDefaultAllowance, adjustedAllowance)

  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]

  val expectedCalculationInput: CalculationInput = CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None)

  val threshold_calculation_result: threshold_calculation_result = injector.instanceOf[threshold_calculation_result]

  val cacheMap: CacheMap = CacheMap("id", Map(
    Constants.dateOfDeathId -> JsString(dateOfDeathString),
    Constants.valueOfEstateId -> JsNumber(valueOfEstate),
    Constants.chargeableEstateValueId -> JsNumber(chargeableEstateValue),
    Constants.propertyInEstateId -> JsBoolean(false),
    Constants.transferAnyUnusedThresholdId -> JsBoolean(false),
    Constants.claimDownsizingThresholdId -> JsBoolean(false)
  ))

  def mockRnrbConnector: RnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.send(any[CalculationInput])(any[HeaderCarrier])) thenReturn Future.successful(Success(calculationResult))
    mockConnector
  }

  def thresholdCalculationResultController(rnrbConnector: RnrbConnector = mockRnrbConnector) =
    new ThresholdCalculationResultController(messagesControllerComponents, rnrbConnector, mockSessionConnector, mockValidatedSession, threshold_calculation_result)

  "Threshold Calculation Result Controller" must {

    "return 200 for a GET" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      contentAsString(result) should include("<title>You can claim residence nil rate band - Calculate the available RNRB - GOV.UK</title>")
    }

    "returns an Internal Server Error when the cache is in an unusable state" in {
      setCacheMap(CacheMap("id", Map()))
      assertThrows[IllegalArgumentException]{
        await(thresholdCalculationResultController().onPageLoad(fakeRequest))
      }
    }

    "send the input to the Microservice if the cache is in a valid state" in {
      setCacheMap(cacheMap)
      val connector = mockRnrbConnector
      val jsonNapper = ArgumentCaptor.forClass(classOf[CalculationInput])
      await(thresholdCalculationResultController(connector).onPageLoad(fakeRequest))
      verify(connector).send(jsonNapper.capture)(any[HeaderCarrier])
      jsonNapper.getValue shouldBe expectedCalculationInput
    }

    "display the calculation result if the Microservice successfully returns it" in {
      setCacheMap(cacheMap)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      val contents = contentAsString(result)
      contents should include(CurrencyFormatter.format(expectedResidenceNilRateAmount))
    }

    "redirect to the SessionExpiredController if no CacheMap can be found" in {
      when(mockSessionConnector.fetch()(any[HeaderCarrier])) thenReturn Future.successful(None)
      val result = thresholdCalculationResultController().onPageLoad(fakeRequest)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SessionExpiredController.onPageLoad.url)
    }
  }
}
