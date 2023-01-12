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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.joda.time.LocalDate
import play.api.i18n.Messages
import play.api.libs.json.{JodaReads, JsValue, Json}
import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes

object AnswerRows extends JodaReads {

  val rowOrderList = List[String](
    Constants.dateOfDeathId,
    Constants.partOfEstatePassingToDirectDescendantsId,
    Constants.valueOfEstateId,
    Constants.chargeableEstateValueId,
    Constants.propertyInEstateId,
    Constants.propertyValueId,
    Constants.propertyPassingToDirectDescendantsId,
    Constants.percentagePassedToDirectDescendantsId,
    Constants.exemptionsAndReliefClaimedId,
    Constants.grossingUpOnEstatePropertyId,
    Constants.chargeablePropertyValueId,
    Constants.chargeableInheritedPropertyValueId,
    Constants.transferAnyUnusedThresholdId,
    Constants.valueBeingTransferredId,
    Constants.claimDownsizingThresholdId,
    Constants.datePropertyWasChangedId,
    Constants.valueOfChangedPropertyId,
    Constants.assetsPassingToDirectDescendantsId,
    Constants.grossingUpOnEstateAssetsId,
    Constants.valueOfAssetsPassingId,
    Constants.transferAvailableWhenPropertyChangedId,
    Constants.valueAvailableWhenPropertyChangedId
  )

  val rowOrder = rowOrderList.zipWithIndex.toMap

  private def errorString(title: String) = s"$title unavailable from cache"

