/*
 * Copyright 2026 HM Revenue & Customs
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

enum InputValidationError(val errorMessage: String) {
  case DateOfDeathNotDefined           extends InputValidationError("Date Of Death was not answered")
  case ValueOfEstateNotDefined         extends InputValidationError("Value Of Estate was not answered")
  case ChargeableEstateValueNotDefined extends InputValidationError("Chargeable Estate Value was not answered")
  case PropertyInEstateNotDefined      extends InputValidationError("Property In Estate was not answered")

  case TransferAnyUnusedThresholdNotDefined
      extends InputValidationError("Transfer Any Unused Allowance was not answered")

  case ClaimDownsizingThresholdNotDefined extends InputValidationError("Claim Downsizing Threshold was not answered")

  case PercentagePassedToDirectDescendantsNotDefined
      extends InputValidationError("Percentage Passed To Direct Descendants was not answered")

  case DatePropertyWasChangedNotDefined extends InputValidationError("Date Property Was Changed was not answered")
  case PropertyValueNotDefined          extends InputValidationError("Property Value was not answered")

  case PropertyPassingToDirectDescendantsNotDefined
      extends InputValidationError("Property Passing To Direct Descendants was not answered")

  case ExemptionsAndReliefClaimedNotDefined
      extends InputValidationError("Exemptions And Relief Claimed was not answered")

  case GrossingUpOnEstatePropertyNotDefined
      extends InputValidationError("Grossing Up On Estate Property was not answered")

  case ChargeablePropertyValueNotDefined extends InputValidationError("Chargeable Property Value was not answered")

  case ChargeableInheritedPropertyValueNotDefined
      extends InputValidationError("Chargeable Inherited Property Value was not answered")

  case ValueBeingTransferredNotDefined  extends InputValidationError("Value Being Transferred was not answered")
  case ValueOfChangedPropertyNotDefined extends InputValidationError("Value Of Changed Property was not answered")

  case AssetsPassingToDirectDescendantsNotDefined
      extends InputValidationError("Assets Passing To Direct Descendants was not answered")

  case TransferAvailableWhenPropertyChangedNotDefined
      extends InputValidationError("Transfer Available When Property Changed was not answered")

  case ValueOfAssetsPassingNotDefined extends InputValidationError("Value Of Assets Passing was not answered")

  case ValueAvailableWhenPropertyChangedNotDefined
      extends InputValidationError("Value Available When Property Changed was not answered")

}
