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

package uk.gov.hmrc.residencenilratebandcalculator

import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class FrontendAppConfig @Inject()(val servicesConfig: ServicesConfig) {
  private def loadConfig(key: String) = servicesConfig.getString(key)

  lazy val contactHost: String = servicesConfig.getConfString("contact-frontend.www", "")
  lazy val contactFormServiceIdentifier = "RNRB"

  lazy val timeOutCountdownSeconds: Int = loadConfig("timeOutCountdownSeconds").toInt
  lazy val timeOutSession: Int = loadConfig("mongodb.timeToLiveInSeconds").toInt
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"
  lazy val feedbackSurvey: String = loadConfig("feedback-survey-frontend.url")
  lazy val serviceUrl: String = servicesConfig.baseUrl("residence-nil-rate-band-calculator")

  val isWelshEnabled: Boolean = true
}
