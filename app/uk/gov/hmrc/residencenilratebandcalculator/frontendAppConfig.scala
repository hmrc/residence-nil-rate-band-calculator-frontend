/*
 * Copyright 2018 HM Revenue & Customs
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
import java.util.Base64

import play.api.Mode.Mode
import play.api.{Configuration, Mode}
import uk.gov.hmrc.play.config.{RunMode, ServicesConfig}

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val timeOutCountdownSeconds: Int
  val timeOutSession: Int
  val isWelshEnabled: Boolean
}

@Singleton
class FrontendAppConfig @Inject()(override val runModeConfiguration: Configuration) extends AppConfig with ServicesConfig {

  private def loadConfig(key: String) = runModeConfiguration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  private lazy val contactHost = runModeConfiguration.getString("contact-frontend.host").getOrElse("")
  private val contactFormServiceIdentifier = "RNRB"
  private lazy val contactFrontendService = baseUrl("contact-frontend")

  override lazy val analyticsToken = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost = loadConfig(s"google-analytics.host")
  override lazy val timeOutCountdownSeconds = loadConfig("timeOutCountdownSeconds").toInt
  override lazy val timeOutSession = loadConfig("mongodb.timeToLiveInSeconds").toInt
  lazy val reportAProblemPartialUrl = s"$contactFrontendService/contact/problem_reports"
  override lazy val reportAProblemNonJSUrl = s"$contactFrontendService/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  private def whitelistConfig(key: String):Seq[String] = Some(new String(Base64.getDecoder.decode(runModeConfiguration.getString(key).getOrElse("")), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val whitelist = whitelistConfig("whitelist")
  lazy val whitelistExcluded = whitelistConfig("whitelistExcludedCalls")

  override val isWelshEnabled: Boolean = true

  override protected def mode: Mode = env match {
    case "dev" => Mode.Dev
    case "test" => Mode.Test
    case "prod" => Mode.Prod
  }
}
