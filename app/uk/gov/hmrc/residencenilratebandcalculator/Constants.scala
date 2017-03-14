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

package uk.gov.hmrc.residencenilratebandcalculator

import org.joda.time.LocalDate
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.models.RadioOption

object Constants {
  val anyAssetsPassingToDirectDescendantsId = "AnyAssetsPassingToDirectDescendants"
  val anyBroughtForwardAllowanceId = "AnyBroughtForwardAllowance"
  val anyBroughtForwardAllowanceOnDisposalId = "AnyBroughtForwardAllowanceOnDisposal"
  val anyDownsizingAllowanceId = "AnyDownsizingAllowance"
  val anyEstatePassedToDescendantsId = "AnyEstatePassedToDescendants"
  val anyExemptionId = "AnyExemption"
  val anyPropertyCloselyInheritedId = "AnyPropertyCloselyInherited"
  val assetsPassingToDirectDescendantsId = "AssetsPassingToDirectDescendants"
  val broughtForwardAllowanceId = "BroughtForwardAllowance"
  val broughtForwardAllowanceOnDisposalId = "BroughtForwardAllowanceOnDisposal"
  val chargeableTransferAmountId = "ChargeableTransferAmount"
  val checkAnswersId = "CheckAnswers"
  val cannotClaimRNRB = "CannotClaimRNRB"
  val dateOfDeathId = "DateOfDeath"
  val dateOfDisposalId = "DateOfDisposal"
  val doesGrossingUpApplyToResidenceId = "DoesGrossingUpApplyToResidence"
  val doesGrossingUpApplyToOtherPropertyId = "DoesGrossingUpApplyToOtherProperty"
  val downsizingDetails = "downsizingDetails"
  val downsizingEligibilityDate = new LocalDate(2015, 7, 8)
  val eligibilityDate = new LocalDate(2017, 4, 6)
  val estateHasPropertyId = "EstateHasProperty"
  val grossEstateValueId = "GrossEstateValue"
  val percentageCloselyInheritedId = "PercentageCloselyInherited"
  val chargeableValueOfResidenceId = "ChargeableValueOfResidence"
  val chargeableValueOfResidenceCloselyInheritedId = "ChargeableValueOfResidenceCloselyInherited"
  val propertyValueId = "PropertyValue"
  val valueOfDisposedPropertyId = "ValueOfDisposedProperty"

  val jsonKeys = Map(
    broughtForwardAllowanceId -> "broughtForwardAllowance",
    chargeableTransferAmountId -> "chargeableTransferAmount",
    dateOfDeathId -> "dateOfDeath",
    grossEstateValueId -> "grossEstateValue",
    propertyValueId -> "propertyValue",
    percentageCloselyInheritedId -> "percentageCloselyInherited",
    chargeableValueOfResidenceId -> "chargeableValueOfResidence",
    chargeableValueOfResidenceCloselyInheritedId -> "chargeableValueOfResidenceCloselyInherited"
  )

  val downsizingKeys = Map(
    dateOfDisposalId -> "dateOfDisposal",
    valueOfDisposedPropertyId -> "valueOfDisposedProperty",
    assetsPassingToDirectDescendantsId -> "valueCloselyInherited",
    broughtForwardAllowanceOnDisposalId -> "broughtForwardAllowanceAtDisposal"
  )

  val all = "all"
  val some = "some"
  val none = "none"
  val anyPropertyCloselyInheritedOptions = Seq(
    RadioOption("any_property_closely_inherited", all),
    RadioOption("any_property_closely_inherited", some),
    RadioOption("any_property_closely_inherited", none)
  )
}
