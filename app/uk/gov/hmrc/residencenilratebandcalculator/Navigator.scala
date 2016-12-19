/*
 * Copyright 2016 HM Revenue & Customs
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
      Constants.percentageCloselyInheritedId -> (_ => ResultsController.onPageLoad())
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
      case Some(false) => TransitionOutController.onPageLoad()
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

  def nextPage(controllerId: String): CacheMap => Call = {
    routeMap.getOrElse(controllerId, _ => PageNotFoundController.onPageLoad())
  }
}
