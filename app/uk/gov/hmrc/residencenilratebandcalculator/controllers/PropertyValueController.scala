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

import javax.inject.{Inject, Singleton}

import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.data.{Form, FormError}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.views.html.property_value

import scala.concurrent.Future

@Singleton
class PropertyValueController @Inject()(override val appConfig: FrontendAppConfig,
                                        val messagesApi: MessagesApi,
                                        override val sessionConnector: SessionConnector,
                                        override val navigator: Navigator) extends SimpleControllerBase[Int] {


  override val controllerId = Constants.propertyValueId

  override def form = () => NonNegativeIntForm()

  override def view(form: Option[Form[Int]])(implicit request: Request[_]) = {
    val backUrl = navigator.lastPage(controllerId)().url
    property_value(appConfig, backUrl, form)
  }

  override def validate(value: Int)(implicit hc: HeaderCarrier): Future[Option[FormError]] = {
    sessionConnector.fetchAndGetEntry[Int](Constants.grossEstateValueId).map {
      case None => Some(FormError("value", "property_value.greater_than_gross_estate_value.error"))
      case Some(g) if value > g => Some(FormError("value", "property_value.greater_than_gross_estate_value.error"))
      case _ => None
    }
  }
}
