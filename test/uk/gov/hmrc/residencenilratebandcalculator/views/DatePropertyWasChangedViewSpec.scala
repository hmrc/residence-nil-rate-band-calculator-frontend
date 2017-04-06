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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_property_was_changed

import scala.language.reflectiveCalls

class DatePropertyWasChangedViewSpec  extends DateViewSpecBase {

  val messageKeyPrefix = "date_property_was_changed"

  def createView(form: Option[Form[Date]] = None) = date_property_was_changed(frontendAppConfig, backUrl, form, Seq())(request, messages)

  "Date Property Was Changed View" must {

    behave like rnrbPage[Date](createView, messageKeyPrefix, "guidance1", "guidance2")

    behave like pageWithoutBackLink[Date](createView)

    behave like datePage(createView, messageKeyPrefix, DatePropertyWasChangedController.onSubmit().url)

    behave like pageContainingPreviousAnswers(createView)

  }
}
