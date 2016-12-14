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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import javax.inject.{Inject, Singleton}

import play.api.libs.json.JsValue
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.residencenilratebandcalculator.WSHttp
import uk.gov.hmrc.residencenilratebandcalculator.models.CalculationResult

import scala.concurrent.Future

@Singleton
class RnrbConnector @Inject()(http: WSHttp) extends ServicesConfig {
  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val serviceUrl = baseUrl("residence-nil-rate-band-calculator")

  def getHelloWorld = http.GET(s"$serviceUrl/residence-nil-rate-band-calculator/hello-world")
  def getStyleGuide: Future[HttpResponse] = http.GET(s"$serviceUrl/residence-nil-rate-band-calculator/style-guide")
  def send(json: JsValue): Future[Either[String, CalculationResult]] = ???
}
