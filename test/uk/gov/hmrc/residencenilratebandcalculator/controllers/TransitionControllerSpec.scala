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

import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import play.api.http.Status
import play.api.libs.json.*
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, DefaultMessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.{AnswerRows, CacheMap, GetReason, Reason, UserAnswers}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class TransitionControllerSpec extends CommonPlaySpec with MockSessionConnector with WithCommonFakeApplication {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val injector: Injector = fakeApplication.injector

  def mockMessagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  val injectedMessagesControllerComponents: DefaultMessagesControllerComponents =
    injector.instanceOf[DefaultMessagesControllerComponents]

  def messages: Messages = mockMessagesApi.preferred(fakeRequest)

  private[controllers] class TestTransitionController
      extends FrontendController(injectedMessagesControllerComponents)
      with TransitionController {
    val sessionConnector: SessionConnector = mockSessionConnector
    val getReason: GetReason = new GetReason { def apply(userAnswers: UserAnswers): Reason = new Reason {} }
    override implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
    def getControllerId(reason: Reason)        = ""

    def createView(reason: Reason, userAnswers: UserAnswers)(implicit request: Request[_]): HtmlFormat.Appendable =
      HtmlFormat.empty

  }

  def createController = new TestTransitionController()

  "Transition controller" must {
    "return 200 for a GET" in {
      val result = createController.onPageLoad(fakeRequest)
      status(result) mustBe Status.OK
    }

    "return the Unable To Calculate Threshold Increase view for a GET" in {
      val result = createController.onPageLoad(fakeRequest)
      contentAsString(result) mustBe ""
    }

    "redirect to the SessionExpiredController when no CacheMap can be found" in {
      when(mockSessionConnector.fetch()(any[HeaderCarrier])).thenReturn(Future.successful(None))
      val result = createController.onPageLoad(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad.url)
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
        val controllerId = createController().controllerId
        val calculatedConstants =
          AnswerRows.truncateAndLocateInCacheMap(controllerId, filledOutCacheMap).data.keys.toList
        val calculatedList = AnswerRows.rowOrderList.filter(calculatedConstants contains _)
        answerRowConstants mustBe calculatedList
      }
    }
  }

}
