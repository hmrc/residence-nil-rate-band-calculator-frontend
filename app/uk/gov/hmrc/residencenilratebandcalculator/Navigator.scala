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

import javax.inject.{Inject, Singleton}

import org.joda.time.LocalDate
import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers


@Singleton
class Navigator @Inject()() {
  private val routeMap: Map[String, UserAnswers => Call] = {

    Map(
      Constants.dateOfDeathId -> (ua => getDateOfDeathRoute(ua)),
      Constants.partOfEstatePassingToDirectDescendantsId -> (ua => getPartOfEstatePassingToDirectDescendantsRoute(ua)),
      Constants.valueOfEstateId -> (_ => ChargeableEstateValueController.onPageLoad()),
      Constants.chargeableEstateValueId -> (_ => PropertyInEstateController.onPageLoad()),
      Constants.propertyInEstateId -> (ua => getPropertyInEstateRoute(ua)),
      Constants.cannotClaimRNRB -> (_ => TransferAnyUnusedThresholdController.onPageLoad()),
      Constants.propertyValueId -> (_ => PropertyPassingToDirectDescendantsController.onPageLoad()),
      Constants.propertyPassingToDirectDescendantsId -> (ua => getPropertyPassingToDirectDescendantsRoute(ua)),
      Constants.percentagePassedToDirectDescendantsId -> (_ => ExemptionsAndReliefClaimedController.onPageLoad()),
      Constants.transferAnyUnusedThresholdId -> (ua => getTransferAnyUnusedThresholdRoute(ua)),
      Constants.valueBeingTransferredId -> (_ => ClaimDownsizingThresholdController.onPageLoad()),
      Constants.claimDownsizingThresholdId -> (ua => getClaimDownsizingThresholdRoute(ua)),
      Constants.cannotClaimDownsizingId -> (_ => ResultsController.onPageLoad()),
      Constants.datePropertyWasChangedId -> (ua => getDatePropertyWasChangedRoute(ua)),
      Constants.exemptionsAndReliefClaimedId -> (ua => getExemptionsAndReliefClaimedRoute(ua)),
      Constants.chargeablePropertyValueId -> (_ => ChargeableInheritedPropertyValueController.onPageLoad()),
      Constants.chargeableInheritedPropertyValueId -> (_ => TransferAnyUnusedThresholdController.onPageLoad()),
      Constants.grossingUpOnEstatePropertyId -> (ua => getGrossingUpOnEstatePropertyRoute(ua)),
      Constants.valueOfChangedPropertyId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (ua => getAnyAssetsPassingToDirectDescendantsRoute(ua)),
      Constants.grossingUpOnEstateAssetsId -> (ua => getGrossingUpOnEstateAssetsRoute(ua)),
      Constants.valueOfAssetsPassingId -> (ua => getValueOfAssetsPassingRoute(ua)),
      Constants.transferAvailableWhenPropertyChangedId -> (ua => getTransferAvailableWhenPropertyChangedRoute(ua)),
      Constants.valueAvailableWhenPropertyChangedId -> (_ => ResultsController.onPageLoad())
    )
  }

  private def getRouteForOptionalBoolean(optionalProperty: Option[Boolean], onTrue: Call, onFalse: Call) = optionalProperty match {
    case Some(true) => onTrue
    case Some(false) => onFalse
    case None => HomeController.onPageLoad()
  }

  private def getRouteForOptionalLocalDate(optionalDate: Option[LocalDate], transitionDate: LocalDate, transitionCall: Call, otherwise: Call) = optionalDate match {
    case Some(d) if d isBefore transitionDate => transitionCall
    case Some(_) => otherwise
    case None => HomeController.onPageLoad()
  }

  private def getDateOfDeathRoute(userAnswers: UserAnswers) =
    getRouteForOptionalLocalDate(userAnswers.dateOfDeath, Constants.eligibilityDate,
      TransitionOutController.onPageLoad(), PartOfEstatePassingToDirectDescendantsController.onPageLoad())

