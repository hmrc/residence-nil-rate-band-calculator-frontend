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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.views.html.{fake_gov_uk_case_studies, fake_gov_uk_guidance}
import scala.concurrent.Future

@Singleton
class FakeGovUkController @Inject()(val appConfig: FrontendAppConfig,
                                    val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  def caseStudies = Action.async { implicit request =>
    Future.successful(Ok(fake_gov_uk_case_studies()))
  }

  def guidance = Action.async { implicit request =>
    Future.successful(Ok(fake_gov_uk_guidance()))
  }
}
