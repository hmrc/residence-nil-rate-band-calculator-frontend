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

import org.apache.pekko.stream.Materializer
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import play.api.mvc.DefaultMessagesControllerComponents
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.common.CommonPlaySpec
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateOfDeathForm._
import uk.gov.hmrc.residencenilratebandcalculator.models.{CacheMap, Date}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}

import scala.concurrent.Future

class DateOfDeathControllerSpec extends DateControllerSpecBase with CommonPlaySpec {

  val mockConnector: SessionConnector = mock[SessionConnector]
  val mockConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val messagesControllerComponents: DefaultMessagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val mockValidatedSession: ValidatedSession = injector.instanceOf[ValidatedSession]
  val date_of_death: date_of_death = injector.instanceOf[date_of_death]
  val controller = new DateOfDeathController(messagesControllerComponents, mockConnector, navigator, mockValidatedSession, date_of_death)

  implicit val mat: Materializer = fakeApplication.injector.instanceOf[Materializer]

  def setupMock(result: Future[Option[CacheMap]]): OngoingStubbing[Future[Option[CacheMap]]] = {
    when(mockConnector.fetch()(ArgumentMatchers.any[HeaderCarrier]))
      .thenReturn(result)
  }

  "Loading the page" when {

    "sessionConnector returns a fail" should {

      "return the exception" in {
        setupMock(Future.failed(new Exception("Test message")))
        val result = controller.onPageLoad(implicitly)(fakeRequest)

        the[Exception] thrownBy await(result) should have message "Test message"
      }
    }

    "sessionConnector returns a none" should {
      lazy val result = controller.onPageLoad(implicitly)(fakeRequest)

      "return a status of OK" in {
        setupMock(Future.successful(None))
        status(result) shouldBe 200
      }

      "load the correct page" in {
        Jsoup.parse(await(bodyOf(result))).title shouldBe "What was the date of death? - Calculate the available RNRB - GOV.UK"
      }
    }

    "sessionConnector returns a cachemap" should {
      lazy val result = controller.onPageLoad(implicitly)(fakeRequest)

      "return a status of OK" in {
        setupMock(Future.successful(Some(CacheMap("id", Map()))))
        status(result) shouldBe 200
      }

      "load the correct page" in {
        Jsoup.parse(await(bodyOf(result))).title shouldBe "What was the date of death? - Calculate the available RNRB - GOV.UK"
      }
    }
  }

  "Date of Death Controller" must {

    def createView: Option[Date] => HtmlFormat.Appendable = {
      case None => date_of_death(dateOfDeathForm(messages))(fakeRequest, messages)
      case Some(v) => date_of_death(dateOfDeathForm(messages).fill(v))(fakeRequest, messages)
    }

    def createController: () => DateOfDeathController = () =>
      new DateOfDeathController(messagesControllerComponents, mockSessionConnector, navigator, mockValidatedSession, date_of_death)

    behave like rnrbDateController(createController, createView, Constants.dateOfDeathId)(Date.dateReads, Date.dateWrites)
  }
}
