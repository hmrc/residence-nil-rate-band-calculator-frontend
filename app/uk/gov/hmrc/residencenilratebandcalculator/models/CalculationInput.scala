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
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.residencenilratebandcalculator.Constants

case class CalculationInput(dateOfDeath: LocalDate,
                            valueOfEstate: Int,
                            chargeableEstateValue: Int,
                            propertyValue: Int,
                            percentageCloselyInherited: Int,
                            broughtForwardAllowance: Int,
                            propertyValueAfterExemption: Option[PropertyValueAfterExemption],
                            downsizingDetails: Option[DownsizingDetails])

object CalculationInput {
  implicit val formats: OFormat[CalculationInput] = Json.format[CalculationInput]

  def apply(userAnswers: UserAnswers): CalculationInput = {
    require(userAnswers.dateOfDeath.isDefined, "Date of Death was not answered")
    require(userAnswers.valueOfEstate.isDefined, "Value Of Estate was not answered")
    require(userAnswers.chargeableEstateValue.isDefined, "Chargeable Estate Value was not answered")
    require(userAnswers.estateHasProperty.isDefined, "Estate Has Property was not answered")
    if (userAnswers.estateHasProperty.get) requireEstateHasPropertyDependancies(userAnswers)
    require(userAnswers.anyBroughtForwardAllowance.isDefined, "Any Brought Forward Allowance was not answered")
    if (userAnswers.anyBroughtForwardAllowance.get) requireBroughtForwardAllowanceDependancies(userAnswers)
    require(userAnswers.anyDownsizingAllowance.isDefined, "Any Downsizing Allowance was not answered")

    CalculationInput(
      userAnswers.dateOfDeath.get,
      userAnswers.valueOfEstate.get,
      userAnswers.chargeableEstateValue.get,
      getPropertyValue(userAnswers),
      getPercentageCloselyInherited(userAnswers),
      getBroughtForwardAllowance(userAnswers),
      getChargeableValueOfResidence(userAnswers),
      getDownsizingDetails(userAnswers)
    )
  }

  def getChargeableValueOfResidence(userAnswers: UserAnswers): Option[PropertyValueAfterExemption] =
    userAnswers.chargeableValueOfResidence.isDefined match {
    case true => Some(PropertyValueAfterExemption(
      userAnswers.chargeableValueOfResidence.get,
      userAnswers.chargeableValueOfResidenceCloselyInherited.get
    ))
    case _ => None
  }

  private def getPropertyValue(userAnswers: UserAnswers) = userAnswers.estateHasProperty.get match {
    case true => userAnswers.propertyValue.get
    case _ => 0
  }

  private def getPercentageCloselyInherited(userAnswers: UserAnswers) = userAnswers.estateHasProperty.get match {
      case true if userAnswers.anyPropertyCloselyInherited.get == Constants.all => 100
      case true if userAnswers.anyPropertyCloselyInherited.get == Constants.some => userAnswers.percentageCloselyInherited.get
      case _ => 0
    }

  private def getBroughtForwardAllowance(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance.get match {
    case true => userAnswers.broughtForwardAllowance.get
    case _ => 0
  }

  private def getDownsizingDetails(userAnswers: UserAnswers) = userAnswers.anyDownsizingAllowance.get match {
    case true =>
      require(userAnswers.dateOfDisposal.isDefined, "Date of Disposal was not answered")

      userAnswers.dateOfDisposal match {
        case Some(d) if d isBefore Constants.downsizingEligibilityDate => None
        case Some(_) => Some(DownsizingDetails(userAnswers))
      }
    case _ => None
  }

  private def requireEstateHasPropertyDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.propertyValue.isDefined, "Property Value was not answered")
    require(userAnswers.anyPropertyCloselyInherited.isDefined, "Any Property Closely Inherited was not answered")

