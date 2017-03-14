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

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.data.FormError
import play.api.http.Status
import play.api.libs.json.{JsNumber, Reads, Writes}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.RnrbConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.brought_forward_allowance
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BroughtForwardAllowanceControllerSpec extends SimpleControllerSpecBase {

  def mockRnrbConnector = {
    val mockConnector = mock[RnrbConnector]
    when(mockConnector.getNilRateBand(any[String])) thenReturn Future.successful(HttpResponse(200, Some(JsNumber(100000))))
    mockConnector
  }

  def createView = (value: Option[Map[String, String]]) => {
    val url = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.AnyBroughtForwardAllowanceController.onPageLoad().url
    value match {
      case None => brought_forward_allowance(frontendAppConfig, url, "100000", answerRows = Seq())(fakeRequest, messages)
      case Some(v) => brought_forward_allowance(frontendAppConfig, url, "100000", Some(NonNegativeIntForm().bind(v)), Seq())(fakeRequest, messages)
    }
  }

  def createController = () => new BroughtForwardAllowanceController(frontendAppConfig, messagesApi, mockSessionConnector, navigator, mockRnrbConnector)

    "Brought Forward Allowance Controller" must {

    // TODO: determine how to replicate the tests from the 'rnrbController' behaviour

    "On a page load with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onPageLoad(Reads.IntReads)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "On a page submit with an expired session, return an redirect to an expired session page" in {
      expireSessionConnector()
      val result = createController().onSubmit(Writes.IntWrites)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "validate" must {

      "return a FormError when given a value greater than the nil rate band for the year of death" in {
        val testValue = 123000
        val controller = createController()
        val result = controller.validate(testValue, "100000")(new HeaderCarrier())
        result.map(x => x should be(Some(FormError("value", "brought_forward_allowance.error"))))
      }

      "return a None when given a value less than or equal to the nil rate band for the year of death" in {
        val testValue = 90000
        val controller = createController()
        val result = controller.validate(testValue, "100000")(new HeaderCarrier())
        result.map(x => x should be(None))
      }

      "throw an exception when given a nil rate band that cannot be parsed into an int" in {
        val exception = intercept[NumberFormatException]{
          val testValue = 90000
          val controller = createController()
          val result = controller.validate(testValue, "not a number")(new HeaderCarrier())
        }
        exception.getMessage shouldBe "Bad value in nil rate band"
      }

    }

  }
}
