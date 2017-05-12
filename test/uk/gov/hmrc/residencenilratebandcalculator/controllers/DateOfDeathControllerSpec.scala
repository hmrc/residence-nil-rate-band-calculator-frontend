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

import com.google.inject.Provider
import play.api.Application
import play.api.http.Status
import play.api.libs.json.{Reads, Writes}
import play.api.test.Helpers._
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.forms.DateForm
import uk.gov.hmrc.residencenilratebandcalculator.models.Date
import uk.gov.hmrc.residencenilratebandcalculator.views.html.date_of_death

class DateOfDeathControllerSpec extends DateControllerSpecBase {
  def applicationProvider: Provider[Application] = injector.instanceOf[Provider[Application]]
  implicit val appProvider: Provider[Application] = applicationProvider
  "Date of Death Controller" must {

    def createView = (value: Option[Map[String, String]]) => value match {
      case None => date_of_death(frontendAppConfig)(fakeRequest, messages, applicationProvider)
      case Some(v) => date_of_death(frontendAppConfig,
        Some(DateForm("date_of_death.error.day_invalid", "date_of_death.error.month_invalid", "date_of_death.error.year_invalid", "date_of_death.error").
          bind(v)))(fakeRequest, messages, applicationProvider)
    }

    def createController = () => new DateOfDeathController(frontendAppConfig, messagesApi, mockSessionConnector, navigator, applicationProvider)

    behave like rnrbDateController(createController, createView, Constants.dateOfDeathId)(Date.dateReads, Date.dateWrites)
  }
}