    if(userAnswers.anyPropertyCloselyInherited.get == Constants.some) {
      require(userAnswers.percentageCloselyInherited.isDefined, "Percentage Closely Inherited was not answered")
    }
    if (userAnswers.anyPropertyCloselyInherited.get != Constants.none) requirePropertyCloselyInheritedDependancies(userAnswers)
  }

  private def requirePropertyCloselyInheritedDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.anyExemption.isDefined, "Any Exemptions was not answered")
    if(userAnswers.anyExemption.get) requireExemptionsDependancies(userAnswers)
  }

  private def requireExemptionsDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.doesGrossingUpApplyToResidence.isDefined, "Does Grossing Up Apply to Residence was not answered")
    if (!userAnswers.doesGrossingUpApplyToResidence.get) requireNoGrossingUpDependancies(userAnswers)
  }

  private def requireNoGrossingUpDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.chargeableValueOfResidence.isDefined, "Chargeable Value of Residence was not answered")
    require(userAnswers.chargeableValueOfResidenceCloselyInherited.isDefined, "Chargeable Value of Residence Closely Inherited was not answered")
  }

  private def requireBroughtForwardAllowanceDependancies(userAnswers: UserAnswers) =
    require(userAnswers.broughtForwardAllowance.isDefined, "Brought Forward Allowance was not answered")
}

case class DownsizingDetails(dateOfDisposal: LocalDate,
                             valueOfDisposedProperty: Int,
                             valueCloselyInherited: Int,
                             broughtForwardAllowanceAtDisposal: Int)

object DownsizingDetails {
  implicit val formats: OFormat[DownsizingDetails] = Json.format[DownsizingDetails]

  def apply(userAnswers: UserAnswers): DownsizingDetails = {
    require(userAnswers.dateOfDisposal.isDefined, "Date of Disposal was not answered")
    require(userAnswers.valueOfDisposedProperty.isDefined, "Value of Disposed Property was not answered")
    require(userAnswers.anyAssetsPassingToDirectDescendants.isDefined, "Any Assets Passing to Direct Descendants was not answered")
    if (userAnswers.anyAssetsPassingToDirectDescendants.get) requireAssetsPassingToDirectDescendantsDependancies(userAnswers)

    DownsizingDetails(
      userAnswers.dateOfDisposal.get,
      userAnswers.valueOfDisposedProperty.get,
      getValueCloselyInherited(userAnswers),
      getBroughtForwardAllowanceOnDisposal(userAnswers))
  }

  private def getValueCloselyInherited(userAnswers: UserAnswers) = userAnswers.anyAssetsPassingToDirectDescendants.get match {
    case true => userAnswers.assetsPassingToDirectDescendants.get
    case _ => 0
  }

  private def getBroughtForwardAllowanceOnDisposal(userAnswers: UserAnswers) = userAnswers.anyAssetsPassingToDirectDescendants.get match {
    case true if userAnswers.anyBroughtForwardAllowanceOnDisposal.isDefined && userAnswers.anyBroughtForwardAllowanceOnDisposal.get =>
      userAnswers.broughtForwardAllowanceOnDisposal.get
    case _ => 0
  }

  private def requireAssetsPassingToDirectDescendantsDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.assetsPassingToDirectDescendants.isDefined, "Assets Passing to Direct Descendants was not answered")
    if (userAnswers.anyBroughtForwardAllowance.get && !(userAnswers.dateOfDisposal.get isBefore Constants.eligibilityDate))
      requireAnyBroughtForwardAllowanceOnDisposalDependancies(userAnswers)
  }

  private def requireAnyBroughtForwardAllowanceOnDisposalDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.anyBroughtForwardAllowanceOnDisposal.isDefined, "Any Brought Forward Allowance on Disposal was not answered")
    if (userAnswers.anyBroughtForwardAllowanceOnDisposal.get)
      require(userAnswers.broughtForwardAllowanceOnDisposal.isDefined, "Brought Forward Allowance on Disposal was not answered")
  }
}
