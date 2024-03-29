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

package uk.gov.hmrc.residencenilratebandcalculator
import uk.gov.hmrc.residencenilratebandcalculator.models.RadioOption

import java.time.LocalDate

object Constants {
  val assetsPassingToDirectDescendantsId = "AssetsPassingToDirectDescendants"
  val transferAnyUnusedThresholdId = "TransferAnyUnusedThreshold"
  val transferAvailableWhenPropertyChangedId = "TransferAvailableWhenPropertyChanged"
  val claimDownsizingThresholdId = "ClaimDownsizingThreshold"
  val partOfEstatePassingToDirectDescendantsId = "PartOfEstatePassingToDirectDescendants"
  val exemptionsAndReliefClaimedId = "ExemptionsAndReliefClaimed"
  val propertyPassingToDirectDescendantsId = "PropertyPassingToDirectDescendants"
  val valueOfAssetsPassingId = "ValueOfAssetsPassing"
  val valueBeingTransferredId = "ValueBeingTransferred"
  val valueAvailableWhenPropertyChangedId = "ValueAvailableWhenPropertyChanged"
  val chargeableEstateValueId = "ChargeableEstateValue"
  val noDownsizingThresholdIncrease = "NoDownsizingThresholdIncrease"
  val noAdditionalThresholdAvailableId = "NoAdditionalThresholdAvailable"
  val dateOfDeathId = "DateOfDeath"
  val datePropertyWasChangedId = "DatePropertyWasChanged"
  val grossingUpOnEstatePropertyId = "GrossingUpOnEstateProperty"
  val grossingUpOnEstateAssetsId = "GrossingUpOnEstateAssets"
  val downsizingDetails = "downsizingDetails"
  val downsizingEligibilityDate: LocalDate = LocalDate.of(2015, 7, 8)
  val eligibilityDate: LocalDate = LocalDate.of(2017, 4, 6)
  val propertyInEstateId = "PropertyInEstate"
  val valueOfEstateId = "ValueOfEstate"
  val percentagePassedToDirectDescendantsId = "PercentagePassedToDirectDescendants"
  val chargeablePropertyValueId = "ChargeablePropertyValue"
  val chargeableInheritedPropertyValueId = "ChargeableInheritedPropertyValue"
  val propertyValueId = "PropertyValue"
  val valueOfChangedPropertyId = "ValueOfChangedProperty"
  val thresholdCalculationResultId = "ThresholdCalculationResult"
  val checkYourAnswersId = "CheckYourAnswers"

  val all = "all"
  val some = "some"
  val none = "none"
  val propertyPassingToDirectDescendantsOptions: Seq[RadioOption] = Seq(
    RadioOption("property_passing_to_direct_descendants", all),
    RadioOption("property_passing_to_direct_descendants", some),
    RadioOption("property_passing_to_direct_descendants", none)
  )

  val bigDecimal100: BigDecimal = BigDecimal(100)
  val bigDecimalZero: BigDecimal = BigDecimal(0)
  val intFour = 4
}
