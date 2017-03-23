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

import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRows
import uk.gov.hmrc.residencenilratebandcalculator.models.GetCannotClaimDownsizingReason.{DateOfDisposalTooEarly, NoAssetsPassingToDirectDescendants}
import uk.gov.hmrc.residencenilratebandcalculator.models.GetCannotClaimRNRBReason.NotCloselyInherited
import uk.gov.hmrc.residencenilratebandcalculator.views.html.cannot_claim_downsizing
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

class CannotClaimDownsizingControllerSpec extends UnitSpec with WithFakeApplication with HttpResponseMocks with MockSessionConnector with MockitoSugar {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val navigator = injector.instanceOf[Navigator]

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  val filledOutCacheMap = new CacheMap("",
    Map[String, JsValue](
      Constants.dateOfDeathId -> JsString("2019-03-04"),
      Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
      Constants.valueOfEstateId -> JsNumber(500000),
      Constants.chargeableEstateValueId -> JsNumber(450000),
      Constants.estateHasPropertyId -> JsBoolean(true),
      Constants.propertyValueId -> JsNumber(400000),
      Constants.doesGrossingUpApplyToOtherPropertyId -> JsBoolean(true),
      Constants.anyPropertyCloselyInheritedId -> JsBoolean(true),
      Constants.percentageCloselyInheritedId -> JsNumber(100),
      Constants.anyBroughtForwardAllowanceId -> JsBoolean(true),
      Constants.broughtForwardAllowanceId -> JsNumber(50000),
      Constants.anyDownsizingAllowanceId -> JsBoolean(true),
      Constants.dateOfDisposalId -> JsString("2018-03-02"),
      Constants.valueOfDisposedPropertyId -> JsNumber(100000),
      Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
      Constants.doesGrossingUpApplyToOtherPropertyId -> JsBoolean(true),
      Constants.chargeableValueOfResidenceId -> JsNumber(50000),
      Constants.assetsPassingToDirectDescendantsId -> JsNumber(1000),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true),
      Constants.broughtForwardAllowanceOnDisposalId -> JsNumber(1000)
    ))

  "Cannot Claim Downsizing Controller" must {
    "return 200 for a GET" in {
      val controller = new CannotClaimDownsizingController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

      val result = controller.onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val controller = new CannotClaimDownsizingController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

      val result = controller.onPageLoad(fakeRequest)
      contentAsString(result) shouldBe
        cannot_claim_downsizing(frontendAppConfig, "cannot_claim_downsizing.date_of_disposal_too_early_reason",
          routes.ResultsController.onPageLoad, Seq())(fakeRequest, messages).toString
    }

    "throw an exception when the cache is unavailable" in {
      val mockSessionConnector = mock[SessionConnector]
      val controller = new CannotClaimDownsizingController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

      an[RuntimeException] should be thrownBy controller.onPageLoad(fakeRequest)
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is NotCloselyInherited" in {
      val controller = new CannotClaimRNRBController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)
      val controllerId = controller.getControllerId(NoAssetsPassingToDirectDescendants)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.valueOfEstateId,
        Constants.chargeableEstateValueId)
      answerList shouldBe (calculatedList)
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is another reason" in {
      val controller = new CannotClaimRNRBController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)
      val controllerId = controller.getControllerId(DateOfDisposalTooEarly)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.valueOfEstateId,
        Constants.chargeableEstateValueId)
      answerList shouldBe (calculatedList)
    }
  }
}
