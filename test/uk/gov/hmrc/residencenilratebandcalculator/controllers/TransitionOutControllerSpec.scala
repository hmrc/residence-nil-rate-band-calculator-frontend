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

import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRows
import uk.gov.hmrc.residencenilratebandcalculator.models.GetTransitionOutReason.{DateOfDeath, DirectDescendant, GrossingUpForResidence}
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.not_possible_to_use_service

class TransitionOutControllerSpec extends UnitSpec with WithFakeApplication with MockSessionConnector {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]
  def messagesApi = injector.instanceOf[MessagesApi]
  def messages = messagesApi.preferred(fakeRequest)

  val filledOutCacheMap = new CacheMap("",
    Map[String, JsValue](
      Constants.dateOfDeathId -> JsString("2019-03-04"),
      Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
      Constants.grossEstateValueId -> JsNumber(500000),
      Constants.chargeableTransferAmountId -> JsNumber(450000),
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

  "Transition controller" must {
    "return 200 for a GET" in {
      val result = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the Not Possible to use Calculator view for a GET" in {
      val result = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector).onPageLoad()(fakeRequest)
      contentAsString(result) shouldBe
        not_possible_to_use_service(frontendAppConfig, "not_possible_to_use_service.grossing_up", Seq())(fakeRequest, messages).toString
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is DateOfDeath" in {
      val controller = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector)
      val controllerId = controller.getControllerId(DateOfDeath)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List()
      answerList shouldBe (calculatedList)
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is DirectDescendant" in {
      val controller = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector)
      val controllerId = controller.getControllerId(DirectDescendant)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId)
      answerList shouldBe (calculatedList)
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is GrossingUpForResidence" in {
      val controller = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector)
      val controllerId = controller.getControllerId(GrossingUpForResidence)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.grossEstateValueId,
        Constants.chargeableTransferAmountId,
        Constants.estateHasPropertyId,
        Constants.propertyValueId,
        Constants.anyPropertyCloselyInheritedId,
        Constants.percentageCloselyInheritedId)
      answerList shouldBe (calculatedList)
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is GrossingUpForOtherProperty" in {
      val controller = new TransitionOutController(frontendAppConfig, messagesApi, mockSessionConnector)
      val controllerId = controller.getControllerId(GrossingUpForResidence)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.grossEstateValueId,
        Constants.chargeableTransferAmountId,
        Constants.estateHasPropertyId,
        Constants.propertyValueId,
        Constants.anyPropertyCloselyInheritedId,
        Constants.percentageCloselyInheritedId)
      answerList shouldBe (calculatedList)
    }
  }
}
