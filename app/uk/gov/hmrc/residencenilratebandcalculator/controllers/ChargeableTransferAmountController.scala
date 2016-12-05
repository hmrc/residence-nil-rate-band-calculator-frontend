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

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_transfer_amount

import scala.concurrent.Future

class ChargeableTransferAmountController  @Inject()(appConfig: FrontendAppConfig, val messagesApi: MessagesApi, sessionConnector: SessionConnector)
  extends FrontendController with I18nSupport {

  val onPageLoad = Action.async { implicit request =>
    sessionConnector.fetchAndGetEntry[Int]("ChargeableTransferAmount").map(
      cachedValue => {
        val form = cachedValue.map(value => NonNegativeIntForm().fill(value))
        Ok(chargeable_transfer_amount(appConfig, form))
      })
  }

  val onSubmit = Action.async { implicit request =>
    val boundForm = NonNegativeIntForm().bindFromRequest()
    boundForm.fold(
      (formWithErrors: Form[Int]) => Future.successful(BadRequest(chargeable_transfer_amount(appConfig, Some(formWithErrors)))),
      (value) => sessionConnector.cache[Int]("ChargeableTransferAmount", value).map(_ => Redirect(""))
    )
  }
}
