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

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.models.InputValidationError.*

case class CalculationInput(
    dateOfDeath: LocalDate,
    valueOfEstate: Int,
    chargeableEstateValue: Int,
    propertyValue: Int,
    percentagePassedToDirectDescendants: BigDecimal,
    valueBeingTransferred: Int,
    propertyValueAfterExemption: Option[PropertyValueAfterExemption],
    downsizingDetails: Option[DownsizingDetails]
)

private final case class ValidatedAnswers(
    dateOfDeath: LocalDate,
    valueOfEstate: Int,
    chargeableEstateValue: Int,
    propertyValue: Int,
    valueBeingTransferred: Int
)

object CalculationInput {
  given formats: OFormat[CalculationInput] = Json.format[CalculationInput]

  def apply(userAnswers: UserAnswers): Either[InputValidationError, CalculationInput] =
    validate(userAnswers).flatMap(validated => create(userAnswers, validated))

  private def validate(userAnswers: UserAnswers): Either[InputValidationError, ValidatedAnswers] =
    for {
      dateOfDeath           <- userAnswers.dateOfDeath.toRight(DateOfDeathNotDefined)
      valueOfEstate         <- userAnswers.valueOfEstate.toRight(ValueOfEstateNotDefined)
      chargeableEstateValue <- userAnswers.chargeableEstateValue.toRight(ChargeableEstateValueNotDefined)
      propertyInEstate      <- userAnswers.propertyInEstate.toRight(PropertyInEstateNotDefined)
      propertyValue <-
        if (propertyInEstate)
          validatePropertyInEstateDependencies(userAnswers)
        else
          Right(0)
      transferAnyUnusedThreshold <- userAnswers.transferAnyUnusedThreshold.toRight(TransferAnyUnusedThresholdNotDefined)
      valueBeingTransferred <-
        if (transferAnyUnusedThreshold)
          validateValueBeingTransferredDependencies(userAnswers)
        else
          Right(0)
      _ <- userAnswers.claimDownsizingThreshold.toRight(ClaimDownsizingThresholdNotDefined)
    } yield ValidatedAnswers(dateOfDeath, valueOfEstate, chargeableEstateValue, propertyValue, valueBeingTransferred)

  private def create(
      userAnswers: UserAnswers,
      validatedAnswers: ValidatedAnswers
  ): Either[InputValidationError, CalculationInput] =
    for {
      propertyValueAfterExemption <- getChargeablePropertyValue(userAnswers)
      downsizingDetails           <- getDownsizingDetails(userAnswers)

    } yield CalculationInput(
      dateOfDeath = validatedAnswers.dateOfDeath,
      valueOfEstate = validatedAnswers.valueOfEstate,
      chargeableEstateValue = validatedAnswers.chargeableEstateValue,
      propertyValue = validatedAnswers.propertyValue,
      percentagePassedToDirectDescendants = userAnswers.percentagePassedToDirectDescendants.getOrElse(BigDecimal(0)),
      valueBeingTransferred = validatedAnswers.valueBeingTransferred,
      propertyValueAfterExemption = propertyValueAfterExemption,
      downsizingDetails = downsizingDetails
    )

  private def getChargeablePropertyValue(
      userAnswers: UserAnswers
  ): Either[InputValidationError, Option[PropertyValueAfterExemption]] =
    userAnswers.chargeablePropertyValue match {
      case None =>
        Right(None)
      case Some(chargeablePropertyValue) =>
        userAnswers.chargeableInheritedPropertyValue
          .toRight(ChargeableInheritedPropertyValueNotDefined)
          .map { chargeableInheritedPropertyValue =>
            Some(
              PropertyValueAfterExemption(
                chargeablePropertyValue,
                chargeableInheritedPropertyValue
              )
            )
          }
    }

  private def getDownsizingDetails(userAnswers: UserAnswers): Either[InputValidationError, Option[DownsizingDetails]] =
    if (userAnswers.claimDownsizingThreshold.contains(true)) {
      userAnswers.datePropertyWasChanged
        .toRight(DatePropertyWasChangedNotDefined)
        .flatMap {
          case d if d.isBefore(Constants.downsizingEligibilityDate) => Right(None)
          case _                                                    => DownsizingDetails(userAnswers).map(Some(_))
        }
    } else {
      Right(None)
    }

  private def validatePropertyInEstateDependencies(userAnswers: UserAnswers): Either[InputValidationError, Int] =

    for {
      propertyValue <- userAnswers.propertyValue.toRight(PropertyValueNotDefined)
      propertyPassingToDirectDescendants <- userAnswers.propertyPassingToDirectDescendants.toRight(
        PropertyPassingToDirectDescendantsNotDefined
      )
      _ <-
        if (propertyPassingToDirectDescendants == Constants.some)
          userAnswers.percentagePassedToDirectDescendants.toRight(PercentagePassedToDirectDescendantsNotDefined)
        else
          Right(())
      _ <-
        if (propertyPassingToDirectDescendants != Constants.none)
          validatePropertyPassingToDirectDescendantsDependencies(userAnswers)
        else
          Right(())

    } yield propertyValue

