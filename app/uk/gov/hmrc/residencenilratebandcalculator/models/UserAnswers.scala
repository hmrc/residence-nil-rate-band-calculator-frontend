/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.JodaReads
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class UserAnswers(cacheMap: CacheMap) extends JodaReads {

  def assetsPassingToDirectDescendants: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.assetsPassingToDirectDescendantsId)

  def transferAnyUnusedThreshold: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.transferAnyUnusedThresholdId)

  def transferAvailableWhenPropertyChanged: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.transferAvailableWhenPropertyChangedId)

  def claimDownsizingThreshold: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.claimDownsizingThresholdId)

  def partOfEstatePassingToDirectDescendants: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.partOfEstatePassingToDirectDescendantsId)

  def exemptionsAndReliefClaimed: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.exemptionsAndReliefClaimedId)

  def propertyPassingToDirectDescendants: Option[String] = cacheMap.getEntry[String](Constants.propertyPassingToDirectDescendantsId)

  def valueOfAssetsPassing: Option[Int] = cacheMap.getEntry[Int](Constants.valueOfAssetsPassingId)

  def valueBeingTransferred: Option[Int] = cacheMap.getEntry[Int](Constants.valueBeingTransferredId)

  def valueAvailableWhenPropertyChanged: Option[Int] = cacheMap.getEntry[Int](Constants.valueAvailableWhenPropertyChangedId)

  def chargeableEstateValue: Option[Int] = cacheMap.getEntry[Int](Constants.chargeableEstateValueId)

  def dateOfDeath: Option[LocalDate] = cacheMap.getEntry[LocalDate](Constants.dateOfDeathId)

  def datePropertyWasChanged: Option[LocalDate] = cacheMap.getEntry[LocalDate](Constants.datePropertyWasChangedId)

  def grossingUpOnEstateProperty: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.grossingUpOnEstatePropertyId)

  def grossingUpOnEstateAssets: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.grossingUpOnEstateAssetsId)

  def propertyInEstate: Option[Boolean] = cacheMap.getEntry[Boolean](Constants.propertyInEstateId)

  def valueOfEstate: Option[Int] = cacheMap.getEntry[Int](Constants.valueOfEstateId)

  def percentagePassedToDirectDescendants: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](Constants.percentagePassedToDirectDescendantsId)

  def chargeablePropertyValue: Option[Int] = cacheMap.getEntry[Int](Constants.chargeablePropertyValueId)

  def chargeableInheritedPropertyValue: Option[Int] = cacheMap.getEntry[Int](Constants.chargeableInheritedPropertyValueId)

  def propertyValue: Option[Int] = cacheMap.getEntry[Int](Constants.propertyValueId)

  def valueOfChangedProperty: Option[Int] = cacheMap.getEntry[Int](Constants.valueOfChangedPropertyId)

  def getPercentagePassedToDirectDescendants: BigDecimal =
    (propertyInEstate, propertyPassingToDirectDescendants) match {
    case (Some(true), Some(directToDescendants)) if directToDescendants == Constants.all => Constants.bigDecimal100
    case (Some(true), Some(directToDescendants)) if directToDescendants == Constants.some =>
      percentagePassedToDirectDescendants.map(
        _.setScale(Constants.intFour, BigDecimal.RoundingMode.HALF_UP)).fold(Constants.bigDecimalZero)(identity)
    case _ => Constants.bigDecimalZero
  }

  def isTransferAvailableWhenPropertyChanged: Option[Boolean] =
    (claimDownsizingThreshold, transferAnyUnusedThreshold, datePropertyWasChanged) match {
      case (Some(true), Some(false), _) => Some(false)
      case (Some(true), _, Some(date)) if date.isBefore(Constants.eligibilityDate) => Some(false)
      case _ => transferAvailableWhenPropertyChanged
    }
}
