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

package uk.gov.hmrc.residencenilratebandcalculator

import javax.inject.{Inject, Singleton}

import java.time.LocalDate
import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers

@Singleton
class Navigator @Inject() () {

  private val routeMap: Map[String, UserAnswers => Call] =
    Map(
      Constants.dateOfDeathId                            -> (ua => getDateOfDeathRoute(ua)),
      Constants.partOfEstatePassingToDirectDescendantsId -> (ua => getPartOfEstatePassingToDirectDescendantsRoute(ua)),
      Constants.valueOfEstateId                          -> (_ => ChargeableEstateValueController.onPageLoad),
      Constants.chargeableEstateValueId                  -> (_ => PropertyInEstateController.onPageLoad),
      Constants.propertyInEstateId                       -> (ua => getPropertyInEstateRoute(ua)),
      Constants.noAdditionalThresholdAvailableId         -> (_ => TransferAnyUnusedThresholdController.onPageLoad),
      Constants.propertyValueId                       -> (_ => PropertyPassingToDirectDescendantsController.onPageLoad),
      Constants.propertyPassingToDirectDescendantsId  -> (ua => getPropertyPassingToDirectDescendantsRoute(ua)),
      Constants.percentagePassedToDirectDescendantsId -> (_ => ExemptionsAndReliefClaimedController.onPageLoad),
      Constants.transferAnyUnusedThresholdId          -> (ua => getTransferAnyUnusedThresholdRoute(ua)),
      Constants.valueBeingTransferredId               -> (_ => ClaimDownsizingThresholdController.onPageLoad),
      Constants.claimDownsizingThresholdId            -> (ua => getClaimDownsizingThresholdRoute(ua)),
      Constants.noDownsizingThresholdIncrease         -> (_ => CheckYourAnswersController.onPageLoad),
      Constants.datePropertyWasChangedId              -> (ua => getDatePropertyWasChangedRoute(ua)),
      Constants.exemptionsAndReliefClaimedId          -> (ua => getExemptionsAndReliefClaimedRoute(ua)),
      Constants.chargeablePropertyValueId             -> (_ => ChargeableInheritedPropertyValueController.onPageLoad),
      Constants.chargeableInheritedPropertyValueId    -> (_ => TransferAnyUnusedThresholdController.onPageLoad),
      Constants.grossingUpOnEstatePropertyId          -> (ua => getGrossingUpOnEstatePropertyRoute(ua)),
      Constants.valueOfChangedPropertyId              -> (_ => AssetsPassingToDirectDescendantsController.onPageLoad),
      Constants.assetsPassingToDirectDescendantsId    -> (ua => getAssetsPassingToDirectDescendantsRoute(ua)),
      Constants.grossingUpOnEstateAssetsId            -> (ua => getGrossingUpOnEstateAssetsRoute(ua)),
      Constants.valueOfAssetsPassingId                -> (ua => getValueOfAssetsPassingRoute(ua)),
      Constants.transferAvailableWhenPropertyChangedId -> (ua => getTransferAvailableWhenPropertyChangedRoute(ua)),
      Constants.valueAvailableWhenPropertyChangedId    -> (_ => CheckYourAnswersController.onPageLoad),
      Constants.checkYourAnswersId                     -> (_ => ThresholdCalculationResultController.onPageLoad)
    )

  private def getRouteForOptionalBoolean(optionalProperty: Option[Boolean], onTrue: Call, onFalse: Call) =
    optionalProperty match {
      case Some(true)  => onTrue
      case Some(false) => onFalse
      case None        => CalculateThresholdIncreaseController.onPageLoad
    }

  private def getRouteForOptionalLocalDate(
      optionalDate: Option[LocalDate],
      transitionDate: LocalDate,
      transitionCall: Call,
      otherwise: Call
  ) = optionalDate match {
    case Some(d) if d.isBefore(transitionDate) => transitionCall
    case Some(_)                               => otherwise
    case None                                  => CalculateThresholdIncreaseController.onPageLoad
  }

