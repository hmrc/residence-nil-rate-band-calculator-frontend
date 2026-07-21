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
import org.mockito.Mockito.*
import org.mockito.stubbing.OngoingStubbing
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.helpers.DateControllerSpec
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.forms.constructors.DateOfDeathForm.*
import uk.gov.hmrc.residencenilratebandcalculator.models.{CacheMap, Date}
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death
import uk.gov.hmrc.residencenilratebandcalculator.Constants

import scala.concurrent.Future

class DateOfDeathControllerSpec extends DateControllerSpec {

  val mockConnector: SessionConnector = mock[SessionConnector]

  val mockValidatedSession: ValidatedSession = inject[ValidatedSession]
  val date_of_death: date_of_death           = inject[date_of_death]

  val controller = new DateOfDeathController(
    messagesControllerComponents,
    mockConnector,
    navigator,
    mockValidatedSession,
    date_of_death
  )

  implicit val mat: Materializer = inject[Materializer]

  def setupMock(result: Future[Option[CacheMap]]): OngoingStubbing[Future[Option[CacheMap]]] =
    when(mockConnector.fetch()(ArgumentMatchers.any[HeaderCarrier]))
      .thenReturn(result)

  "Loading the page" when {

    "sessionConnector returns a fail" must {

      "return the exception" in {
        setupMock(Future.failed(new Exception("Test message")))
        val result = controller.onPageLoad(implicitly)(fakeRequest)

        (the[Exception] thrownBy await(result) must have).message("Test message")
      }
    }

    "sessionConnector returns a none" must {
      lazy val result = controller.onPageLoad(implicitly)(fakeRequest)

      "return a status of OK" in {
        setupMock(Future.successful(None))
        status(result) mustBe 200
      }

      "load the correct page" in {
        Jsoup
          .parse(await(bodyOf(result)))
          .title mustBe "What was the date of death? - Calculate the available RNRB - GOV.UK"
      }
    }

    "sessionConnector returns a cachemap" must {
      lazy val result = controller.onPageLoad(implicitly)(fakeRequest)

      "return a status of OK" in {
        setupMock(Future.successful(Some(CacheMap("id", Map()))))
        status(result) mustBe 200
      }

      "load the correct page" in {
        Jsoup
          .parse(await(bodyOf(result)))
          .title mustBe "What was the date of death? - Calculate the available RNRB - GOV.UK"
      }
    }
  }

  "Date of Death Controller" must {

    def createView: Option[Date] => Html = {
      case None    => date_of_death(dateOfDeathForm(messages))(fakeRequest, messages)
      case Some(v) => date_of_death(dateOfDeathForm(messages).fill(v))(fakeRequest, messages)
    }

    def createController: () => DateOfDeathController = () =>
      new DateOfDeathController(
        messagesControllerComponents,
        mockSessionConnector,
        navigator,
        mockValidatedSession,
        date_of_death
      )

    behave.like(
      rnrbDateController(createController, createView, Constants.dateOfDeathId)(Date.dateReads, Date.dateWrites)
    )
  }

}
