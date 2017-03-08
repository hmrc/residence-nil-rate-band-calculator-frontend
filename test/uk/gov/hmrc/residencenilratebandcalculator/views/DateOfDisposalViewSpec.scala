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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_disposal

import scala.language.reflectiveCalls

class DateOfDisposalViewSpec  extends DateViewSpecBase {

  val messageKeyPrefix = "date_of_disposal"

  def createView(form: Option[Form[Date]] = None) = date_of_disposal(frontendAppConfig, backUrl, form, Seq())(request, messages)

  "Date of Disposal View" must {

    behave like rnrbPage[Date](createView, messageKeyPrefix, "guidance1", "guidance2")

    behave like pageWithBackLink[Date](createView)

    behave like datePage(createView, messageKeyPrefix, DateOfDisposalController.onSubmit().url)
  }
}
