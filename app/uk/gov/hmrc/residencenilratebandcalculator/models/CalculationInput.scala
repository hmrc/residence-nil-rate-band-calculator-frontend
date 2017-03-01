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

case class CalculationInput(dateOfDeath: LocalDate,
                            grossEstateValue: Int,
                            chargeableTransferAmount: Int,
                            propertyValue: Int,
                            percentageCloselyInherited: Int,
                            broughtForwardAllowance: Int,
                            propertyValueAfterExemption: Option[PropertyValueAfterExemption],
                            downsizingDetails: Option[DownsizingDetails])

object CalculationInput {
  implicit val formats: OFormat[CalculationInput] = Json.format[CalculationInput]

  // TODO: add some requires that check propertyValueAfterExemption

  def apply(userAnswers: UserAnswers): CalculationInput = {
    require(userAnswers.dateOfDeath.isDefined, "Date of Death was not answered")
    require(userAnswers.grossEstateValue.isDefined, "Gross Estate Value was not answered")
    require(userAnswers.chargeableTransferAmount.isDefined, "Chargeable Transfer Amount was not answered")
    require(userAnswers.estateHasProperty.isDefined, "Estate Has Property was not answered")
    require(!userAnswers.estateHasProperty.get || userAnswers.propertyValue.isDefined, "Property Value was not answered")
    require(!userAnswers.estateHasProperty.get || userAnswers.anyPropertyCloselyInherited.isDefined, "Any Property Closely Inherited was not answered")
    require(userAnswers.anyPropertyCloselyInherited.isEmpty ||
      !userAnswers.anyPropertyCloselyInherited.get ||
      userAnswers.percentageCloselyInherited.isDefined,
      "Percentage Closely Inherited was not answered")
    require(userAnswers.anyBroughtForwardAllowance.isDefined, "Any Brought Forward Allowance was not answered")
    require(!userAnswers.anyBroughtForwardAllowance.get || userAnswers.broughtForwardAllowance.isDefined, "Brought Forward Allowance was not answered")
    require(userAnswers.anyDownsizingAllowance.isDefined, "Any Downsizing Allowance was not answered")

    CalculationInput(
      userAnswers.dateOfDeath.get,
      userAnswers.grossEstateValue.get,
      userAnswers.chargeableTransferAmount.get,
      getPropertyValue(userAnswers),
      getPercentageCloselyInherited(userAnswers),
      getBroughtForwardAllowance(userAnswers),
      getPropertyValueAfterExemption(userAnswers),
      getDownsizingDetails(userAnswers)
    )
  }

  // TODO: Amend what is matched against to refer to the previous page

  def getPropertyValueAfterExemption(userAnswers: UserAnswers): Option[PropertyValueAfterExemption] =
    userAnswers.propertyValueAfterExemption.isDefined match {
    case true => Some(PropertyValueAfterExemption(
      userAnswers.propertyValueAfterExemption.get,
      userAnswers.propertyValueAfterExemptionCloselyInherited.get
    ))
    case _ => None
  }

  private def getPropertyValue(userAnswers: UserAnswers) = userAnswers.estateHasProperty.get match {
    case true => userAnswers.propertyValue.get
    case _ => 0
  }

  private def getPercentageCloselyInherited(userAnswers: UserAnswers) = userAnswers.estateHasProperty.get match {
      case true if userAnswers.anyPropertyCloselyInherited.get => userAnswers.percentageCloselyInherited.get
      case _ => 0
    }

  private def getBroughtForwardAllowance(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance.get match {
    case true => userAnswers.broughtForwardAllowance.get
    case _ => 0
  }

  private def getDownsizingDetails(userAnswers: UserAnswers) = userAnswers.anyDownsizingAllowance.get match {
    case true => Some(DownsizingDetails(userAnswers))
    case _ => None
  }
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
    require(!userAnswers.anyAssetsPassingToDirectDescendants.get || userAnswers.assetsPassingToDirectDescendants.isDefined,
      "Assets Passing to Direct Descendants was not answered")
    require(!userAnswers.anyAssetsPassingToDirectDescendants.get || userAnswers.anyBroughtForwardAllowanceOnDisposal.isDefined,
      "Any Brought Forward Allowance on Disposal was not answered")
    require(!userAnswers.anyAssetsPassingToDirectDescendants.get ||
      !userAnswers.anyBroughtForwardAllowanceOnDisposal.get ||
      userAnswers.broughtForwardAllowanceOnDisposal.isDefined,
      "Brought Forward Allowance on Disposal was not answered")

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
    case true if userAnswers.anyBroughtForwardAllowanceOnDisposal.get => userAnswers.broughtForwardAllowanceOnDisposal.get
    case _ => 0
  }
}
