/*
 * Copyright 2016 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value
import play.api.test.Helpers._

class GrossEstateValueViewSpec extends UnitSpec with WithFakeApplication {

  val fakeRequest = FakeRequest("GET", "/")
  val injector = fakeApplication.injector

  def frontendAppConfig = injector.instanceOf[FrontendAppConfig]
  def messagesApi = injector.instanceOf[MessagesApi]

  def messages = messagesApi.preferred(fakeRequest)

  "Gross Estate Value View" must {

    "display the correct browser title" in {
      val view = gross_estate_value(frontendAppConfig)(fakeRequest, messages)
      contentAsString(view) should include (s"<title>${messages("gross_estate_value.browser_title")}</title>")
    }

    "display the correct page title" in {
      val view = gross_estate_value(frontendAppConfig)(fakeRequest, messages)
      contentAsString(view) should include (s"<h1>${messages("gross_estate_value.title")}</h1>")
    }

    "display the correct guidance" in {
      val view = gross_estate_value(frontendAppConfig)(fakeRequest, messages)
      contentAsString(view) should include (s"<p>${messages("gross_estate_value.guidance")}</p>")
    }
  }
}
