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
      Constants.anyEstatePassedToDescendantsId -> (ua => getAnyEstatePassedToDescendantsRoute(ua)),
      Constants.grossEstateValueId -> (_ => ChargeableTransferAmountController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (_ => EstateHasPropertyController.onPageLoad()),
      Constants.estateHasPropertyId -> (ua => getEstateHasPropertyRoute(ua)),
      Constants.cannotClaimRNRB -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.propertyValueId -> (_ => AnyPropertyCloselyInheritedController.onPageLoad()),
      Constants.anyPropertyCloselyInheritedId -> (ua => getAnyPropertyCloselyInheritedRoute(ua)),
      Constants.percentageCloselyInheritedId -> (_ => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (ua => getAnyBroughtForwardAllowanceRoute(ua)),
      Constants.broughtForwardAllowanceId -> (_ => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (ua => getAnyDownsizingAllowanceRoute(ua)),
      Constants.cannotClaimDownsizingId -> (_ => ResultsController.onPageLoad()),
      Constants.dateOfDisposalId -> (ua => getDateOfDisposalRoute(ua)),
      Constants.anyExemptionId -> (ua => getAnyExemptionRoute(ua)),
      Constants.chargeableValueOfResidenceId -> (_ => ChargeableValueOfResidenceCloselyInheritedController.onPageLoad()),
      Constants.chargeableValueOfResidenceCloselyInheritedId -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.doesGrossingUpApplyToResidenceId -> (ua => getDoesGrossingUpApplyToResidenceRoute(ua)),
      Constants.valueOfDisposedPropertyId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (ua => getAnyAssetsPassingToDirectDescendantsRoute(ua)),
      Constants.doesGrossingUpApplyToOtherPropertyId -> (ua => getDoesGrossingUpApplyToOtherPropertyRoute(ua)),
      Constants.assetsPassingToDirectDescendantsId -> (_ => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (ua => getAnyBroughtForwardAllowanceOnDisposalRoute(ua)),
      Constants.broughtForwardAllowanceOnDisposalId -> (_ => ResultsController.onPageLoad()),
      Constants.checkAnswersId -> (_ => ResultsController.onPageLoad())
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
      TransitionOutController.onPageLoad(), AnyEstatePassedToDescendantsController.onPageLoad())

  private def getAnyEstatePassedToDescendantsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyEstatePassedToDescendants, GrossEstateValueController.onPageLoad(), TransitionOutController.onPageLoad())

  private def getEstateHasPropertyRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.estateHasProperty, PropertyValueController.onPageLoad(), CannotClaimRNRBController.onPageLoad())

  private def getAnyBroughtForwardAllowanceRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyBroughtForwardAllowance, BroughtForwardAllowanceController.onPageLoad(), AnyDownsizingAllowanceController.onPageLoad())

  private def getAnyBroughtForwardAllowanceOnDisposalRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyBroughtForwardAllowanceOnDisposal, BroughtForwardAllowanceOnDisposalController.onPageLoad(), ResultsController.onPageLoad())

  private def getAnyDownsizingAllowanceRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyDownsizingAllowance, DateOfDisposalController.onPageLoad(), ResultsController.onPageLoad())

  private def getDateOfDisposalRoute(userAnswers: UserAnswers) =
    getRouteForOptionalLocalDate(userAnswers.dateOfDisposal, Constants.downsizingEligibilityDate,
      CannotClaimDownsizingController.onPageLoad(), ValueOfDisposedPropertyController.onPageLoad())

  private def getAnyPropertyCloselyInheritedRoute(userAnswers: UserAnswers) = userAnswers.anyPropertyCloselyInherited match {
    case Some(Constants.all) => AnyExemptionController.onPageLoad()
    case Some(Constants.some) => PercentageCloselyInheritedController.onPageLoad()
    case Some(_) => CannotClaimRNRBController.onPageLoad()
    case _ => HomeController.onPageLoad()
  }

  private def getAnyExemptionRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyExemption, DoesGrossingUpApplyToResidenceController.onPageLoad(), AnyBroughtForwardAllowanceController.onPageLoad())

  private def getDoesGrossingUpApplyToResidenceRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.doesGrossingUpApplyToResidence, TransitionOutController.onPageLoad(), ChargeableValueOfResidenceController.onPageLoad())

  private def getAnyAssetsPassingToDirectDescendantsRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.anyAssetsPassingToDirectDescendants, DoesGrossingUpApplyToOtherPropertyController.onPageLoad(), CannotClaimDownsizingController.onPageLoad())

  private def getDoesGrossingUpApplyToOtherPropertyRoute(userAnswers: UserAnswers) =
    getRouteForOptionalBoolean(userAnswers.doesGrossingUpApplyToOtherProperty, TransitionOutController.onPageLoad(), AssetsPassingToDirectDescendantsController.onPageLoad())

  def nextPage(controllerId: String): UserAnswers => Call = {
    routeMap.getOrElse(controllerId, _ => PageNotFoundController.onPageLoad())
  }

  private val reverseRouteMap: Map[String, UserAnswers => Call] = {
    Map(
      Constants.anyEstatePassedToDescendantsId -> (_ => DateOfDeathController.onPageLoad()),
      Constants.grossEstateValueId -> (_ => AnyEstatePassedToDescendantsController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (_ => GrossEstateValueController.onPageLoad()),
      Constants.estateHasPropertyId -> (_ => ChargeableTransferAmountController.onPageLoad()),
      Constants.propertyValueId -> (_ => EstateHasPropertyController.onPageLoad()),
      Constants.anyPropertyCloselyInheritedId -> (_ => PropertyValueController.onPageLoad()),
      Constants.percentageCloselyInheritedId -> (_ => AnyPropertyCloselyInheritedController.onPageLoad()),
      Constants.anyExemptionId -> (ua => getAnyExemptionReverseRoute(ua)),
      Constants.chargeableValueOfResidenceId -> (_ => DoesGrossingUpApplyToResidenceController.onPageLoad()),
      Constants.chargeableValueOfResidenceCloselyInheritedId -> (_ => ChargeableValueOfResidenceController.onPageLoad()),
      Constants.doesGrossingUpApplyToResidenceId -> (_ => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (ua => getAnyBroughtForwardAllowanceReverseRoute(ua)),
      Constants.broughtForwardAllowanceId -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (ua => getAnyDownsizingAllowanceReverseRoute(ua)),
      Constants.dateOfDisposalId -> (_ => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.valueOfDisposedPropertyId -> (_ => DateOfDisposalController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (_ => ValueOfDisposedPropertyController.onPageLoad()),
      Constants.doesGrossingUpApplyToOtherPropertyId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.assetsPassingToDirectDescendantsId -> (_ => DoesGrossingUpApplyToOtherPropertyController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (_ => AssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.broughtForwardAllowanceOnDisposalId -> (_ => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad())
    )
  }

  private def goToPageNotFound: UserAnswers => Call = _ => PageNotFoundController.onPageLoad()

  private def getAnyBroughtForwardAllowanceReverseRoute(userAnswers: UserAnswers) = userAnswers.anyExemption match {
    case Some(true) => ChargeableValueOfResidenceCloselyInheritedController.onPageLoad()
    case _ => userAnswers.anyPropertyCloselyInherited match {
      case Some(Constants.none) => AnyPropertyCloselyInheritedController.onPageLoad()
      case Some(_) => AnyExemptionController.onPageLoad()
      case _ => userAnswers.estateHasProperty match {
        case Some(true) => PropertyValueController.onPageLoad()
        case _ => EstateHasPropertyController.onPageLoad()
      }
    }
  }

  private def getAnyExemptionReverseRoute(userAnswers: UserAnswers) = userAnswers.anyPropertyCloselyInherited match {
    case Some(Constants.some) => PercentageCloselyInheritedController.onPageLoad()
    case _ => AnyPropertyCloselyInheritedController.onPageLoad()
  }

  private def getAnyDownsizingAllowanceReverseRoute(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance match {
    case Some(true) => BroughtForwardAllowanceController.onPageLoad()
    case _ => AnyBroughtForwardAllowanceController.onPageLoad()
  }

  def lastPage(controllerId: String): UserAnswers => Call = {
    reverseRouteMap.getOrElse(controllerId, goToPageNotFound)
  }
}
