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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import play.api.libs.json._
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.json.JsonErrorProcessor
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class RnrbConnector @Inject() (val httpClientV2: HttpClientV2, val config: FrontendAppConfig)(
    implicit ec: ExecutionContext
) {

  lazy val baseSegment = s"${config.serviceUrl}/residence-nil-rate-band-calculator"
  val jsonContentTypeHeader: (String, String) = ("Content-Type", "application/json")

  implicit val calculationResultWrites: Writes[CalculationInput] = Writes { (input: CalculationInput) =>
    val propertyValueAfter = input.propertyValueAfterExemption match {
      case Some(pvae) => Json.obj("propertyValueAfterExemption" -> pvae)
      case _ => Json.obj()
    }

    val downsizingDetails = input.downsizingDetails match {
      case Some(down) =>
        Json.obj(
          "downsizingDetails" ->
            Json.obj(
              "datePropertyWasChanged" -> Json.toJson(down.datePropertyWasChanged),
              "valueOfChangedProperty" -> Json.toJson(down.valueOfChangedProperty),
              "valueOfAssetsPassing" -> Json.toJson(down.valueOfAssetsPassing),
              "valueAvailableWhenPropertyChanged" -> Json.toJson(down.valueAvailableWhenPropertyChanged)
            )
        )
      case _ => Json.obj()
    }

    Json.obj(
      "dateOfDeath" -> Json.toJson(input.dateOfDeath),
      "valueOfEstate" -> Json.toJson(input.valueOfEstate),
      "chargeableEstateValue" -> Json.toJson(input.chargeableEstateValue),
      "propertyValue" -> Json.toJson(input.propertyValue),
      "percentagePassedToDirectDescendants" -> Json.toJson(input.percentagePassedToDirectDescendants),
      "valueBeingTransferred" -> Json.toJson(input.valueBeingTransferred)
    ) ++ propertyValueAfter ++ downsizingDetails
  }

  def send(input: CalculationInput)(implicit hc: HeaderCarrier): Future[Try[CalculationResult]] = sendJson(
    Json.toJson(input)
  )

  def sendJson(json: JsValue)(implicit hc: HeaderCarrier): Future[Try[CalculationResult]] = {

    httpClientV2.post(url"$baseSegment/calculate").withBody(Json.toJson(json)).setHeader(jsonContentTypeHeader).execute[HttpResponse].map {
      response =>
        Json.fromJson[CalculationResult](response.json) match {
          case JsSuccess(result, _) => Success(result)
          case JsError(error) =>
            Failure(new JsonInvalidException(JsonErrorProcessor(error)))
        }
    }
  }

    def getNilRateBand(dateStr: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
      httpClientV2.get(url"$baseSegment/nilrateband/$dateStr").execute[HttpResponse]
    }

  }

