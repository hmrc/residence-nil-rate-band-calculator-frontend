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
      Constants.dateOfDeathId -> (cd => getDateOfDeathRoute(cd)),
      Constants.grossEstateValueId -> (_ => ChargeableTransferAmountController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (_ => EstateHasPropertyController.onPageLoad()),
      Constants.estateHasPropertyId -> (cd => getEstateHasPropertyRoute(cd)),
      Constants.propertyValueId -> (_ => AnyPropertyCloselyInheritedController.onPageLoad()),
      Constants.anyPropertyCloselyInheritedId -> (cd => getAnyPropertyCloselyInheritedRoute(cd)),
      Constants.percentageCloselyInheritedId -> (_ => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (cd => getAnyBroughtForwardAllowanceRoute(cd)),
      Constants.broughtForwardAllowanceId -> (_ => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (cd => getAnyDownsizingAllowanceRoute(cd)),
      Constants.dateOfDisposalId -> (cd => getDateOfDisposalRoute(cd)),
      Constants.anyExemptionId -> (cd => getAnyExemptionRoute(cd)),
      Constants.propertyValueAfterExemptionId -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.valueOfDisposedPropertyId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (cd => getAnyAssetsPassingToDirectDescendantsRoute(cd)),
      Constants.assetsPassingToDirectDescendantsId -> (_ => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (cd => getAnyBroughtForwardAllowanceOnDisposalRoute(cd)),
      Constants.broughtForwardAllowanceOnDisposalId -> (_ => CheckAnswersController.onPageLoad()),
      Constants.checkAnswersId -> (_ => ResultsController.onPageLoad()),
      Constants.purposeOfUseId -> (cd => getPurposeOfUseRoute(cd))
    )
  }

  private val reverseRouteMap: Map[String, () => Call] = {
    Map(
      Constants.grossEstateValueId -> (() => DateOfDeathController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (() => GrossEstateValueController.onPageLoad()),
      Constants.estateHasPropertyId -> (() => ChargeableTransferAmountController.onPageLoad()),
      Constants.propertyValueId -> (() => EstateHasPropertyController.onPageLoad()),
      Constants.anyPropertyCloselyInheritedId -> (() => PropertyValueController.onPageLoad()),
      Constants.percentageCloselyInheritedId -> (() => AnyPropertyCloselyInheritedController.onPageLoad()),
      Constants.anyExemptionId -> (() => PercentageCloselyInheritedController.onPageLoad()),
      Constants.propertyValueAfterExemptionId -> (() => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (() => PropertyValueAfterExemptionController.onPageLoad()),
      Constants.broughtForwardAllowanceId -> (() => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (() => BroughtForwardAllowanceController.onPageLoad()),
      Constants.dateOfDisposalId -> (() => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.valueOfDisposedPropertyId -> (() => DateOfDisposalController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (() => ValueOfDisposedPropertyController.onPageLoad()),
      Constants.assetsPassingToDirectDescendantsId -> (() => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (() => AssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.broughtForwardAllowanceOnDisposalId -> (() => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad())
    )
  }

  private def getDateOfDeathRoute(cacheData: UserAnswers) = cacheData.dateOfDeath match {
    case Some(d) if (d isEqual Constants.eligibilityDate) || (d isAfter Constants.eligibilityDate) => GrossEstateValueController.onPageLoad()
    case Some(_) => TransitionOutController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getEstateHasPropertyRoute(cacheData: UserAnswers) = cacheData.estateHasProperty match {
    case Some(true) => PropertyValueController.onPageLoad()
    case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyBroughtForwardAllowanceRoute(cacheData: UserAnswers) = cacheData.anyBroughtForwardAllowance match {
    case Some(true) => BroughtForwardAllowanceController.onPageLoad()
    case Some(false) => AnyDownsizingAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyBroughtForwardAllowanceOnDisposalRoute(cacheData: UserAnswers) = cacheData.anyBroughtForwardAllowanceOnDisposal match {
    case Some(true) => BroughtForwardAllowanceOnDisposalController.onPageLoad()
    case Some(false) => CheckAnswersController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyDownsizingAllowanceRoute(cacheData: UserAnswers) = cacheData.anyDownsizingAllowance match {
    case Some(true) => DateOfDisposalController.onPageLoad()
    case Some(false) => CheckAnswersController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getDateOfDisposalRoute(cacheData: UserAnswers) = cacheData.dateOfDisposal match {
    case Some(d) if (d isEqual Constants.downsizingEligibilityDate) || (d isAfter Constants.downsizingEligibilityDate) => ValueOfDisposedPropertyController.onPageLoad()
    case Some(_) => TransitionOutController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyPropertyCloselyInheritedRoute(cacheData: UserAnswers) = cacheData.anyPropertyCloselyInherited match {
    case Some(true) => PercentageCloselyInheritedController.onPageLoad()
    case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyExemptionRoute(cacheData: UserAnswers) = cacheData.anyExemption match {
    case Some(true) => PropertyValueAfterExemptionController.onPageLoad()
    case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getAnyAssetsPassingToDirectDescendantsRoute(cacheData: UserAnswers) = cacheData.anyAssetsPassingToDirectDescendants match {
    case Some(true) => AssetsPassingToDirectDescendantsController.onPageLoad()
    case Some(false) => CheckAnswersController.onPageLoad()
    case None => HomeController.onPageLoad()
  }

  private def getPurposeOfUseRoute(cacheData: UserAnswers) = cacheData.purposeOfUse match {
    case Some(Constants.dealingWithEstate) => DateOfDeathController.onPageLoad()
    case Some(Constants.planning) => PlanningController.onPageLoad()
    case _ => HomeController.onPageLoad()
  }

  def nextPage(controllerId: String): UserAnswers => Call = {
    routeMap.getOrElse(controllerId, _ => PageNotFoundController.onPageLoad())
  }

  def lastPage(controllerId: String): () => Call = {
    reverseRouteMap.getOrElse(controllerId, () => PageNotFoundController.onPageLoad())
  }
}
