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
  val partOfEstatePassingToDirectDescendantsId = "PartOfEstatePassingToDirectDescendants"
  val anyExemptionId = "AnyExemption"
  val propertyPassingToDirectDescendantsId = "PropertyPassingToDirectDescendants"
  val assetsPassingToDirectDescendantsId = "AssetsPassingToDirectDescendants"
  val broughtForwardAllowanceId = "BroughtForwardAllowance"
  val broughtForwardAllowanceOnDisposalId = "BroughtForwardAllowanceOnDisposal"
  val chargeableEstateValueId = "ChargeableEstateValue"
  val cannotClaimDownsizingId = "CannotClaimDownsizing"
  val cannotClaimRNRB = "CannotClaimRNRB"
  val dateOfDeathId = "DateOfDeath"
  val dateOfDisposalId = "DateOfDisposal"
  val doesGrossingUpApplyToResidenceId = "DoesGrossingUpApplyToResidence"
  val doesGrossingUpApplyToOtherPropertyId = "DoesGrossingUpApplyToOtherProperty"
  val downsizingDetails = "downsizingDetails"
  val downsizingEligibilityDate = new LocalDate(2015, 7, 8)
  val eligibilityDate = new LocalDate(2017, 4, 6)
  val propertyInEstateId = "PropertyInEstate"
  val valueOfEstateId = "ValueOfEstate"
  val percentagePassedToDirectDescendantsId = "PercentagePassedToDirectDescendants"
  val chargeableValueOfResidenceId = "ChargeableValueOfResidence"
  val chargeableValueOfResidenceCloselyInheritedId = "ChargeableValueOfResidenceCloselyInherited"
  val propertyValueId = "PropertyValue"
  val valueOfDisposedPropertyId = "ValueOfDisposedProperty"

  val all = "all"
  val some = "some"
  val none = "none"
  val propertyPassingToDirectDescendantsOptions = Seq(
    RadioOption("property_passing_to_direct_descendants", all),
    RadioOption("property_passing_to_direct_descendants", some),
    RadioOption("property_passing_to_direct_descendants", none)
  )
}