  private def validatePropertyPassingToDirectDescendantsDependencies(
      userAnswers: UserAnswers
  ): Either[InputValidationError, UserAnswers] =
    for {
      exemptionsAndReliefClaimed <- userAnswers.exemptionsAndReliefClaimed.toRight(ExemptionsAndReliefClaimedNotDefined)
      _ <-
        if (exemptionsAndReliefClaimed)
          validateExemptionsDependencies(userAnswers)
        else
          Right(())
    } yield userAnswers

  private def validateExemptionsDependencies(userAnswers: UserAnswers): Either[InputValidationError, UserAnswers] =
    for {
      grossingUpOnEstateProperty <- userAnswers.grossingUpOnEstateProperty.toRight(GrossingUpOnEstatePropertyNotDefined)
      _ <-
        if (!grossingUpOnEstateProperty)
          validateNoGrossingUpDependencies(userAnswers)
        else
          Right(())
    } yield userAnswers

  private def validateNoGrossingUpDependencies(userAnswers: UserAnswers): Either[InputValidationError, UserAnswers] =
    for {
      _ <- userAnswers.chargeablePropertyValue.toRight(ChargeablePropertyValueNotDefined)
      _ <- userAnswers.chargeableInheritedPropertyValue.toRight(ChargeableInheritedPropertyValueNotDefined)
    } yield userAnswers

  private def validateValueBeingTransferredDependencies(userAnswers: UserAnswers) =
    userAnswers.valueBeingTransferred.toRight(ValueBeingTransferredNotDefined)

}

case class DownsizingDetails(
    datePropertyWasChanged: LocalDate,
    valueOfChangedProperty: Int,
    valueOfAssetsPassing: Int,
    valueAvailableWhenPropertyChanged: Int
)

object DownsizingDetails {
  given OFormat[DownsizingDetails] = Json.format[DownsizingDetails]

  def apply(userAnswers: UserAnswers): Either[InputValidationError, DownsizingDetails] =

    validate(userAnswers).map {
      case (datePropertyWasChange, valueOfChangedProperty, valueOfAssetPassing, valueAvailableWhenPropertyChanged) =>
        create(datePropertyWasChange, valueOfChangedProperty, valueOfAssetPassing, valueAvailableWhenPropertyChanged)
    }

  private def create(
      datePropertyWasChanged: LocalDate,
      valueOfChangedProperty: Int,
      valueOfAssetsPassing: Int,
      valueAvailableWhenPropertyChanged: Int
  ): DownsizingDetails =
    DownsizingDetails(
      datePropertyWasChanged,
      valueOfChangedProperty,
      valueOfAssetsPassing,
      valueAvailableWhenPropertyChanged
    )

  private def validate(userAnswers: UserAnswers): Either[InputValidationError, (LocalDate, Int, Int, Int)] =
    for {
      datePropertyWasChanged <- userAnswers.datePropertyWasChanged.toRight(DatePropertyWasChangedNotDefined)
      valueOfChangedProperty <- userAnswers.valueOfChangedProperty.toRight(ValueOfChangedPropertyNotDefined)
      assetsPassingToDirectDescendants <- userAnswers.assetsPassingToDirectDescendants.toRight(
        AssetsPassingToDirectDescendantsNotDefined
      )
      valueOfAssetsPassing <-
        if (assetsPassingToDirectDescendants)
          validateValueOfAssetsPassingDependencies(userAnswers)
        else
          Right(0)
      valueWhenPropertyChanged <- getValueAvailableWhenPropertyChanged(userAnswers)
    } yield (datePropertyWasChanged, valueOfChangedProperty, valueOfAssetsPassing, valueWhenPropertyChanged)

  private def getValueAvailableWhenPropertyChanged(userAnswers: UserAnswers): Either[InputValidationError, Int] =
    userAnswers.assetsPassingToDirectDescendants match {
      case Some(true) =>
        userAnswers.transferAvailableWhenPropertyChanged match {
          case Some(true) =>
            userAnswers.valueAvailableWhenPropertyChanged.toRight(ValueAvailableWhenPropertyChangedNotDefined)
          case _ => Right(0)
        }
      case _ => Right(0)
    }

  private def validateValueOfAssetsPassingDependencies(
      userAnswers: UserAnswers
  ): Either[InputValidationError, Int] =

    for {
      valueOfAssetsPassing       <- userAnswers.valueOfAssetsPassing.toRight(ValueOfAssetsPassingNotDefined)
      transferAnyUnusedThreshold <- userAnswers.transferAnyUnusedThreshold.toRight(TransferAnyUnusedThresholdNotDefined)
      datePropertyWasChanged     <- userAnswers.datePropertyWasChanged.toRight(DatePropertyWasChangedNotDefined)
      _ <-
        if (transferAnyUnusedThreshold && !datePropertyWasChanged.isBefore(Constants.eligibilityDate))
          validateTransferAvailableWhenPropertyChangedDependencies(userAnswers)
        else
          Right(())
    } yield valueOfAssetsPassing

  private def validateTransferAvailableWhenPropertyChangedDependencies(userAnswers: UserAnswers) =

    userAnswers.transferAvailableWhenPropertyChanged match {
      case None        => Left(TransferAvailableWhenPropertyChangedNotDefined)
      case Some(false) => Right(())
      case Some(true) =>
        userAnswers.valueAvailableWhenPropertyChanged match {
          case Some(_) => Right(())
          case None    => Left(ValueAvailableWhenPropertyChangedNotDefined)
        }
    }

}
