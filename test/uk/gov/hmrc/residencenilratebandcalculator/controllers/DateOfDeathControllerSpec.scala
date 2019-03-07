/*
 * Copyright 2019 HM Revenue & Customs
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

import akka.stream.Materializer
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm._
import org.mockito.Mockito._
import play.api.mvc.DefaultMessagesControllerComponents
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class DateOfDeathControllerSpec extends DateControllerSpecBase {

  val mockConnector = mock[SessionConnector]
  val mockConfig = injector.instanceOf[FrontendAppConfig]
  val messagesControllerComponents = injector.instanceOf[DefaultMessagesControllerComponents]
  val controller = new DateOfDeathController(messagesControllerComponents, mockConnector, navigator, mockConfig)
  implicit val mat = fakeApplication.injector.instanceOf[Materializer]

  def setupMock(result: Future[Option[CacheMap]]) = {
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
        Jsoup.parse(await(bodyOf(result))).title shouldBe "What was the date of death?"
      }
    }

    "sessionConnector returns a cachemap" should {
      lazy val result = controller.onPageLoad(implicitly)(fakeRequest)

      "return a status of OK" in {
        setupMock(Future.successful(Some(CacheMap("id", Map()))))
        status(result) shouldBe 200
      }

      "load the correct page" in {
        Jsoup.parse(await(bodyOf(result))).title shouldBe "What was the date of death?"
      }
    }
  }

  "Date of Death Controller" must {

    def createView = (value: Option[Date]) => value match {
      case None => date_of_death(dateOfDeathForm)(fakeRequest, messages, mockConfig)
      case Some(v) => date_of_death(dateOfDeathForm.fill(v))(fakeRequest, messages, mockConfig)
    }

    def createController = () => new DateOfDeathController(messagesControllerComponents, mockSessionConnector, navigator, mockConfig)

    behave like rnrbDateController(createController, createView, Constants.dateOfDeathId)(Date.dateReads, Date.dateWrites)
  }
}
