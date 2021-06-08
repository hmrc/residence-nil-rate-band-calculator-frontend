/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val timeOutCountdownSeconds: Int
  val timeOutSession: Int
  val isWelshEnabled: Boolean
  val betaFeedbackUnauthenticatedUrl : String
  val betaFeedbackUrl : String
  val feedbackSurvey : String
}

@Singleton
class FrontendAppConfig @Inject()(val servicesConfig: ServicesConfig) extends AppConfig {
  private def loadConfig(key: String) = servicesConfig.getString(key)

  private lazy val contactHost = servicesConfig.getConfString("contact-frontend.www", "")
  private val contactFormServiceIdentifier = "RNRB"

  override lazy val timeOutCountdownSeconds: Int = loadConfig("timeOutCountdownSeconds").toInt
  override lazy val timeOutSession: Int = loadConfig("mongodb.timeToLiveInSeconds").toInt
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  override lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"
  override lazy val feedbackSurvey: String = loadConfig("feedback-survey-frontend.url")
  lazy val serviceUrl: String = servicesConfig.baseUrl("residence-nil-rate-band-calculator")

  override val isWelshEnabled: Boolean = true


}
