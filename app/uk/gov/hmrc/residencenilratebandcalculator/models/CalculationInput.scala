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
                            percentagePassedToDirectDescendants: Int,
                            broughtForwardAllowance: Int,
                            propertyValueAfterExemption: Option[PropertyValueAfterExemption],
                            downsizingDetails: Option[DownsizingDetails])

object CalculationInput {
  implicit val formats: OFormat[CalculationInput] = Json.format[CalculationInput]

  def apply(userAnswers: UserAnswers): CalculationInput = {
    require(userAnswers.dateOfDeath.isDefined, "Date of Death was not answered")
    require(userAnswers.valueOfEstate.isDefined, "Value Of Estate was not answered")
    require(userAnswers.chargeableEstateValue.isDefined, "Chargeable Estate Value was not answered")
    require(userAnswers.propertyInEstate.isDefined, "Property In Estate was not answered")
    if (userAnswers.propertyInEstate.get) requirePropertyInEstateDependencies(userAnswers)
    require(userAnswers.anyBroughtForwardAllowance.isDefined, "Any Brought Forward Allowance was not answered")
    if (userAnswers.anyBroughtForwardAllowance.get) requireBroughtForwardAllowanceDependencies(userAnswers)
    require(userAnswers.claimDownsizingThreshold.isDefined, "Claim Downsizing Threshold was not answered")

    CalculationInput(
      userAnswers.dateOfDeath.get,
      userAnswers.valueOfEstate.get,
      userAnswers.chargeableEstateValue.get,
      getPropertyValue(userAnswers),
      getPercentagePassedToDirectDescendants(userAnswers),
      getBroughtForwardAllowance(userAnswers),
      getChargeablePropertyValue(userAnswers),
      getDownsizingDetails(userAnswers)
    )
  }

  def getChargeablePropertyValue(userAnswers: UserAnswers): Option[PropertyValueAfterExemption] =
    userAnswers.chargeablePropertyValue.isDefined match {
    case true => Some(PropertyValueAfterExemption(
      userAnswers.chargeablePropertyValue.get,
      userAnswers.chargeableInheritedPropertyValue.get
    ))
    case _ => None
  }

  private def getPropertyValue(userAnswers: UserAnswers) = userAnswers.propertyInEstate.get match {
    case true => userAnswers.propertyValue.get
    case _ => 0
  }

  private def getPercentagePassedToDirectDescendants(userAnswers: UserAnswers) = userAnswers.propertyInEstate.get match {
      case true if userAnswers.propertyPassingToDirectDescendants.get == Constants.all => 100
      case true if userAnswers.propertyPassingToDirectDescendants.get == Constants.some => userAnswers.percentagePassedToDirectDescendants.get
      case _ => 0
    }

  private def getBroughtForwardAllowance(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance.get match {
    case true => userAnswers.broughtForwardAllowance.get
    case _ => 0
  }

  private def getDownsizingDetails(userAnswers: UserAnswers) = userAnswers.claimDownsizingThreshold.get match {
    case true =>
      require(userAnswers.datePropertyWasChanged.isDefined, "Date Property Was Changed was not answered")

      userAnswers.datePropertyWasChanged match {
        case Some(d) if d isBefore Constants.downsizingEligibilityDate => None
        case Some(_) => Some(DownsizingDetails(userAnswers))
      }
    case _ => None
  }

  private def requirePropertyInEstateDependencies(userAnswers: UserAnswers) = {
    require(userAnswers.propertyValue.isDefined, "Property Value was not answered")
    require(userAnswers.propertyPassingToDirectDescendants.isDefined, "Property Passing To Direct Descendants was not answered")

    if(userAnswers.propertyPassingToDirectDescendants.get == Constants.some) {
      require(userAnswers.percentagePassedToDirectDescendants.isDefined, "Percentage Passed To Direct Descendants was not answered")
    }
    if (userAnswers.propertyPassingToDirectDescendants.get != Constants.none) requirePropertyPassingToDirectDescendantsDependencies(userAnswers)
  }

  private def requirePropertyPassingToDirectDescendantsDependencies(userAnswers: UserAnswers) = {
    require(userAnswers.exemptionsAndReliefClaimed.isDefined, "Exemptions And Relief Claimed was not answered")
    if(userAnswers.exemptionsAndReliefClaimed.get) requireExemptionsDependancies(userAnswers)
  }

  private def requireExemptionsDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.grossingUpOnEstateProperty.isDefined, "Grossing Up On Estate Property was not answered")
    if (!userAnswers.grossingUpOnEstateProperty.get) requireNoGrossingUpDependancies(userAnswers)
  }

  private def requireNoGrossingUpDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.chargeablePropertyValue.isDefined, "Chargeable Property Value was not answered")
    require(userAnswers.chargeableInheritedPropertyValue.isDefined, "Chargeable Inherited Property Value was not answered")
  }

  private def requireBroughtForwardAllowanceDependencies(userAnswers: UserAnswers) =
    require(userAnswers.broughtForwardAllowance.isDefined, "Brought Forward Allowance was not answered")
}

case class DownsizingDetails(datePropertyWasChanged: LocalDate,
                             valueOfDisposedProperty: Int,
                             valueCloselyInherited: Int,
                             broughtForwardAllowanceAtDisposal: Int)

object DownsizingDetails {
  implicit val formats: OFormat[DownsizingDetails] = Json.format[DownsizingDetails]

  def apply(userAnswers: UserAnswers): DownsizingDetails = {
    require(userAnswers.datePropertyWasChanged.isDefined, "Date Property Was Changed was not answered")
    require(userAnswers.valueOfDisposedProperty.isDefined, "Value of Disposed Property was not answered")
    require(userAnswers.anyAssetsPassingToDirectDescendants.isDefined, "Any Assets Passing to Direct Descendants was not answered")
    if (userAnswers.anyAssetsPassingToDirectDescendants.get) requireAssetsPassingToDirectDescendantsDependencies(userAnswers)

    DownsizingDetails(
      userAnswers.datePropertyWasChanged.get,
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

  private def requireAssetsPassingToDirectDescendantsDependencies(userAnswers: UserAnswers) = {
    require(userAnswers.assetsPassingToDirectDescendants.isDefined, "Assets Passing to Direct Descendants was not answered")
    if (userAnswers.anyBroughtForwardAllowance.get && !(userAnswers.datePropertyWasChanged.get isBefore Constants.eligibilityDate)) {
      requireAnyBroughtForwardAllowanceOnDisposalDependencies(userAnswers)
    }
  }

  private def requireAnyBroughtForwardAllowanceOnDisposalDependencies(userAnswers: UserAnswers) = {
    require(userAnswers.anyBroughtForwardAllowanceOnDisposal.isDefined, "Any Brought Forward Allowance on Disposal was not answered")
    if (userAnswers.anyBroughtForwardAllowanceOnDisposal.get) {
      require(userAnswers.broughtForwardAllowanceOnDisposal.isDefined, "Brought Forward Allowance on Disposal was not answered")
    }
  }
}
