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

import com.eclipsesource.schema.SchemaType
import play.api.libs.json._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.residencenilratebandcalculator.WSHttp
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.models.CalculationResult

import scala.concurrent.ExecutionContext.Implicits.global
import play.Logger
import play.api.data.validation.ValidationError

import scala.util.{Failure, Success, Try}

@Singleton
class RnrbConnector @Inject()(http: WSHttp) extends ServicesConfig {
  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val serviceUrl = baseUrl("residence-nil-rate-band-calculator")
  val baseSegment = "/residence-nil-rate-band-calculator/"
  val jsonContentTypeHeader = ("Content-Type", "application/json")

  def getHelloWorld = http.GET(s"$serviceUrl${baseSegment}hello-world")
  def getStyleGuide = http.GET(s"$serviceUrl${baseSegment}style-guide")

  def send(json: JsValue) =
    http.POST(s"$serviceUrl${baseSegment}calculate", json, Seq(jsonContentTypeHeader))
    .map {
      response => Json.fromJson[CalculationResult](response.json) match {
        case JsSuccess(result, _) => Success(result)
        case JsError(error) => {
          val msg  = error.seq.flatMap(_._2).map(_.message).foldLeft(new StringBuilder())(_ append _).toString()
          Failure(new JsonInvalidException(msg))
        }
      }
    }

  def getSuccessfulResponseSchema =
    http.GET(s"$serviceUrl${baseSegment}schemas/deceaseds-estate.jsonschema").map {
      response => Json.fromJson[SchemaType](response.json) match {
        case JsSuccess(schema, _) => Success(schema)
        case error: JsError => {
          Left((JsError.toJson(error) \ "obj" \ 0 \ "msg" \ 0).as[String])
          val errorLookupResult = (JsError.toJson(error) \ "obj" \ 0 \ "msg" \ 0).as[String]
          Failure(new JsonInvalidException(errorLookupResult.toString))
        }
      }
    }
}
