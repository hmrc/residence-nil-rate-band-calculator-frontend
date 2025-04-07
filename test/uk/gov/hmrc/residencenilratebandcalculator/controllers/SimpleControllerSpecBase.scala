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

import org.jsoup.Jsoup
import play.api.http.Status
import play.api.i18n._
import play.api.inject.Injector
import play.api.libs.json._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.models._
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

import scala.reflect.ClassTag

trait SimpleControllerSpecBase
    extends CommonPlaySpec
    with HttpResponseMocks
    with MockSessionConnector
    with WithCommonFakeApplication {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "").withSession(SessionKeys.sessionId -> "id")

  val injector: Injector = fakeApplication.injector

  val navigator: Navigator = injector.instanceOf[Navigator]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def messages: Messages = messagesApi.preferred(fakeRequest)

  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def rnrbController[A: ClassTag](
      createController: () => ControllerBase[A],
      createView: Option[Map[String, String]] => HtmlFormat.Appendable,
      cacheKey: String,
      messageKeyPrefix: String,
      testValue: A,
      valuesToCacheBeforeSubmission: Map[String, A] = Map[String, A](),
      valuesToCacheBeforeLoad: Map[String, Any] = Map[String, Any]()
  )(rds: Reads[A], wts: Writes[A]): Unit = {

    "return 200 for a GET" in {
      for (v <- valuesToCacheBeforeLoad) setCacheValue(v._1, v._2)
      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the View for a GET" in {
      for (v <- valuesToCacheBeforeLoad) setCacheValue(v._1, v._2)
      val result = createController().onPageLoad(rds)(fakeRequest)
      Jsoup.parse(contentAsString(result)).title() mustBe messages(s"$messageKeyPrefix.title")
    }

    "return a redirect on submit with valid data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString)).withMethod("POST")
      for (v <- valuesToCacheBeforeSubmission) setCacheValue(v._1, v._2)
      setCacheValue(cacheKey, testValue)
      val result = createController().onSubmit(wts)(fakePostRequest)
      status(result) mustBe Status.SEE_OTHER
    }

    "store valid submitted data" in {
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", testValue.toString)).withMethod("POST")
      for (v <- valuesToCacheBeforeSubmission) setCacheValue(v._1, v._2)
      setCacheValue(cacheKey, testValue)
      await(createController().onSubmit(wts)(fakePostRequest))
      verifyValueIsCached(cacheKey, testValue)
    }

    "return bad request on submit with invalid data" in {
      for (v <- valuesToCacheBeforeLoad) setCacheValue(v._1, v._2)
      val value           = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value)).withMethod("POST")
      val result          = createController().onSubmit(wts)(fakePostRequest)
      status(result) mustBe Status.BAD_REQUEST
    }

    "return form with errors when invalid data is submitted" in {
      for (v <- valuesToCacheBeforeLoad) setCacheValue(v._1, v._2)
      val value           = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value)).withMethod("POST")
      val result          = createController().onSubmit(wts)(fakePostRequest)
      contentAsString(result) mustBe createView(Some(Map("value" -> value))).toString
    }

    "not store invalid submitted data" in {
      val value           = "invalid data"
      val fakePostRequest = fakeRequest.withFormUrlEncodedBody(("value", value)).withMethod("POST")
      createController().onSubmit(wts)(fakePostRequest)
      verifyValueIsNotCached()
    }

    "get a previously stored value from keystore" in {
      for (v <- valuesToCacheBeforeLoad) setCacheValue(v._1, v._2)
      setCacheValue(cacheKey, testValue)
      val result = createController().onPageLoad(rds)(fakeRequest)
      contentAsString(result) mustBe createView(Some(Map("value" -> testValue.toString))).toString
    }
  }

  def nonStartingController[A: ClassTag](
      createController: () => SimpleControllerBase[A],
      answerRowConstants: List[String]
  )(rds: Reads[A], wts: Writes[A]): Unit = {

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onPageLoad(rds)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(
        uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url
      )
    }

    "On a page submit with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onSubmit(wts)(fakeRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(
        uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad.url
      )
    }

    "The answer constants must be the same as the calulated constants for the controller" in {
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
      val controllerId        = createController().controllerId
      val calculatedConstants = AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
      val calculatedList      = AnswerRows.rowOrderList.filter(calculatedConstants contains _)
      answerRowConstants mustBe calculatedList
    }
  }

}
