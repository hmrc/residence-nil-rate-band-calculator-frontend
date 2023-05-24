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
import play.api.libs.json.{JodaReads, Json, OFormat}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class CalculationInput(dateOfDeath: LocalDate,
                            valueOfEstate: Int,
                            chargeableEstateValue: Int,
                            propertyValue: Int,
                            percentagePassedToDirectDescendants: BigDecimal,
                            valueBeingTransferred: Int,
                            propertyValueAfterExemption: Option[PropertyValueAfterExemption],
                            downsizingDetails: Option[DownsizingDetails])

object CalculationInput extends JodaReads {
  implicit val formats: OFormat[CalculationInput] = Json.format[CalculationInput]

  def apply(userAnswers: UserAnswers): CalculationInput = {
    require(userAnswers.dateOfDeath.isDefined, "Date of Death was not answered")
    require(userAnswers.valueOfEstate.isDefined, "Value Of Estate was not answered")
    require(userAnswers.chargeableEstateValue.isDefined, "Chargeable Estate Value was not answered")
    require(userAnswers.propertyInEstate.isDefined, "Property In Estate was not answered")
    if (userAnswers.propertyInEstate.get) requirePropertyInEstateDependencies(userAnswers)
    require(userAnswers.transferAnyUnusedThreshold.isDefined, "Transfer Any Unused Allowance was not answered")
    if (userAnswers.transferAnyUnusedThreshold.get) requireValueBeingTransferredDependencies(userAnswers)
    require(userAnswers.claimDownsizingThreshold.isDefined, "Claim Downsizing Threshold was not answered")

    CalculationInput(
      userAnswers.dateOfDeath.get,
      userAnswers.valueOfEstate.get,
      userAnswers.chargeableEstateValue.get,
      getPropertyValue(userAnswers),
      userAnswers.getPercentagePassedToDirectDescendants,
      getValueBeingTransferred(userAnswers),
      getChargeablePropertyValue(userAnswers),
      getDownsizingDetails(userAnswers)
    )
  }

  def getChargeablePropertyValue(userAnswers: UserAnswers): Option[PropertyValueAfterExemption] =
    if (userAnswers.chargeablePropertyValue.isDefined) {
      Some(PropertyValueAfterExemption(
        userAnswers.chargeablePropertyValue.get,
        userAnswers.chargeableInheritedPropertyValue.get
      ))
    } else {
      None
    }

  private def getPropertyValue(userAnswers: UserAnswers) = if (userAnswers.propertyInEstate.get) {
    userAnswers.propertyValue.get
  } else {
    0
  }

  private def getValueBeingTransferred(userAnswers: UserAnswers) = if (userAnswers.transferAnyUnusedThreshold.get) {
    userAnswers.valueBeingTransferred.get
  } else {
    0
  }

  private def getDownsizingDetails(userAnswers: UserAnswers) = if (userAnswers.claimDownsizingThreshold.get) {
    require(userAnswers.datePropertyWasChanged.isDefined, "Date Property Was Changed was not answered")

    userAnswers.datePropertyWasChanged match {
      case Some(d) if d isBefore Constants.downsizingEligibilityDate => None
      case Some(_) => Some(DownsizingDetails(userAnswers))
      case _ => None
    }
  } else {
    None
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

  private def requireValueBeingTransferredDependencies(userAnswers: UserAnswers) =
    require(userAnswers.valueBeingTransferred.isDefined, "Value Being Transferred was not answered")
}

case class DownsizingDetails(datePropertyWasChanged: LocalDate,
                             valueOfChangedProperty: Int,
                             valueOfAssetsPassing: Int,
                             valueAvailableWhenPropertyChanged: Int)

object DownsizingDetails {
  implicit val formats: OFormat[DownsizingDetails] = Json.format[DownsizingDetails]

  def apply(userAnswers: UserAnswers): DownsizingDetails = {
    require(userAnswers.datePropertyWasChanged.isDefined, "Date Property Was Changed was not answered")
    require(userAnswers.valueOfChangedProperty.isDefined, "Value Of Changed Property was not answered")
    require(userAnswers.assetsPassingToDirectDescendants.isDefined, "Assets Passing To Direct Descendants was not answered")
    if (userAnswers.assetsPassingToDirectDescendants.get) requireValueOfAssetsPassingDependancies(userAnswers)

    DownsizingDetails(
      userAnswers.datePropertyWasChanged.get,
      userAnswers.valueOfChangedProperty.get,
      getValueOfAssetsPassing(userAnswers),
      getValueAvailableWhenPropertyChanged(userAnswers))
  }

  private def getValueOfAssetsPassing(userAnswers: UserAnswers) = userAnswers.assetsPassingToDirectDescendants.get match {
    case true => userAnswers.valueOfAssetsPassing.get
    case _ => 0
  }

  private def getValueAvailableWhenPropertyChanged(userAnswers: UserAnswers) = userAnswers.assetsPassingToDirectDescendants.get match {
    case true if userAnswers.transferAvailableWhenPropertyChanged.isDefined && userAnswers.transferAvailableWhenPropertyChanged.get =>
      userAnswers.valueAvailableWhenPropertyChanged.get
    case _ => 0
  }

  private def requireValueOfAssetsPassingDependancies(userAnswers: UserAnswers) = {
    require(userAnswers.valueOfAssetsPassing.isDefined, "Value Of Assets Passing was not answered")
    if (userAnswers.transferAnyUnusedThreshold.get && !(userAnswers.datePropertyWasChanged.get isBefore Constants.eligibilityDate)) {
      requireTransferAvailableWhenPropertyChangedDependencies(userAnswers)
    }
  }

  private def requireTransferAvailableWhenPropertyChangedDependencies(userAnswers: UserAnswers) = {
    require(userAnswers.transferAvailableWhenPropertyChanged.isDefined, "Transfer Available When Property Changed was not answered")
    if (userAnswers.transferAvailableWhenPropertyChanged.get) {
      require(userAnswers.valueAvailableWhenPropertyChanged.isDefined, "Value Available When Property Changed was not answered")
    }
  }
}
