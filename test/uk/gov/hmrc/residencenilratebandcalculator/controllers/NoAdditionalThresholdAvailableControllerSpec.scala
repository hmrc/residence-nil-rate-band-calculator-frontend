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

import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models.{CacheMap, Reason, UserAnswers}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_additional_threshold_available
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

class NoAdditionalThresholdAvailableControllerSpec
    extends CommonPlaySpec
    with HttpResponseMocks
    with MockSessionConnector
    with WithCommonFakeApplication {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val injector: Injector = fakeApplication.injector

  val navigator: Navigator = injector.instanceOf[Navigator]

  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  val no_additional_threshold_available: no_additional_threshold_available =
    fakeApplication.injector.instanceOf[no_additional_threshold_available]

  val filledOutCacheMap = new CacheMap(
    "",
    Map[String, JsValue](
      Constants.dateOfDeathId                            -> JsString("2019-03-04"),
      Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
      Constants.valueOfEstateId                          -> JsNumber(500000),
      Constants.chargeableEstateValueId                  -> JsNumber(450000),
      Constants.propertyInEstateId                       -> JsBoolean(true),
      Constants.propertyValueId                          -> JsNumber(400000),
      Constants.grossingUpOnEstateAssetsId               -> JsBoolean(true),
      Constants.propertyPassingToDirectDescendantsId     -> JsBoolean(true),
      Constants.percentagePassedToDirectDescendantsId    -> JsNumber(100),
      Constants.transferAnyUnusedThresholdId             -> JsBoolean(true),
      Constants.valueBeingTransferredId                  -> JsNumber(50000),
      Constants.claimDownsizingThresholdId               -> JsBoolean(true),
      Constants.datePropertyWasChangedId                 -> JsString("2018-03-02"),
      Constants.valueOfChangedPropertyId                 -> JsNumber(100000),
      Constants.assetsPassingToDirectDescendantsId       -> JsBoolean(true),
      Constants.grossingUpOnEstateAssetsId               -> JsBoolean(true),
      Constants.chargeablePropertyValueId                -> JsNumber(50000),
      Constants.valueOfAssetsPassingId                   -> JsNumber(1000),
      Constants.transferAvailableWhenPropertyChangedId   -> JsBoolean(true),
      Constants.valueAvailableWhenPropertyChangedId      -> JsNumber(1000)
    )
  )

  "No Additional Threshold Available Controller" must {
    "return 200 for a GET" in {
      val controller = new NoAdditionalThresholdAvailableController(
        messagesControllerComponents,
        mockSessionConnector,
        navigator,
        no_additional_threshold_available
      )

      val result = controller.onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the  View for a GET" in {
      val controller = new NoAdditionalThresholdAvailableController(
        messagesControllerComponents,
        mockSessionConnector,
        navigator,
        no_additional_threshold_available
      )

      val result = controller.onPageLoad(fakeRequest)
      contentAsString(result) mustBe
        no_additional_threshold_available(
          "no_additional_threshold_available.not_closely_inherited_reason",
          routes.TransferAnyUnusedThresholdController.onPageLoad
        )(fakeRequest, messages).toString
    }

    "return the  Correct View for a GET" in {

      val userAnswers = mock[UserAnswers]
      val reason      = mock[Reason]

      val controller = new NoAdditionalThresholdAvailableController(
        messagesControllerComponents,
        mockSessionConnector,
        navigator,
        no_additional_threshold_available
      )

      val result = controller.createView(reason, userAnswers)(fakeRequest)
      contentAsString(result) mustBe
        no_additional_threshold_available(
          "",
          routes.TransferAnyUnusedThresholdController.onPageLoad
        )(fakeRequest, messages).toString
    }

    "throw an exception when the cache is unavailable" in {
      val mockSessionConnector = mock[SessionConnector]
      val controller = new NoAdditionalThresholdAvailableController(
        messagesControllerComponents,
        mockSessionConnector,
        navigator,
        no_additional_threshold_available
      )

      an[RuntimeException] must be thrownBy controller.onPageLoad(fakeRequest)
    }
  }

}
