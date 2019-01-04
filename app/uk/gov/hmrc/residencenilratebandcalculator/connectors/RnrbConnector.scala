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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.residencenilratebandcalculator.WSHttp
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.json.JsonErrorProcessor
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import uk.gov.hmrc.http.{CoreGet, CorePost, HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.{WSGet, WSPost}

@Singleton
class RnrbConnector @Inject()() extends ServicesConfig {
  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val serviceUrl = baseUrl("residence-nil-rate-band-calculator")
  val baseSegment = "/residence-nil-rate-band-calculator/"
  val schemaBaseSegment = s"${baseSegment}api/conf/0.1/schemas/"
  val jsonContentTypeHeader = ("Content-Type", "application/json")
  lazy val http: WSHttp with WSGet with WSPost = WSHttp


  def getStyleGuide = http.GET(s"$serviceUrl${baseSegment}style-guide")

  def send(input: CalculationInput) = sendJson(Json.toJson(input))

  def sendJson(json: JsValue) =
    http.POST(s"$serviceUrl${baseSegment}calculate", json, Seq(jsonContentTypeHeader))
      .map {
        response =>
          Json.fromJson[CalculationResult](response.json) match {
            case JsSuccess(result, _) => Success(result)
            case JsError(error) => {
              Failure(new JsonInvalidException(JsonErrorProcessor(error)))
            }
          }
      }

  def getNilRateBand(dateStr: String): Future[HttpResponse] = http.GET(s"$serviceUrl${baseSegment}nilrateband/$dateStr")

}