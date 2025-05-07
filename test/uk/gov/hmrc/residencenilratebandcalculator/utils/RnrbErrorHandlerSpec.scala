/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import org.scalatest.matchers.must.Matchers.must
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import play.api.i18n.MessagesApi

import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.residencenilratebandcalculator.common.{CommonPlaySpec, WithCommonFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.MockSessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.mocks.HttpResponseMocks
import uk.gov.hmrc.residencenilratebandcalculator.views.html.error_template

class RnrbErrorHandlerSpec
    extends CommonPlaySpec
    with HttpResponseMocks
    with MockSessionConnector
    with WithCommonFakeApplication {
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  val injector: Injector                                    = fakeApplication.injector
  private val messageApi: MessagesApi                       = injector.instanceOf[MessagesApi]
  private val errorTemplate: error_template                 = injector.instanceOf[error_template]
  private val errorHandler: FrontendErrorHandler            = new RnrbErrorHandler(messageApi, errorTemplate)

  "RnrbErrorHandler" must {

    "return an error page" in {
      val result = errorHandler.standardErrorTemplate(
        pageTitle = "pageTitle",
        heading = "heading",
        message = "message"
      )(request)

      result.body must include("pageTitle")
      result.body must include("heading")
      result.body must include("message")
    }
  }

}
