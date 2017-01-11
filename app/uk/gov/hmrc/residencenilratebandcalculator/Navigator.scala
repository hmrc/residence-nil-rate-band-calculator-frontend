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
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._


@Singleton
class Navigator @Inject()() {
  private val routeMap: Map[String, CacheMap => Call] = {

    Map(
      Constants.dateOfDeathId -> (cm => getDateOfDeathRoute(cm)),
      Constants.grossEstateValueId -> (cm => getGrossEstateValueRoute(cm)),
      Constants.chargeableTransferAmountId -> (_ => EstateHasPropertyController.onPageLoad()),
      Constants.estateHasPropertyId -> (cm => getEstateHasPropertyRoute(cm)),
      Constants.propertyValueId -> (_ => PercentageCloselyInheritedController.onPageLoad()),
      Constants.percentageCloselyInheritedId -> (cm => getPercentageCloselyInheritedRoute(cm)),
      Constants.anyBroughtForwardAllowanceId -> (cm => getAnyBroughtForwardAllowanceRoute(cm)),
      Constants.broughtForwardAllowanceId -> (_ => AnyDownsizingAllowanceController.onPageLoad()),
      Constants.anyDownsizingAllowanceId -> (cm => getAnyDownsizingAllowanceRoute(cm)),
      Constants.dateOfDisposalId -> (cm => getDateOfDisposalRoute(cm)),
      Constants.anyExemptionId -> (cm => getAnyExemptionRoute(cm)),
      Constants.propertyValueAfterExemptionId -> (_ => AnyBroughtForwardAllowanceController.onPageLoad()),
      Constants.valueOfDisposedPropertyId -> (_ => AnyAssetsPassingToDirectDescendantsController.onPageLoad()),
      Constants.anyAssetsPassingToDirectDescendantsId -> (cm => getAnyAssetsPassingToDirectDescendantsRoute(cm)),
      Constants.assetsPassingToDirectDescendantsId -> (_ => AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceOnDisposalId -> (cm => getAnyBroughtForwardAllowanceOnDisposalRoute(cm)),
      Constants.broughtForwardAllowanceOnDisposalId -> (_ => ResultsController.onPageLoad())
    )
  }

  private val reverseRouteMap: Map[String, () => Call] = {
    Map(
      Constants.grossEstateValueId -> (() => DateOfDeathController.onPageLoad()),
      Constants.chargeableTransferAmountId -> (() => GrossEstateValueController.onPageLoad()),
      Constants.estateHasPropertyId -> (() => ChargeableTransferAmountController.onPageLoad()),
      Constants.propertyValueId -> (() => EstateHasPropertyController.onPageLoad()),
      Constants.percentageCloselyInheritedId -> (() => PropertyValueController.onPageLoad()),
      Constants.anyExemptionId -> (() => PercentageCloselyInheritedController.onPageLoad()),
      Constants.propertyValueAfterExemptionId -> (() => AnyExemptionController.onPageLoad()),
      Constants.anyBroughtForwardAllowanceId -> (() => PropertyValueAfterExemptionController.onPageLoad())
    )
  }

  private def getDateOfDeathRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[LocalDate](Constants.dateOfDeathId) match {
      case Some(d) if (d isEqual Constants.eligibilityDate) || (d isAfter Constants.eligibilityDate) => GrossEstateValueController.onPageLoad()
      case Some(_) => TransitionOutController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getEstateHasPropertyRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Boolean](Constants.estateHasPropertyId) match {
      case Some(true) => PropertyValueController.onPageLoad()
      case Some(false) => AnyDownsizingAllowanceController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getGrossEstateValueRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Int](Constants.grossEstateValueId) match {
      case Some(v) if v > Constants.maxGrossEstateValue => TransitionOutController.onPageLoad()
      case Some(_) => ChargeableTransferAmountController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getAnyBroughtForwardAllowanceRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Boolean](Constants.anyBroughtForwardAllowanceId) match {
      case Some(true) => BroughtForwardAllowanceController.onPageLoad()
      case Some(false) => AnyDownsizingAllowanceController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getAnyBroughtForwardAllowanceOnDisposalRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Boolean](Constants.anyBroughtForwardAllowanceOnDisposalId) match {
      case Some(true) => BroughtForwardAllowanceOnDisposalController.onPageLoad()
      case Some(false) => ResultsController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getAnyDownsizingAllowanceRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Boolean](Constants.anyDownsizingAllowanceId) match {
      case Some(true) => DateOfDisposalController.onPageLoad()
      case Some(false) => ResultsController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getDateOfDisposalRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[LocalDate](Constants.dateOfDisposalId) match {
      case Some(d) if (d isEqual Constants.downsizingEligibilityDate) || (d isAfter Constants.downsizingEligibilityDate) => ValueOfDisposedPropertyController.onPageLoad()
      case Some(_) => TransitionOutController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getPercentageCloselyInheritedRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Int](Constants.percentageCloselyInheritedId) match {
      case Some(x) if x == 0 => AnyBroughtForwardAllowanceController.onPageLoad()
      case Some(_) => AnyExemptionController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getAnyExemptionRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Boolean](Constants.anyExemptionId) match {
      case Some(true) => PropertyValueAfterExemptionController.onPageLoad()
      case Some(false) => AnyBroughtForwardAllowanceController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  private def getAnyAssetsPassingToDirectDescendantsRoute(cacheMap: CacheMap) = {
    cacheMap.getEntry[Boolean](Constants.anyAssetsPassingToDirectDescendantsId) match {
      case Some(true) => AssetsPassingToDirectDescendantsController.onPageLoad()
      case Some(false) => ResultsController.onPageLoad()
      case None => HomeController.onPageLoad()
    }
  }

  def nextPage(controllerId: String): CacheMap => Call = {
    routeMap.getOrElse(controllerId, _ => PageNotFoundController.onPageLoad())
  }

  def lastPage(controllerId: String): () => Call = {
    reverseRouteMap.getOrElse(controllerId, () => PageNotFoundController.onPageLoad())
  }
}
