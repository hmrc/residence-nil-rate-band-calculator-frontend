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

  def transferAnyUnusedThreshold = cacheMap.getEntry[Boolean](Constants.transferAnyUnusedThresholdId)

  def transferAvailableWhenPropertyChanged = cacheMap.getEntry[Boolean](Constants.transferAvailableWhenPropertyChangedId)

  def claimDownsizingThreshold = cacheMap.getEntry[Boolean](Constants.claimDownsizingThresholdId)

  def partOfEstatePassingToDirectDescendants = cacheMap.getEntry[Boolean](Constants.partOfEstatePassingToDirectDescendantsId)

  def exemptionsAndReliefClaimed = cacheMap.getEntry[Boolean](Constants.exemptionsAndReliefClaimedId)

  def propertyPassingToDirectDescendants = cacheMap.getEntry[String](Constants.propertyPassingToDirectDescendantsId)

  def assetsPassingToDirectDescendants = cacheMap.getEntry[Int](Constants.valueOfAssetsPassingId)

  def valueBeingTransferred = cacheMap.getEntry[Int](Constants.valueBeingTransferredId)

  def valueAvailableWhenPropertyChanged = cacheMap.getEntry[Int](Constants.valueAvailableWhenPropertyChangedId)

  def chargeableEstateValue = cacheMap.getEntry[Int](Constants.chargeableEstateValueId)

  def dateOfDeath = cacheMap.getEntry[LocalDate](Constants.dateOfDeathId)

  def datePropertyWasChanged = cacheMap.getEntry[LocalDate](Constants.datePropertyWasChangedId)

  def grossingUpOnEstateProperty = cacheMap.getEntry[Boolean](Constants.grossingUpOnEstatePropertyId)

  def grossingUpOnEstateAssets = cacheMap.getEntry[Boolean](Constants.grossingUpOnEstateAssetsId)

  def downsizingDetails = cacheMap.getEntry[Date](Constants.downsizingDetails)

  def propertyInEstate = cacheMap.getEntry[Boolean](Constants.propertyInEstateId)

  def valueOfEstate = cacheMap.getEntry[Int](Constants.valueOfEstateId)

  def percentagePassedToDirectDescendants = cacheMap.getEntry[Int](Constants.percentagePassedToDirectDescendantsId)

  def chargeablePropertyValue = cacheMap.getEntry[Int](Constants.chargeablePropertyValueId)

  def chargeableInheritedPropertyValue = cacheMap.getEntry[Int](Constants.chargeableInheritedPropertyValueId)

  def propertyValue = cacheMap.getEntry[Int](Constants.propertyValueId)

  def valueOfChangedProperty = cacheMap.getEntry[Int](Constants.valueOfChangedPropertyId)
}
