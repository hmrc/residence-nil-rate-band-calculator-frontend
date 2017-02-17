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
                            propertyValueAfterExemption: Option[PropertyValueAfterExemption] = None,
                            downsizingDetails: Option[DownsizingDetails] = None)

object CalculationInput {
  implicit val formats: OFormat[CalculationInput] = Json.format[CalculationInput]

  def apply(userAnswers: UserAnswers): CalculationInput = {
    CalculationInput(
      userAnswers.dateOfDeath.get,
      userAnswers.grossEstateValue.get,
      userAnswers.chargeableTransferAmount.get,
      getPropertyValue(userAnswers),
      getPercentageCloselyInherited(userAnswers),
      getBroughtForwardAllowance(userAnswers),
      None,
      None
    )
  }

  private def getPropertyValue(userAnswers: UserAnswers) = userAnswers.estateHasProperty.get match {
    case true => userAnswers.propertyValue.get
    case _ => 0
  }

  private def getPercentageCloselyInherited(userAnswers: UserAnswers) = userAnswers.estateHasProperty.get match {
    case true => userAnswers.percentageCloselyInherited.get
    case _ => 0
  }

  private def getBroughtForwardAllowance(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance.get match {
    case true => userAnswers.broughtForwardAllowance.get
    case _ => 0
  }
}

case class DownsizingDetails(dateOfDisposal: LocalDate,
                             valueOfDisposedProperty: Int,
                             valueCloselyInherited: Int,
                             broughtForwardAllowanceAtDisposal: Int)

object DownsizingDetails {
  implicit val formats: OFormat[DownsizingDetails] = Json.format[DownsizingDetails]

  def apply(userAnswers: UserAnswers): DownsizingDetails = {
    ???
  }
}