  private def getPartOfEstatePassingToDirectDescendantsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.partOfEstatePassingToDirectDescendants, ValueOfEstateController.onPageLoad(), TransitionOutController.onPageLoad())

  private def getPropertyInEstateRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.propertyInEstate, PropertyValueController.onPageLoad(), CannotClaimRNRBController.onPageLoad())

  private def getTransferAnyUnusedThresholdRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.transferAnyUnusedThreshold, ValueBeingTransferredController.onPageLoad(), ClaimDownsizingThresholdController.onPageLoad())

  private def getTransferAvailableWhenPropertyChangedRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.transferAvailableWhenPropertyChanged, ValueAvailableWhenPropertyChangedController.onPageLoad(), ResultsController.onPageLoad())

  private def getClaimDownsizingThresholdRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.claimDownsizingThreshold, DatePropertyWasChangedController.onPageLoad(), ResultsController.onPageLoad())

  private def getDatePropertyWasChangedRoute(userAnswers: UserAnswers) =
    getRouteForOptionalLocalDate(userAnswers.datePropertyWasChanged, Constants.downsizingEligibilityDate,
      CannotClaimDownsizingController.onPageLoad(), ValueOfChangedPropertyController.onPageLoad())

  private def getPropertyPassingToDirectDescendantsRoute(userAnswers: UserAnswers) = userAnswers.propertyPassingToDirectDescendants match {
    case Some(Constants.all) => ExemptionsAndReliefClaimedController.onPageLoad()
    case Some(Constants.some) => PercentagePassedToDirectDescendantsController.onPageLoad()
    case Some(_) => CannotClaimRNRBController.onPageLoad()
    case _ => HomeController.onPageLoad()
  }

  private def getExemptionsAndReliefClaimedRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.exemptionsAndReliefClaimed, GrossingUpOnEstatePropertyController.onPageLoad(), TransferAnyUnusedThresholdController.onPageLoad())

  private def getGrossingUpOnEstatePropertyRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.grossingUpOnEstateProperty, TransitionOutController.onPageLoad(), ChargeablePropertyValueController.onPageLoad())

  private def getAnyAssetsPassingToDirectDescendantsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyAssetsPassingToDirectDescendants, GrossingUpOnEstateAssetsController.onPageLoad(), CannotClaimDownsizingController.onPageLoad())

  private def getGrossingUpOnEstateAssetsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.grossingUpOnEstateAssets, TransitionOutController.onPageLoad(), ValueOfAssetsPassingController.onPageLoad())

  private def getValueOfAssetsPassingRoute(userAnswers: UserAnswers) = (userAnswers.transferAnyUnusedThreshold, userAnswers.datePropertyWasChanged) match {
    case (Some(true), Some(d)) if !(d isBefore Constants.eligibilityDate) => TransferAvailableWhenPropertyChangedController.onPageLoad()
    case _ => ResultsController.onPageLoad()
  }

  def nextPage(controllerId: String): UserAnswers => Call = {
    routeMap.getOrElse(controllerId, _ => PageNotFoundController.onPageLoad())
  }

  private val reverseRouteMap: Map[String, UserAnswers => Call] = {
    Map(
      Constants.partOfEstatePassingToDirectDescendantsId -> (_ => DateOfDeathController.onPageLoad()),
      Constants.valueOfEstateId -> (_ => PartOfEstatePassingToDirectDescendantsController.onPageLoad()),
      Constants.chargeableEstateValueId -> (_ => ValueOfEstateController.onPageLoad()),
      Constants.propertyInEstateId -> (_ => ChargeableEstateValueController.onPageLoad()),
      Constants.propertyValueId -> (_ => PropertyInEstateController.onPageLoad()),
      Constants.propertyPassingToDirectDescendantsId -> (_ => PropertyValueController.onPageLoad()),
      Constants.percentagePassedToDirectDescendantsId -> (_ => PropertyPassingToDirectDescendantsController.onPageLoad()),
      Constants.exemptionsAndReliefClaimedId -> (ua => getExemptionsAndReliefClaimedReverseRoute(ua)),
      Constants.chargeablePropertyValueId -> (_ => GrossingUpOnEstatePropertyController.onPageLoad()),
      Constants.chargeableInheritedPropertyValueId -> (_ => ChargeablePropertyValueController.onPageLoad()),
      Constants.grossingUpOnEstatePropertyId -> (_ => ExemptionsAndReliefClaimedController.onPageLoad()),
      Constants.transferAnyUnusedThresholdId -> (ua => getTransferAnyUnusedThresholdReverseRoute(ua)),
      Constants.valueBeingTransferredId -> (_ => TransferAnyUnusedThresholdController.onPageLoad()),
      Constants.claimDownsizingThresholdId -> (ua => getClaimDownsizingThresholdReverseRoute(ua)),
      Constants.datePropertyWasChangedId -> (_ => ClaimDownsizingThresholdController.onPageLoad()),
      Constants.valueOfChangedPropertyId -> (_ => DatePropertyWasChangedController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (_ => ValueOfChangedPropertyController.onPageLoad()),
      Constants.grossingUpOnEstateAssetsId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.valueOfAssetsPassingId -> (_ => GrossingUpOnEstateAssetsController.onPageLoad()),
      Constants.transferAvailableWhenPropertyChangedId -> (_ => ValueOfAssetsPassingController.onPageLoad()),
      Constants.valueAvailableWhenPropertyChangedId -> (_ => TransferAvailableWhenPropertyChangedController.onPageLoad())
    )
  }

  private def goToPageNotFound: UserAnswers => Call = _ => PageNotFoundController.onPageLoad()

  private def getTransferAnyUnusedThresholdReverseRoute(userAnswers: UserAnswers) = userAnswers.exemptionsAndReliefClaimed match {
    case Some(true) => ChargeableInheritedPropertyValueController.onPageLoad()
    case _ => userAnswers.propertyPassingToDirectDescendants match {
      case Some(Constants.none) => PropertyPassingToDirectDescendantsController.onPageLoad()
      case Some(_) => ExemptionsAndReliefClaimedController.onPageLoad()
      case _ => userAnswers.propertyInEstate match {
        case Some(true) => PropertyValueController.onPageLoad()
        case _ => PropertyInEstateController.onPageLoad()
      }
    }
  }

  private def getExemptionsAndReliefClaimedReverseRoute(userAnswers: UserAnswers) = userAnswers.propertyPassingToDirectDescendants match {
    case Some(Constants.some) => PercentagePassedToDirectDescendantsController.onPageLoad()
    case _ => PropertyPassingToDirectDescendantsController.onPageLoad()
  }

  private def getClaimDownsizingThresholdReverseRoute(userAnswers: UserAnswers) = userAnswers.transferAnyUnusedThreshold match {
    case Some(true) => ValueBeingTransferredController.onPageLoad()
    case _ => TransferAnyUnusedThresholdController.onPageLoad()
  }

  def lastPage(controllerId: String): UserAnswers => Call = {
    reverseRouteMap.getOrElse(controllerId, goToPageNotFound)
  }
}