  def intAnswerRowFn(titleKey: String, title: String, url: Call) =
    (jsValue: JsValue) => Json.fromJson[Int](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url) _
    })

  def boolAnswerRowFn(titleKey: String, title: String, url: Call) =
    (jsValue: JsValue) => Json.fromJson[Boolean](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url) _
    })

  def dateAnswerRowFn(titleKey: String, title: String, url: Call) =
    (jsValue: JsValue) => Json.fromJson[LocalDate](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url) _
    })

  def percentAnswerRowFn(titleKey: String, title: String, url: Call) =
    (jsValue: JsValue) => Json.fromJson[Double](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, value, url) _
    })

  def propertyPassingToDirectDescendantsAnswerRowFn(titleKey: String, title: String, url: Call) =
    (jsValue: JsValue) => Json.fromJson[String](jsValue).fold(_ => throw new RuntimeException(errorString(title)), {
      value => AnswerRow(titleKey, s"property_passing_to_direct_descendants.$value", url) _
    })

  val answerRowFns = Map[String, JsValue => Messages => AnswerRow](
    Constants.dateOfDeathId ->
      dateAnswerRowFn("check_your_answers.date_of_death.title", "Date of death", routes.DateOfDeathController.onPageLoad),
    Constants.partOfEstatePassingToDirectDescendantsId ->
      boolAnswerRowFn("check_your_answers.part_of_estate_passing_to_direct_descendants.title",
        Constants.partOfEstatePassingToDirectDescendantsId,
        routes.PartOfEstatePassingToDirectDescendantsController.onPageLoad),
    Constants.valueOfEstateId ->
      intAnswerRowFn("check_your_answers.value_of_estate.title", Constants.valueOfEstateId, routes.ValueOfEstateController.onPageLoad),
    Constants.chargeableEstateValueId ->
      intAnswerRowFn("check_your_answers.chargeable_estate_value.title", "Chargeable transfer amount", routes.ChargeableEstateValueController.onPageLoad),
    Constants.propertyInEstateId ->
      boolAnswerRowFn("check_your_answers.property_in_estate.title", Constants.propertyInEstateId, routes.PropertyInEstateController.onPageLoad),
    Constants.claimDownsizingThresholdId ->
      boolAnswerRowFn("check_your_answers.claim_downsizing_threshold.title", Constants.claimDownsizingThresholdId,
        routes.ClaimDownsizingThresholdController.onPageLoad),
    Constants.datePropertyWasChangedId ->
      dateAnswerRowFn("check_your_answers.date_property_was_changed.title", Constants.datePropertyWasChangedId,
        routes.DatePropertyWasChangedController.onPageLoad),
    Constants.valueOfChangedPropertyId ->
      intAnswerRowFn("check_your_answers.value_of_changed_property.title", Constants.valueOfChangedPropertyId,
        routes.ValueOfChangedPropertyController.onPageLoad),
    Constants.assetsPassingToDirectDescendantsId ->
      boolAnswerRowFn("check_your_answers.assets_passing_to_direct_descendants.title",
        Constants.assetsPassingToDirectDescendantsId,
        routes.AssetsPassingToDirectDescendantsController.onPageLoad),
    Constants.transferAnyUnusedThresholdId ->
      boolAnswerRowFn("check_your_answers.transfer_any_unused_threshold.title", Constants.transferAnyUnusedThresholdId,
        routes.TransferAnyUnusedThresholdController.onPageLoad),
    Constants.transferAvailableWhenPropertyChangedId ->
      boolAnswerRowFn("check_your_answers.transfer_available_when_property_changed.title",
        Constants.transferAvailableWhenPropertyChangedId,
        routes.TransferAvailableWhenPropertyChangedController.onPageLoad),
    Constants.exemptionsAndReliefClaimedId ->
      boolAnswerRowFn("check_your_answers.exemptions_and_relief_claimed.title", Constants.exemptionsAndReliefClaimedId,
        routes.ExemptionsAndReliefClaimedController.onPageLoad),
    Constants.valueOfAssetsPassingId ->
      intAnswerRowFn("check_your_answers.value_of_assets_passing.title",
        Constants.valueOfAssetsPassingId,
        routes.ValueOfAssetsPassingController.onPageLoad),
    Constants.valueBeingTransferredId ->
      intAnswerRowFn("check_your_answers.value_being_transferred.title", Constants.valueBeingTransferredId,
        routes.ValueBeingTransferredController.onPageLoad),
    Constants.valueAvailableWhenPropertyChangedId ->
      intAnswerRowFn("check_your_answers.value_available_when_property_changed.title",
        Constants.valueAvailableWhenPropertyChangedId,
        routes.ValueAvailableWhenPropertyChangedController.onPageLoad),
    Constants.propertyPassingToDirectDescendantsId ->
      propertyPassingToDirectDescendantsAnswerRowFn("check_your_answers.property_passing_to_direct_descendants.title",
        Constants.propertyPassingToDirectDescendantsId, routes.PropertyPassingToDirectDescendantsController.onPageLoad),
    Constants.grossingUpOnEstateAssetsId ->
      boolAnswerRowFn("check_your_answers.grossing_up_on_estate_assets.title",
        Constants.grossingUpOnEstateAssetsId,
        routes.GrossingUpOnEstateAssetsController.onPageLoad),
    Constants.grossingUpOnEstatePropertyId ->
      boolAnswerRowFn("check_your_answers.grossing_up_on_estate_property.title",
        Constants.grossingUpOnEstatePropertyId,
        routes.GrossingUpOnEstatePropertyController.onPageLoad),
    Constants.percentagePassedToDirectDescendantsId ->
      percentAnswerRowFn("check_your_answers.percentage_passed_to_direct_descendants.title", Constants.percentagePassedToDirectDescendantsId,
        routes.PercentagePassedToDirectDescendantsController.onPageLoad),
    Constants.propertyValueId ->
      intAnswerRowFn("check_your_answers.property_value.title", "Property value", routes.PropertyValueController.onPageLoad),
    Constants.chargeablePropertyValueId ->
      intAnswerRowFn("check_your_answers.chargeable_property_value.title",
        Constants.chargeablePropertyValueId,
        routes.ChargeablePropertyValueController.onPageLoad),
    Constants.chargeableInheritedPropertyValueId ->
      intAnswerRowFn("check_your_answers.chargeable_inherited_property_value.title",
        Constants.chargeableInheritedPropertyValueId,
      routes.ChargeableInheritedPropertyValueController.onPageLoad)
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
