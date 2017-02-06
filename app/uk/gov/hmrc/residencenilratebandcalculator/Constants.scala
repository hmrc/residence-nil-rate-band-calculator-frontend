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
import uk.gov.hmrc.residencenilratebandcalculator.models.RadioOption

object Constants {
  val anyAssetsPassingToDirectDescendantsId = "AnyAssetsPassingToDirectDescendants"
  val anyBroughtForwardAllowanceId = "AnyBroughtForwardAllowance"
  val anyBroughtForwardAllowanceOnDisposalId = "AnyBroughtForwardAllowanceOnDisposal"
  val anyDownsizingAllowanceId = "AnyDownsizingAllowance"
  val anyExemptionId = "AnyExemption"
  val anyPropertyCloselyInheritedId = "AnyPropertyCloselyInherited"
  val assetsPassingToDirectDescendantsId = "AssetsPassingToDirectDescendants"
  val broughtForwardAllowanceId = "BroughtForwardAllowance"
  val broughtForwardAllowanceOnDisposalId = "BroughtForwardAllowanceOnDisposal"
  val chargeableTransferAmountId = "ChargeableTransferAmount"
  val checkAnswersId = "CheckAnswers"
  val dateOfDeathId = "DateOfDeath"
  val dateOfDisposalId = "DateOfDisposal"
  val downsizingDetails = "downsizingDetails"
  val downsizingEligibilityDate = new LocalDate(2015, 7, 8)
  val eligibilityDate = new LocalDate(2017, 4, 6)
  val estateHasPropertyId = "EstateHasProperty"
  val grossEstateValueId = "GrossEstateValue"
  val percentageCloselyInheritedId = "PercentageCloselyInherited"
  val propertyValueAfterExemptionId = "PropertyValueAfterExemption"
  val propertyValueId = "PropertyValue"
  val purposeOfUseId = "PurposeOfUse"
  val valueOfDisposedPropertyId = "ValueOfDisposedProperty"

  val jsonKeys = Map(
    broughtForwardAllowanceId -> "broughtForwardAllowance",
    chargeableTransferAmountId -> "chargeableTransferAmount",
    dateOfDeathId -> "dateOfDeath",
    grossEstateValueId -> "grossEstateValue",
    propertyValueId -> "propertyValue",
    percentageCloselyInheritedId -> "percentageCloselyInherited",
    propertyValueAfterExemptionId -> "propertyValueAfterExemption"
  )

  val downsizingKeys = Map(
    dateOfDisposalId -> "dateOfDisposal",
    valueOfDisposedPropertyId -> "valueOfDisposedProperty",
    assetsPassingToDirectDescendantsId -> "valueCloselyInherited",
    broughtForwardAllowanceOnDisposalId -> "broughtForwardAllowanceAtDisposal"
  )

  val planning = "planning"
  val dealingWithEstate = "dealing_with_estate"
  val purposeOfUseOptions = Seq(
    RadioOption("purpose_of_use", dealingWithEstate),
    RadioOption("purpose_of_use", planning)
  )
}
