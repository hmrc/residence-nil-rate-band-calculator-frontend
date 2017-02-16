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

import play.api.mvc.Call
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers


@Singleton
class Navigator @Inject()() {
  private val routeMap: Map[String, UserAnswers => Call] = {

    Map(
      Constants.dateOfDeathId -> (ua => getDateOfDeathRoute(ua)),
      Constants.grossEstateValueId -> (_ => ChargeableTransferAmountController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (_ => EstateHasPropertyController.onPageLoad()),
      Constants.estateHasPropertyId -> (ua => getEstateHasPropertyRoute(ua)),
      Constants.propertyValueId -> (_ => AnyPropertyCloselyInheritedController.onPageLoad()),
      Constants.anyPropertyCloselyInheritedId -> (ua => getAnyPropertyCloselyInheritedRoute(ua)),
      Constants.percentageCloselyInheritedId -> (_ => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (ua => getAnyBroughtForwardAllowanceRoute(ua)),
      Constants.broughtForwardAllowanceId -> (_ => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (ua => getAnyDownsizingAllowanceRoute(ua)),
      Constants.dateOfDisposalId -> (ua => getDateOfDisposalRoute(ua)),
      Constants.anyExemptionId -> (ua => getAnyExemptionRoute(ua)),
      Constants.propertyValueAfterExemptionId -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.valueOfDisposedPropertyId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (ua => getAnyAssetsPassingToDirectDescendantsRoute(ua)),
      Constants.assetsPassingToDirectDescendantsId -> (_ => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (ua => getAnyBroughtForwardAllowanceOnDisposalRoute(ua)),
      Constants.broughtForwardAllowanceOnDisposalId -> (_ => CheckAnswersController.onPageLoad()),
      Constants.checkAnswersId -> (_ => ResultsController.onPageLoad()),
      Constants.purposeOfUseId -> (ua => getPurposeOfUseRoute(ua))
    )
  }

  private def getDateOfDeathRoute(cacheData: UserAnswers) = cacheData.dateOfDeath match {
    case Some(d) if (d isEqual Constants.eligibilityDate) || (d isAfter Constants.eligibilityDate) => GrossEstateValueController.onPageLoad()
    case Some(_) => TransitionOutController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getEstateHasPropertyRoute(userAnswers: UserAnswers) = userAnswers.estateHasProperty match {
    case Some(true) => PropertyValueController.onPageLoad()
    case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyBroughtForwardAllowanceRoute(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance match {
    case Some(true) => BroughtForwardAllowanceController.onPageLoad()
    case Some(false) => AnyDownsizingAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyBroughtForwardAllowanceOnDisposalRoute(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowanceOnDisposal match {
    case Some(true) => BroughtForwardAllowanceOnDisposalController.onPageLoad()
    case Some(false) => CheckAnswersController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyDownsizingAllowanceRoute(userAnswers: UserAnswers) = userAnswers.anyDownsizingAllowance match {
    case Some(true) => DateOfDisposalController.onPageLoad()
    case Some(false) => CheckAnswersController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getDateOfDisposalRoute(userAnswers: UserAnswers) = userAnswers.dateOfDisposal match {
    case Some(d) if d isBefore Constants.downsizingEligibilityDate => TransitionOutController.onPageLoad()
    case Some(_) => ValueOfDisposedPropertyController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyPropertyCloselyInheritedRoute(userAnswers: UserAnswers) = userAnswers.anyPropertyCloselyInherited match {
    case Some(true) => PercentageCloselyInheritedController.onPageLoad()
    case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyExemptionRoute(userAnswers: UserAnswers) = userAnswers.anyExemption match {
    case Some(true) => PropertyValueAfterExemptionController.onPageLoad()
    case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyAssetsPassingToDirectDescendantsRoute(userAnswers: UserAnswers) = userAnswers.anyAssetsPassingToDirectDescendants match {
    case Some(true) => AssetsPassingToDirectDescendantsController.onPageLoad()
    case Some(false) => CheckAnswersController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getPurposeOfUseRoute(userAnswers: UserAnswers) = userAnswers.purposeOfUse match {
    case Some(Constants.dealingWithEstate) => DateOfDeathController.onPageLoad()
    case Some(Constants.planning) => PlanningController.onPageLoad()
    case _ => HomeController.onPageLoad()
  }

  def nextPage(controllerId: String): UserAnswers => Call = {
    routeMap.getOrElse(controllerId, _ => PageNotFoundController.onPageLoad())
  }

  private val reverseRouteMap: Map[String, UserAnswers => Call] = {
    Map(
      Constants.grossEstateValueId -> (_ => DateOfDeathController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (_ => GrossEstateValueController.onPageLoad()),
      Constants.estateHasPropertyId -> (_ => ChargeableTransferAmountController.onPageLoad()),
      Constants.propertyValueId -> (_ => EstateHasPropertyController.onPageLoad()),
      Constants.anyPropertyCloselyInheritedId -> (_ => PropertyValueController.onPageLoad()),
      Constants.percentageCloselyInheritedId -> (_ => AnyPropertyCloselyInheritedController.onPageLoad()),
      Constants.anyExemptionId -> (_ => PercentageCloselyInheritedController.onPageLoad()),
      Constants.propertyValueAfterExemptionId -> (_ => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (ua => getAnyBroughtForwardAllowanceReverseRoute(ua)),
      Constants.broughtForwardAllowanceId -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (ua => getAnyDownsizingAllowanceReverseRoute(ua)),
      Constants.dateOfDisposalId -> (_ => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.valueOfDisposedPropertyId -> (_ => DateOfDisposalController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (_ => ValueOfDisposedPropertyController.onPageLoad()),
      Constants.assetsPassingToDirectDescendantsId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (_ => AssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.broughtForwardAllowanceOnDisposalId -> (_ => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad())
    )
  }

  private def goToPageNotFound: UserAnswers => Call = _ => PageNotFoundController.onPageLoad()

  private def getAnyBroughtForwardAllowanceReverseRoute(userAnswers: UserAnswers) = userAnswers.anyExemption match {
    case Some(true) => PropertyValueAfterExemptionController.onPageLoad()
    case _ => userAnswers.anyPropertyCloselyInherited match {
      case Some(true) => PercentageCloselyInheritedController.onPageLoad()
      case _ => userAnswers.estateHasProperty match {
        case Some(true) => PropertyValueController.onPageLoad()
        case _ => EstateHasPropertyController.onPageLoad()
      }
    }
  }

  private def getAnyDownsizingAllowanceReverseRoute(userAnswers: UserAnswers) = userAnswers.anyBroughtForwardAllowance match {
    case Some(true) => BroughtForwardAllowanceController.onPageLoad()
    case _ => AnyBroughtForwardAllowanceController.onPageLoad()
  }

  def lastPage(controllerId: String): UserAnswers => Call = {
    reverseRouteMap.getOrElse(controllerId, goToPageNotFound)
  }
}
