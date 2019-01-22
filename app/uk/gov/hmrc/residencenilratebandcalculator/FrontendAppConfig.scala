/*
 * Copyright 2019 HM Revenue & Customs
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

import java.util.Base64

import javax.inject.Inject
import play.api.{Configuration, Environment}
import play.api.Play.{configuration, current}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val timeOutCountdownSeconds: Int
  val timeOutSession: Int
  val isWelshEnabled: Boolean
  val betaFeedbackUnauthenticatedUrl : String
  val betaFeedbackUrl : String
  val feedbackSurvey : String
  val googleTagManagerId : String
}

class FrontendAppConfig @Inject()(override val runModeConfiguration: Configuration,
                                  val environment: Environment) extends AppConfig with ServicesConfig {

  def mode = environment.mode
  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  private lazy val contactHost = configuration.getString("microservice.services.contact-frontend.www").getOrElse("")
  private val contactFormServiceIdentifier = "RNRB"
  private lazy val contactFrontendService = baseUrl("contact-frontend")
  lazy val googleTagManagerId = loadConfig("google-tag-manager.id")

  override lazy val analyticsToken = loadConfig("google-analytics.token")
  override lazy val analyticsHost = loadConfig("google-analytics.host")
  override lazy val timeOutCountdownSeconds = loadConfig("timeOutCountdownSeconds").toInt
  override lazy val timeOutSession = loadConfig("mongodb.timeToLiveInSeconds").toInt
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  override lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"
  override lazy val feedbackSurvey: String = loadConfig("feedback-survey-frontend.url")
  lazy val serviceUrl = baseUrl("residence-nil-rate-band-calculator")

  private def whitelistConfig(key: String):Seq[String] = Some(new String(Base64.getDecoder.decode(configuration.getString(key).getOrElse("")), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val whitelist = whitelistConfig("whitelist")
  lazy val whitelistExcluded = whitelistConfig("whitelistExcludedCalls")

  override val isWelshEnabled: Boolean = true


}
