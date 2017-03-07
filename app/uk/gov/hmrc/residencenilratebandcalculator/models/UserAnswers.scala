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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.joda.time.LocalDate
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class UserAnswers(cacheMap: CacheMap) {

  def anyAssetsPassingToDirectDescendants = cacheMap.getEntry[Boolean](Constants.anyAssetsPassingToDirectDescendantsId)

  def anyBroughtForwardAllowance = cacheMap.getEntry[Boolean](Constants.anyBroughtForwardAllowanceId)

  def anyBroughtForwardAllowanceOnDisposal = cacheMap.getEntry[Boolean](Constants.anyBroughtForwardAllowanceOnDisposalId)

  def anyDownsizingAllowance = cacheMap.getEntry[Boolean](Constants.anyDownsizingAllowanceId)

  def anyEstatePassedToDescendants = cacheMap.getEntry[Boolean](Constants.anyEstatePassedToDescendantsId)

  def anyExemption = cacheMap.getEntry[Boolean](Constants.anyExemptionId)

  def anyPropertyCloselyInherited = cacheMap.getEntry[String](Constants.anyPropertyCloselyInheritedId)

  def assetsPassingToDirectDescendants = cacheMap.getEntry[Int](Constants.assetsPassingToDirectDescendantsId)

  def broughtForwardAllowance = cacheMap.getEntry[Int](Constants.broughtForwardAllowanceId)

  def broughtForwardAllowanceOnDisposal = cacheMap.getEntry[Int](Constants.broughtForwardAllowanceOnDisposalId)

  def chargeableTransferAmount = cacheMap.getEntry[Int](Constants.chargeableTransferAmountId)

  def checkAnswers = cacheMap.getEntry[Int](Constants.checkAnswersId)

  def dateOfDeath = cacheMap.getEntry[LocalDate](Constants.dateOfDeathId)

  def dateOfDisposal = cacheMap.getEntry[LocalDate](Constants.dateOfDisposalId)

  def doesGrossingUpApplyToResidence = cacheMap.getEntry[Boolean](Constants.doesGrossingUpApplyToResidenceId)

  def doesGrossingUpApplyToOtherProperty = cacheMap.getEntry[Boolean](Constants.doesGrossingUpApplyToOtherPropertyId)

  def downsizingDetails = cacheMap.getEntry[Date](Constants.downsizingDetails)

  def estateHasProperty = cacheMap.getEntry[Boolean](Constants.estateHasPropertyId)

  def grossEstateValue = cacheMap.getEntry[Int](Constants.grossEstateValueId)

  def percentageCloselyInherited = cacheMap.getEntry[Int](Constants.percentageCloselyInheritedId)

  def chargeableValueOfResidence = cacheMap.getEntry[Int](Constants.chargeableValueOfResidenceId)

  def chargeableValueOfResidenceCloselyInherited = cacheMap.getEntry[Int](Constants.chargeableValueOfResidenceCloselyInheritedId)

  def propertyValue = cacheMap.getEntry[Int](Constants.propertyValueId)

  def valueOfDisposedProperty = cacheMap.getEntry[Int](Constants.valueOfDisposedPropertyId)
}
