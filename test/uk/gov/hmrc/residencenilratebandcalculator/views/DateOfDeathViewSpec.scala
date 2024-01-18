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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateOfDeathForm._

class DateOfDeathViewSpec extends NewDateViewSpecBase {

  val messageKeyPrefix = "date_of_death"
  val date_of_death: date_of_death = injector.instanceOf[date_of_death]
  def createView(form: Form[Date]): HtmlFormat.Appendable = date_of_death(form)(request, messages)

  "Date of Death View" must {

    behave like rnrbPage[Date](createView, messageKeyPrefix, "guidance")(dateOfDeathForm)

    behave like datePage(createView, messageKeyPrefix, DateOfDeathController.onSubmit.url, "dateOfDeath", dateOfDeathForm,dateOfDeathForm)

    "display the correct example" in {
      val doc = asDocument(createView(dateOfDeathForm))
      assertContainsMessages(doc, s"$messageKeyPrefix.example")
    }
  }
}
