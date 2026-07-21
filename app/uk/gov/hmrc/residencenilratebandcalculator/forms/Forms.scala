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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.forms.constructors.{
  BooleanForm,
  NonNegativeIntForm,
  PositivePercentForm,
  PropertyPassingToDirectDescendantsForm
}

object Forms {

  val AssetsPassingToDirectDescendants: Form[Boolean] = BooleanForm(
    errorKey = "assets_passing_to_direct_descendants.error.required"
  )

  val ChargeableEstateValue: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "chargeable_estate_value.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "chargeable_estate_value.error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

  val ChargeableInheritedPropertyValue: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "chargeable_inherited_property_value.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

  val ChargeablePropertyValue: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "chargeable_property_value.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "chargeable_property_value.error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

  val ClaimDownsizingThreshold: Form[Boolean] = BooleanForm(errorKey = "claim_downsizing_threshold.error.required")

  val ExemptionsAndReliefClaimed: Form[Boolean] = BooleanForm(errorKey = "exemptions_and_relief_claimed.error.required")

  val GrossingUpOnEstateAssets: Form[Boolean] = BooleanForm(errorKey = "grossing_up_on_estate_assets.error.required")

  val GrossingUpOnEstateProperty: Form[Boolean] =
    BooleanForm(errorKey = "grossing_up_on_estate_property.error.required")

  val PartOfEstatePassingToDirectDescendants: Form[Boolean] = BooleanForm(
    errorKey = "part_of_estate_passing_to_direct_descendants.error.required"
  )

  val PercentagePassedToDirectDescendants: Form[BigDecimal] = PositivePercentForm(
    errorKeyBlank = "percentage_passed_to_direct_descendants.error.required",
    errorKeyNonNumeric = "percentage_passed_to_direct_descendants.error.non_numeric",
    errorKeyOutOfRange = "percentage_passed_to_direct_descendants.error.out_of_range"
  )

  val PropertyInEstate: Form[Boolean] = BooleanForm("property_in_estate.error.required")

  val PropertyPassingToDirectDescendants: Form[String] = PropertyPassingToDirectDescendantsForm()

  val PropertyValue: Form[Int] = NonNegativeIntForm(
    "property_value.error.blank",
    "error.whole_pounds",
    "property_value.error.non_numeric",
    "error.value_too_large"
  )

  val TransferAnyUnusedThreshold: Form[Boolean] = BooleanForm("transfer_any_unused_threshold.error.required")

  val TransferAvailableWhenPropertyChanged: Form[Boolean] = BooleanForm(
    "transfer_available_when_property_changed.error.required"
  )

  val ValueAvailableWhenPropertyChanged: Form[Int] =
    NonNegativeIntForm(
      errorKeyBlank = "value_available_when_property_changed.error.blank",
      errorKeyDecimal = "error.whole_pounds",
      errorKeyNonNumeric = "error.non_numeric",
      errorKeyTooLarge = "error.value_too_large"
    )

  val ValueBeingTransferred: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "value_being_transferred.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

  val ValueOfAssetsPassing: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "value_of_assets_passing.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

  val ValueOfChangedProperty: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "value_of_changed_property.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

  val ValueOfEstate: Form[Int] = NonNegativeIntForm(
    errorKeyBlank = "value_of_estate.error.blank",
    errorKeyDecimal = "error.whole_pounds",
    errorKeyNonNumeric = "value_of_estate.error.non_numeric",
    errorKeyTooLarge = "error.value_too_large"
  )

}
