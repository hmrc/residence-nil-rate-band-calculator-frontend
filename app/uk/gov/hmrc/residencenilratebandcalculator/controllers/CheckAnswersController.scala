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

import org.joda.time.LocalDate
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json._
import play.api.mvc.{Action, Call, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.AnswerRow
import uk.gov.hmrc.residencenilratebandcalculator.views.html.check_answers
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, FrontendAppConfig, Navigator}

@Singleton
class CheckAnswersController @Inject()(val appConfig: FrontendAppConfig,
                                       val navigator: Navigator,
                                       val messagesApi: MessagesApi,
                                       val sessionConnector: SessionConnector) extends FrontendController with I18nSupport {

  val rowOrder = Map[String, Int](
    Constants.dateOfDeathId -> 1,
    Constants.grossEstateValueId -> 2,
    Constants.chargeableTransferAmountId -> 3,
    Constants.estateHasPropertyId -> 4,
    Constants.anyDownsizingAllowanceId -> 5,
    Constants.dateOfDisposalId -> 6,
    Constants.valueOfDisposedPropertyId -> 7,
    Constants.anyAssetsPassingToDirectDescendantsId -> 8,
    // Not obvious where the following rows are going to be in the page
    Constants.anyBroughtForwardAllowanceId -> 109,
    Constants.anyBroughtForwardAllowanceOnDisposalId -> 116,
    Constants.anyExemptionId -> 107,
    Constants.assetsPassingToDirectDescendantsId -> 115,
    Constants.broughtForwardAllowanceId -> 110,
    Constants.broughtForwardAllowanceOnDisposalId -> 117,
    Constants.percentageCloselyInheritedId -> 106,
    Constants.propertyValueId -> 105,
    Constants.propertyValueAfterExemptionId -> 108
  )

  def intAnswerRowFn(summaryKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[Int](jsValue).fold(_ => throw new RuntimeException(s"$title unavailable from cache"), {
      value => AnswerRow(summaryKey, value, url()) _
    })

  def boolAnswerRowFn(summaryKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[Boolean](jsValue).fold(_ => throw new RuntimeException(s"$title unavailable from cache"), {
      value => AnswerRow(summaryKey, value, url()) _
    })

  def dateAnswerRowFn(summaryKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[LocalDate](jsValue).fold(_ => throw new RuntimeException(s"$title unavailable from cache"), {
      value => AnswerRow(summaryKey, value, url()) _
    })

  def percentAnswerRowFn(summaryKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[Double](jsValue).fold(_ => throw new RuntimeException(s"$title unavailable from cache"), {
      value => AnswerRow(summaryKey, value, url()) _
    })

  val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
    Constants.dateOfDeathId ->
      dateAnswerRowFn("date_of_death.summary", "Date of death", routes.DateOfDeathController.onPageLoad),
    Constants.grossEstateValueId ->
      intAnswerRowFn("gross_estate_value.summary", "Gross estate value", routes.GrossEstateValueController.onPageLoad),
    Constants.chargeableTransferAmountId ->
      intAnswerRowFn("chargeable_transfer_amount.summary", "Chargeable transfer amount", routes.ChargeableTransferAmountController.onPageLoad),
    Constants.estateHasPropertyId ->
      boolAnswerRowFn("estate_has_property.summary", "Estate has property", routes.EstateHasPropertyController.onPageLoad),
    Constants.anyDownsizingAllowanceId ->
      boolAnswerRowFn("any_downsizing_allowance.summary", "Any downsizing allowance", routes.AnyDownsizingAllowanceController.onPageLoad),
    Constants.dateOfDisposalId ->
      dateAnswerRowFn("date_of_disposal.summary", "Date of disposal", routes.DateOfDisposalController.onPageLoad),
    Constants.valueOfDisposedPropertyId ->
      intAnswerRowFn("value_of_disposed_property.summary", "Vale of disposed property", routes.ValueOfDisposedPropertyController.onPageLoad),
    Constants.anyAssetsPassingToDirectDescendantsId ->
      boolAnswerRowFn("any_assets_passing_to_direct_descendants.summary",
        "Any assets passing to direct descentdants",
        routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad),
    Constants.anyBroughtForwardAllowanceId ->
      boolAnswerRowFn("any_brought_forward_allowance.summary", "Any brought forward allowance", routes.AnyBroughtForwardAllowanceController.onPageLoad),
    Constants.anyBroughtForwardAllowanceOnDisposalId ->
      boolAnswerRowFn("any_brought_forward_allowance_on_disposal.summary",
        "Any brought forward allowance on disposal",
        routes.AnyBroughtForwardAllowanceOnDisposalController.onPageLoad),
    Constants.anyExemptionId ->
      boolAnswerRowFn("any_exemption.summary", "Any exemption", routes.AnyExemptionController.onPageLoad),
    Constants.assetsPassingToDirectDescendantsId ->
      intAnswerRowFn("assets_passing_to_direct_descendants.summary",
        "Assets passing to direct descendants",
        routes.AssetsPassingToDirectDescendantsController.onPageLoad),
    Constants.broughtForwardAllowanceId ->
      intAnswerRowFn("brought_forward_allowance.summary", "Brought forward allowance", routes.BroughtForwardAllowanceController.onPageLoad),
    Constants.broughtForwardAllowanceOnDisposalId ->
      intAnswerRowFn("brought_forward_allowance_on_disposal.summary",
        "Brought forward allowance on disposal",
        routes.BroughtForwardAllowanceOnDisposalController.onPageLoad),
    Constants.percentageCloselyInheritedId ->
      percentAnswerRowFn("percentage_closely_inherited.summary", "Percentage closely inherited", routes.PercentageCloselyInheritedController.onPageLoad),
    Constants.propertyValueId ->
      intAnswerRowFn("property_value.summary", "Property value", routes.PropertyValueController.onPageLoad),
    Constants.propertyValueAfterExemptionId ->
      intAnswerRowFn("property_value_after_exemption.summary", "Property value after exemption", routes.PropertyValueAfterExemptionController.onPageLoad)
  )

  def constructAnswerRows(cacheMap: CacheMap,
                          answerRowFns: Map[String, JsValue => Messages => AnswerRow],
                          rowOrder: Map[String, Int],
                          request: Request[_]): Seq[AnswerRow] = {
    val messages = messagesApi.preferred(request)
    cacheMap.data.toSeq.sortWith { case ((key1, _), (key2, _)) => rowOrder(key1) < rowOrder(key2) }.map {
      case (key, value) => answerRowFns(key)(value)(messages)
    }
  }

  def onPageLoad = Action.async {
    implicit request => {
      sessionConnector.fetch.map {
        cacheMapOption => {
          cacheMapOption.fold(throw new RuntimeException("No cache unavailable")) {
            cacheMap =>
              Ok(check_answers(appConfig,
                constructAnswerRows(cacheMap, answerRowFns, rowOrder, request),
                navigator.nextPage(Constants.checkAnswersId)(cacheMap)))
          }
        }
      }
    }
  }
}
