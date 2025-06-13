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
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.views.html.unable_to_calculate_threshold_increase
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

class UnableToCalculateThresholdIncreaseControllerSpec
    extends CommonPlaySpec
    with MockSessionConnector
    with WithCommonFakeApplication {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val injector: Injector = fakeApplication.injector

  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  val unable_to_calculate_threshold_increase: unable_to_calculate_threshold_increase =
    injector.instanceOf[unable_to_calculate_threshold_increase]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  def messages: Messages       = messagesApi.preferred(fakeRequest)

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

  "Transition controller" must {
    "return 200 for a GET" in {
      val result = new UnableToCalculateThresholdIncreaseController(
        messagesControllerComponents,
        mockSessionConnector,
        unable_to_calculate_threshold_increase
      ).onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the Unable To Calculate Threshold Increase view for a GET" in {
      val result = new UnableToCalculateThresholdIncreaseController(
        messagesControllerComponents,
        mockSessionConnector,
        unable_to_calculate_threshold_increase
      ).onPageLoad(fakeRequest)
      contentAsString(result) mustBe
        unable_to_calculate_threshold_increase("unable_to_calculate_threshold_increase.grossing_up")(
          fakeRequest,
          messages
        ).toString
    }
  }

}
