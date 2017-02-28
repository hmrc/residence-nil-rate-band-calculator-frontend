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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes

object AnswerRows {
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
    Constants.anyPropertyCloselyInheritedId -> 118,
    Constants.percentageCloselyInheritedId -> 106,
    Constants.propertyValueId -> 105,
    Constants.propertyValueAfterExemptionId -> 108
  )

  private def errorString(title: String) = s"$title unavailable from cache"

  def intAnswerRowFn(titleKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[Int](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url()) _
    })

  def boolAnswerRowFn(titleKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[Boolean](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url()) _
    })

  def dateAnswerRowFn(titleKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[LocalDate](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url()) _
    })

  def percentAnswerRowFn(titleKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[Double](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url()) _
    })

  def propertyValueAfterExemptionAnswerRowFn(titleKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[PropertyValueAfterExemption](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url()) _
    })

  val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
    Constants.dateOfDeathId ->
      dateAnswerRowFn("date_of_death.title", "Date of death", routes.DateOfDeathController.onPageLoad),
    Constants.grossEstateValueId ->
      intAnswerRowFn("gross_estate_value.title", "Gross estate value", routes.GrossEstateValueController.onPageLoad),
    Constants.chargeableTransferAmountId ->
      intAnswerRowFn("chargeable_transfer_amount.title", "Chargeable transfer amount", routes.ChargeableTransferAmountController.onPageLoad),
    Constants.estateHasPropertyId ->
      boolAnswerRowFn("estate_has_property.title", "Estate has property", routes.EstateHasPropertyController.onPageLoad),
    Constants.anyDownsizingAllowanceId ->
      boolAnswerRowFn("any_downsizing_allowance.title", "Any downsizing allowance", routes.AnyDownsizingAllowanceController.onPageLoad),
    Constants.dateOfDisposalId ->
      dateAnswerRowFn("date_of_disposal.title", "Date of disposal", routes.DateOfDisposalController.onPageLoad),
    Constants.valueOfDisposedPropertyId ->
      intAnswerRowFn("value_of_disposed_property.title", "Vale of disposed property", routes.ValueOfDisposedPropertyController.onPageLoad),
    Constants.anyAssetsPassingToDirectDescendantsId ->
      boolAnswerRowFn("any_assets_passing_to_direct_descendants.title",
        "Any assets passing to direct descentdants",
        routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad),
    Constants.anyBroughtForwardAllowanceId ->
      boolAnswerRowFn("any_brought_forward_allowance.title", "Any brought forward allowance", routes.AnyBroughtForwardAllowanceController.onPageLoad),
    Constants.anyBroughtForwardAllowanceOnDisposalId ->
      boolAnswerRowFn("any_brought_forward_allowance_on_disposal.title",
        "Any brought forward allowance on disposal",
        routes.AnyBroughtForwardAllowanceOnDisposalController.onPageLoad),
    Constants.anyExemptionId ->
      boolAnswerRowFn("any_exemption.title", "Any exemption", routes.AnyExemptionController.onPageLoad),
    Constants.assetsPassingToDirectDescendantsId ->
      intAnswerRowFn("assets_passing_to_direct_descendants.title",
        "Assets passing to direct descendants",
        routes.AssetsPassingToDirectDescendantsController.onPageLoad),
    Constants.broughtForwardAllowanceId ->
      intAnswerRowFn("brought_forward_allowance.title", "Brought forward allowance", routes.BroughtForwardAllowanceController.onPageLoad),
    Constants.broughtForwardAllowanceOnDisposalId ->
      intAnswerRowFn("brought_forward_allowance_on_disposal.title",
        "Brought forward allowance on disposal",
        routes.BroughtForwardAllowanceOnDisposalController.onPageLoad),
    Constants.anyPropertyCloselyInheritedId ->
      boolAnswerRowFn("any_property_closely_inherited.title", "Any property closely inherited", routes.AnyPropertyCloselyInheritedController.onPageLoad),
    Constants.percentageCloselyInheritedId ->
      percentAnswerRowFn("percentage_closely_inherited.title", "Percentage closely inherited", routes.PercentageCloselyInheritedController.onPageLoad),
    Constants.propertyValueId ->
      intAnswerRowFn("property_value.title", "Property value", routes.PropertyValueController.onPageLoad),
    Constants.propertyValueAfterExemptionId ->
      propertyValueAfterExemptionAnswerRowFn("property_value_after_exemption.title",
        "Property value after exemption",
        routes.PropertyValueAfterExemptionController.onPageLoad)
  )

  def constructAnswerRows(cacheMap: CacheMap,
                          answerRowFns: Map[String, JsValue => Messages => AnswerRow],
                          rowOrder: Map[String, Int],
                          messages: Messages): Seq[AnswerRow] = {
    val dataToInclude = cacheMap.data.filterKeys(key => answerRowFns.keys.toSeq contains key).toSeq
    dataToInclude.sortWith { case ((key1, _), (key2, _)) => rowOrder(key1) < rowOrder(key2) }.map {
      case (key, value) => answerRowFns(key)(value)(messages)
    }
  }

  def apply(cacheMap: CacheMap, messages: Messages) = constructAnswerRows(cacheMap, answerRowFns, rowOrder, messages)
}