  private def getDateOfDeathRoute(userAnswers: UserAnswers) =
    getRouteForOptionalLocalDate(
      userAnswers.dateOfDeath,
      Constants.eligibilityDate,
      NoThresholdIncreaseController.onPageLoad,
      PartOfEstatePassingToDirectDescendantsController.onPageLoad
    )

  private def getPartOfEstatePassingToDirectDescendantsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.partOfEstatePassingToDirectDescendants,
      ValueOfEstateController.onPageLoad,
      NoThresholdIncreaseController.onPageLoad
    )

  private def getPropertyInEstateRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.propertyInEstate,
      PropertyValueController.onPageLoad,
      NoAdditionalThresholdAvailableController.onPageLoad
    )

  private def getTransferAnyUnusedThresholdRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.transferAnyUnusedThreshold,
      ValueBeingTransferredController.onPageLoad,
      ClaimDownsizingThresholdController.onPageLoad
    )

  private def getTransferAvailableWhenPropertyChangedRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.transferAvailableWhenPropertyChanged,
      ValueAvailableWhenPropertyChangedController.onPageLoad,
      CheckYourAnswersController.onPageLoad
    )

  private def getClaimDownsizingThresholdRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.claimDownsizingThreshold,
      DatePropertyWasChangedController.onPageLoad,
      CheckYourAnswersController.onPageLoad
    )

  private def getDatePropertyWasChangedRoute(userAnswers: UserAnswers) =
    getRouteForOptionalLocalDate(
      userAnswers.datePropertyWasChanged,
      Constants.downsizingEligibilityDate,
      NoDownsizingThresholdIncreaseController.onPageLoad,
      ValueOfChangedPropertyController.onPageLoad
    )

  private def getPropertyPassingToDirectDescendantsRoute(userAnswers: UserAnswers) =
    userAnswers.propertyPassingToDirectDescendants match {
      case Some(Constants.all)  => ExemptionsAndReliefClaimedController.onPageLoad
      case Some(Constants.some) => PercentagePassedToDirectDescendantsController.onPageLoad
      case Some(_)              => NoAdditionalThresholdAvailableController.onPageLoad
      case _                    => CalculateThresholdIncreaseController.onPageLoad
    }

  private def getExemptionsAndReliefClaimedRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.exemptionsAndReliefClaimed,
      GrossingUpOnEstatePropertyController.onPageLoad,
      TransferAnyUnusedThresholdController.onPageLoad
    )

  private def getGrossingUpOnEstatePropertyRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.grossingUpOnEstateProperty,
      UnableToCalculateThresholdIncreaseController.onPageLoad,
      ChargeablePropertyValueController.onPageLoad
    )

  private def getAssetsPassingToDirectDescendantsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.assetsPassingToDirectDescendants,
      GrossingUpOnEstateAssetsController.onPageLoad,
      NoDownsizingThresholdIncreaseController.onPageLoad
    )

  private def getGrossingUpOnEstateAssetsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(
      userAnswers.grossingUpOnEstateAssets,
      UnableToCalculateThresholdIncreaseController.onPageLoad,
      ValueOfAssetsPassingController.onPageLoad
    )

  private def getValueOfAssetsPassingRoute(userAnswers: UserAnswers) =
    (userAnswers.transferAnyUnusedThreshold, userAnswers.datePropertyWasChanged) match {
      case (Some(true), Some(d)) if !d.isBefore(Constants.eligibilityDate) =>
        TransferAvailableWhenPropertyChangedController.onPageLoad
      case _ => CheckYourAnswersController.onPageLoad
    }

  def nextPage(controllerId: String): UserAnswers => Call =
    routeMap.getOrElse(controllerId, _ => throw new Exception(s"Page not found for controller id: $controllerId"))

}
