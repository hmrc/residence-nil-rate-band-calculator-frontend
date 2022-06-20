/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.mvc.DefaultMessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRows
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoThresholdIncreaseReason.{DateOfDeath, DirectDescendant}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.no_threshold_increase
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

class NoThresholdIncreaseControllerSpec extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication{

  val fakeRequest = FakeRequest("", "")

  val injector = fakeApplication.injector

  val mockConfig = injector.instanceOf[FrontendAppConfig]

  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]

  def messagesApi = injector.instanceOf[MessagesApi]
  def messages = messagesApi.preferred(fakeRequest)

  val no_threshold_increase = fakeApplication.injector.instanceOf[no_threshold_increase]

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

  "No Threshold Increase controller" must {
    "return 200 for a GET" in {
      val result = new NoThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, no_threshold_increase).onPageLoad(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return the No Threshold Increase view for a GET" in {
      val result = new NoThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, no_threshold_increase).onPageLoad(fakeRequest)
      contentAsString(result) shouldBe
        no_threshold_increase("no_threshold_increase.direct_descendant")(fakeRequest, messages).toString
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is DateOfDeath" in {
      val controller = new NoThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, no_threshold_increase)
      val controllerId = controller.getControllerId(DateOfDeath)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List()
      answerList shouldBe calculatedList
    }

    "The answer constants should be the same as the calulated constants for the controller when the reason is DirectDescendant" in {
      val controller = new NoThresholdIncreaseController(messagesControllerComponents, mockSessionConnector, no_threshold_increase)
      val controllerId = controller.getControllerId(DirectDescendant)
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList = AnswerRows.rowOrderList filter (calculatedConstants contains _)
      val answerList = List(Constants.dateOfDeathId)
      answerList shouldBe calculatedList
    }
  }
}
