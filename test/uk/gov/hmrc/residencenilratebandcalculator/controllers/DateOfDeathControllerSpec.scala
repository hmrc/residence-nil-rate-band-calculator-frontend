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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.joda.time.LocalDate
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death

class DateOfDeathControllerSpec extends SimpleControllerSpecBase {

  "Date of Death Controller" must {

    def createView = (value: Option[Date]) => value match {
      case None => date_of_death(frontendAppConfig)(fakeRequest, messages)
      case Some(v) => date_of_death(frontendAppConfig, Some(DateForm().fill(v)))(fakeRequest, messages)
    }

    def createController = () => new DateOfDeathController(frontendAppConfig, messagesApi, mockSessionConnector, navigator)

    val day = 1
    val month = 6
    val year = 2017

    val validData = Date(day, month, year)

    val validRequestBody = Map("day" -> day.toString, "month" -> month.toString, "year" -> year.toString)

    val invalidRequestBody = Map("day" -> "invalid data", "month" -> "invalid data", "year" -> "invalid data")

    def cacheValue = () => setCacheValue[LocalDate](Constants.dateOfDeathId, new LocalDate(year, month, day))

    def cacheFormValue = () => setCacheValue[Date](Constants.dateOfDeathId, Date(day, month, year))

    behave like rnrbController(createController, createView, Constants.dateOfDeathId, validData, validRequestBody,
      invalidRequestBody, cacheValue, cacheFormValue)(Date.dateReads, Date.dateWrites)
  }
}
