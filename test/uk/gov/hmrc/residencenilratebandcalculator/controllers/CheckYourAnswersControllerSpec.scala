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
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, CacheMap}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.check_your_answers
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

class CheckYourAnswersControllerSpec extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(SessionKeys.sessionId -> "id")
  val injector: Injector                               = fakeApplication.injector

  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  val messagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  val validatedSession: ValidatedSession     = injector.instanceOf[ValidatedSession]
  def messagesApi: MessagesApi               = injector.instanceOf[MessagesApi]
  def messages: Messages                     = messagesApi.preferred(fakeRequest)
  val check_your_answers: check_your_answers = fakeApplication.injector.instanceOf[check_your_answers]

  val filledOutCacheMap = new CacheMap(
    "id",
    Map[String, JsValue](
      Constants.dateOfDeathId                            -> JsString("2019-03-04"),
      Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
      Constants.valueOfEstateId                          -> JsNumber(1234),
      Constants.chargeableEstateValueId                  -> JsNumber(1234),
      Constants.propertyInEstateId                       -> JsBoolean(true),
      Constants.propertyValueId                          -> JsNumber(1234),
      Constants.propertyPassingToDirectDescendantsId     -> JsString(Constants.some),
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

  val incompleteCacheMap = new CacheMap(
    "id",
    filledOutCacheMap.data -- Seq(
      Constants.noDownsizingThresholdIncrease,
      Constants.valueAvailableWhenPropertyChangedId,
      Constants.transferAvailableWhenPropertyChangedId,
      Constants.claimDownsizingThresholdId,
      Constants.valueOfAssetsPassingId
    )
  )

  def controller() = new CheckYourAnswersController(
    messagesControllerComponents,
    mockSessionConnector,
    validatedSession,
    check_your_answers
  )

  "Check Your Answers Controller" must {
    "return 200 for a GET" in {
      setCacheMap(filledOutCacheMap)
      val result = controller().onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the Check Your Answers view for a GET" in {
      setCacheMap(filledOutCacheMap)
      val result = controller().onPageLoad(fakeRequest)
      contentAsString(result) mustBe check_your_answers(AnswerRows(filledOutCacheMap, messages))(fakeRequest, messages)
        .toString()
    }

    "redirect when required answers are not present" in {
      setCacheMap(incompleteCacheMap)
      val result = controller().onPageLoad(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
    }
  }

}
