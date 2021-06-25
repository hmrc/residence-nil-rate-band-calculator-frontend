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

package uk.gov.hmrc.residencenilratebandcalculator.connectors

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.DefaultHttpClient
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.JsonInvalidException
import uk.gov.hmrc.residencenilratebandcalculator.json.JsonErrorProcessor
import uk.gov.hmrc.residencenilratebandcalculator.models.{CalculationInput, CalculationResult}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class RnrbConnector @Inject()(val http: DefaultHttpClient,
                              val config: FrontendAppConfig)(implicit ec: ExecutionContext) {

  lazy val serviceUrl: String = config.serviceUrl
  val baseSegment = "/residence-nil-rate-band-calculator/"
  val schemaBaseSegment = s"${baseSegment}api/conf/0.1/schemas/"
  val jsonContentTypeHeader: (String, String) = ("Content-Type", "application/json")

  def getStyleGuide()(implicit hc: HeaderCarrier): Future[HttpResponse] = http.GET(s"$serviceUrl${baseSegment}style-guide")

  implicit val calculationResultWrites: Writes[CalculationInput] = new Writes[CalculationInput] {
    def writes(o : CalculationInput): JsValue = {
      import play.api.libs.json.JodaWrites.DefaultJodaLocalDateWrites

      val propertyValueAfter = o.propertyValueAfterExemption match {
        case Some(pvae) => Json.obj("propertyValueAfterExemption" -> pvae)
        case _          => Json.obj()
      }

      val downsizingDetails = o.downsizingDetails match {
        case Some(down) => Json.obj("downsizingDetails" ->
          Json.obj(
            "datePropertyWasChanged" -> Json.toJson(down.datePropertyWasChanged),
            "valueOfChangedProperty" -> Json.toJson(down.valueOfChangedProperty),
            "valueOfAssetsPassing" -> Json.toJson(down.valueOfAssetsPassing),
            "valueAvailableWhenPropertyChanged" -> Json.toJson(down.valueAvailableWhenPropertyChanged)
          )
        )
        case _          => Json.obj()
      }

      Json.obj(
        "dateOfDeath" -> Json.toJson(o.dateOfDeath),
        "valueOfEstate" -> Json.toJson(o.valueOfEstate),
        "chargeableEstateValue" -> Json.toJson(o.chargeableEstateValue),
        "propertyValue" -> Json.toJson(o.propertyValue),
        "percentagePassedToDirectDescendants" -> Json.toJson(o.percentagePassedToDirectDescendants),
        "valueBeingTransferred" -> Json.toJson(o.valueBeingTransferred)
      ) ++ propertyValueAfter ++ downsizingDetails
    }
  }

  def send(input: CalculationInput) (implicit hc: HeaderCarrier): Future[Try[CalculationResult]] = sendJson(Json.toJson(input))

  def sendJson(json: JsValue) (implicit hc: HeaderCarrier): Future[Try[CalculationResult]] = {
    http.POST[JsValue,HttpResponse](s"$serviceUrl${baseSegment}calculate", json, Seq(jsonContentTypeHeader))
      .map {
        response =>
          Json.fromJson[CalculationResult](response.json) match {
            case JsSuccess(result, _) => Success(result)
            case JsError(error) =>
              Failure(new JsonInvalidException(JsonErrorProcessor(error)))
          }
      }
  }

  def getNilRateBand(dateStr: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = http.GET(s"$serviceUrl${baseSegment}nilrateband/$dateStr")

}