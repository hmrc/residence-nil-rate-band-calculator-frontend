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

  val rowOrderList = List[String](
    Constants.dateOfDeathId,
    Constants.partOfEstatePassingToDirectDescendantsId,
    Constants.valueOfEstateId,
    Constants.chargeableEstateValueId,
    Constants.propertyInEstateId,
    Constants.propertyValueId,
    Constants.propertyPassingToDirectDescendantsId,
    Constants.percentageCloselyInheritedId,
    Constants.anyExemptionId,
    Constants.doesGrossingUpApplyToResidenceId,
    Constants.chargeableValueOfResidenceId,
    Constants.chargeableValueOfResidenceCloselyInheritedId,
    Constants.anyBroughtForwardAllowanceId,
    Constants.broughtForwardAllowanceId,
    Constants.anyDownsizingAllowanceId,
    Constants.dateOfDisposalId,
    Constants.valueOfDisposedPropertyId,
    Constants.anyAssetsPassingToDirectDescendantsId,
    Constants.doesGrossingUpApplyToOtherPropertyId,
    Constants.assetsPassingToDirectDescendantsId,
    Constants.anyBroughtForwardAllowanceOnDisposalId,
    Constants.broughtForwardAllowanceOnDisposalId
  )

  val rowOrder = rowOrderList.zipWithIndex.toMap

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

  def propertyPassingToDirectDescendantsAnswerRowFn(titleKey: String, title: String, url: () => Call) =
    (jsValue: JsValue) => Json.fromJson[String](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, s"property_passing_to_direct_descendants.$value", url()) _
    })

  val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
    Constants.dateOfDeathId ->
      dateAnswerRowFn("date_of_death.title", "Date of death", routes.DateOfDeathController.onPageLoad),
    Constants.partOfEstatePassingToDirectDescendantsId ->
      boolAnswerRowFn("part_of_estate_passing_to_direct_descendants.title",
        Constants.partOfEstatePassingToDirectDescendantsId,
        routes.PartOfEstatePassingToDirectDescendantsController.onPageLoad),
    Constants.valueOfEstateId ->
      intAnswerRowFn("value_of_estate.title", Constants.valueOfEstateId, routes.ValueOfEstateController.onPageLoad),
    Constants.chargeableEstateValueId ->
      intAnswerRowFn("chargeable_estate_value.title", "Chargeable transfer amount", routes.ChargeableEstateValueController.onPageLoad),
    Constants.propertyInEstateId ->
      boolAnswerRowFn("property_in_estate.title", Constants.propertyInEstateId, routes.PropertyInEstateController.onPageLoad),
    Constants.anyDownsizingAllowanceId ->
      boolAnswerRowFn("any_downsizing_allowance.title", "Any downsizing allowance", routes.AnyDownsizingAllowanceController.onPageLoad),
    Constants.dateOfDisposalId ->
      dateAnswerRowFn("date_of_disposal.title", "Date of disposal", routes.DateOfDisposalController.onPageLoad),
    Constants.valueOfDisposedPropertyId ->
      intAnswerRowFn("value_of_disposed_property.title", "Vale of disposed property", routes.ValueOfDisposedPropertyController.onPageLoad),
    Constants.anyAssetsPassingToDirectDescendantsId ->
      boolAnswerRowFn("any_assets_passing_to_direct_descendants.title",
        "Any assets passing to direct descendants",
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
    Constants.propertyPassingToDirectDescendantsId ->
      propertyPassingToDirectDescendantsAnswerRowFn("property_passing_to_direct_descendants.title",
        Constants.propertyPassingToDirectDescendantsId, routes.PropertyPassingToDirectDescendantsController.onPageLoad),
    Constants.doesGrossingUpApplyToOtherPropertyId ->
      boolAnswerRowFn("does_grossing_up_apply_to_other_property.title",
        "Does grossing up apply to other property",
        routes.DoesGrossingUpApplyToOtherPropertyController.onPageLoad),
    Constants.doesGrossingUpApplyToResidenceId ->
      boolAnswerRowFn("does_grossing_up_apply_to_residence.title",
        "Does grossing up apply to residence",
        routes.DoesGrossingUpApplyToResidenceController.onPageLoad),
    Constants.percentageCloselyInheritedId ->
      percentAnswerRowFn("percentage_closely_inherited.title", "Percentage closely inherited", routes.PercentageCloselyInheritedController.onPageLoad),
    Constants.propertyValueId ->
      intAnswerRowFn("property_value.title", "Property value", routes.PropertyValueController.onPageLoad),
    Constants.chargeableValueOfResidenceId ->
      intAnswerRowFn("chargeable_value_of_residence.title",
        "Chargeable value of residence",
        routes.ChargeableValueOfResidenceController.onPageLoad),
    Constants.chargeableValueOfResidenceCloselyInheritedId ->
      intAnswerRowFn("chargeable_value_of_residence_closely_inherited.title",
      "Chargeable value of residence closely inherited",
      routes.ChargeableValueOfResidenceCloselyInheritedController.onPageLoad)
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

  def truncateAndLocateInCacheMap(id: String, cacheMap: CacheMap): CacheMap = {
    val truncatedList = rowOrderList.takeWhile(_ != id)
    CacheMap(cacheMap.id, cacheMap.data.filterKeys(x => truncatedList.contains(x)))
  }

  def truncateAndAddCurrentLocateInCacheMap(id: String, cacheMap: CacheMap) = {
    val truncatedList = rowOrderList.takeWhile(_ != id) :+ id
    CacheMap(cacheMap.id, cacheMap.data.filterKeys(x => truncatedList.contains(x)))
  }


  def apply(cacheMap: CacheMap, messages: Messages) = constructAnswerRows(cacheMap, answerRowFns, rowOrder, messages)
}
