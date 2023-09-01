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

import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import play.api.mvc.DefaultMessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoAdditionalThresholdAvailableReason.NotCloselyInherited
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoDownsizingThresholdIncreaseReason.{DatePropertyWasChangedTooEarly, NoAssetsPassingToDirectDescendants}
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.{no_additional_threshold_available, no_downsizing_threshold_increase}
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

class NoDownsizingThresholdIncreaseControllerSpec extends CommonPlaySpec with HttpResponseMocks with MockSessionConnector with MockitoSugar with WithCommonFakeApplication {

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val mockConfig = injector.instanceOf[FrontendAppConfig]

  val navigator = injector.instanceOf[Navigator]

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  val no_downsizing_threshold_increase = fakeApplication.injector.instanceOf[no_downsizing_threshold_increase]
  val no_additional_threshold_available = fakeApplication.injector.instanceOf[no_additional_threshold_available]

  val filledOutCacheMap = new CacheMap("",
    Map[String, JsValue](
      Constants.dateOfDeathId -> JsString("2019-03-04"),
      Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
      Constants.valueOfEstateId -> JsNumber(500000),
      Constants.chargeableEstateValueId -> JsNumber(450000),
      Constants.propertyInEstateId -> JsBoolean(true),
      Constants.propertyValueId -> JsNumber(400000),
      Constants.grossingUpOnEstateAssetsId -> JsBoolean(true),
      Constants.propertyPassingToDirectDescendantsId -> JsBoolean(true),
      Constants.percentagePassedToDirectDescendantsId -> JsNumber(100),
      Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
      Constants.valueBeingTransferredId -> JsNumber(50000),
      Constants.claimDownsizingThresholdId -> JsBoolean(true),
      Constants.datePropertyWasChangedId -> JsString("2018-03-02"),
      Constants.valueOfChangedPropertyId -> JsNumber(100000),
      Constants.assetsPassingToDirectDescendantsId -> JsBoolean(true),
      Constants.grossingUpOnEstateAssetsId -> JsBoolean(true),
      Constants.chargeablePropertyValueId -> JsNumber(50000),
      Constants.valueOfAssetsPassingId -> JsNumber(1000),
      Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true),
      Constants.valueAvailableWhenPropertyChangedId -> JsNumber(1000)
    ))

  "No Downsizing Threshold Increase Controller" must {
    "return 200 for a GET" in {
      val controller = new NoDownsizingThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, navigator, no_downsizing_threshold_increase)

      val result = controller.onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the View for a GET" in {
      val controller = new NoDownsizingThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, navigator, no_downsizing_threshold_increase)

      val result = controller.onPageLoad(fakeRequest)
      contentAsString(result) shouldBe
        no_downsizing_threshold_increase("no_downsizing_threshold_increase.date_property_was_changed_too_early_reason",
          routes.CheckYourAnswersController.onPageLoad)(fakeRequest, messages).toString
    }

    "return the view with the no assets key when that is the reason" in {
      val controller = new NoDownsizingThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, navigator, no_downsizing_threshold_increase)
      val userAnswers = new UserAnswers(filledOutCacheMap)

      val result = controller.createView(NoAssetsPassingToDirectDescendants, userAnswers)(fakeRequest)
      val target = no_downsizing_threshold_increase("no_downsizing_threshold_increase.no_assets_passing_to_direct_descendants_reason",
        navigator.nextPage(Constants.noDownsizingThresholdIncrease)(userAnswers))(fakeRequest, messages).toString()
      result.toString() shouldBe target

    }

    "throw an exception when the cache is unavailable" in {
      val mockSessionConnector = mock[SessionConnector]
      val controller = new NoDownsizingThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, navigator, no_downsizing_threshold_increase)

      an[RuntimeException] should be thrownBy controller.onPageLoad(fakeRequest)
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is NotCloselyInherited" in {
      val controller = new NoAdditionalThresholdAvailableController(messagesControllerComponents, mockSessionConnector, navigator, no_additional_threshold_available)
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
      val controller = new NoAdditionalThresholdAvailableController(messagesControllerComponents, mockSessionConnector, navigator, no_additional_threshold_available)
      val controllerId = controller.getControllerId(DatePropertyWasChangedTooEarly)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId,
        Constants.partOfEstatePassingToDirectDescendantsId,
        Constants.valueOfEstateId,
        Constants.chargeableEstateValueId)
      answerList shouldBe (calculatedList)
    }

    "getControllerId" should {

      "return assetsPassingToDirectDescendantsId constant when no assets is passed in as the reason" in {
        val controller = fakeApplication.injector.instanceOf[NoDownsizingThresholdIncreaseController]
        controller.getControllerId(NoAssetsPassingToDirectDescendants) shouldBe Constants.assetsPassingToDirectDescendantsId
      }

      "return datePropertyWasChangedId constant when assets are passed as the reason" in {
        val controller = fakeApplication.injector.instanceOf[NoDownsizingThresholdIncreaseController]
        controller.getControllerId(NotCloselyInherited) shouldBe Constants.datePropertyWasChangedId
      }
    }
  }
}
