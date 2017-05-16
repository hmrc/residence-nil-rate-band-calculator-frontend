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
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, Constants}

class UserAnswersSpec extends BaseSpec {

  val cacheMapKey = "aa"
  
  "User Answers" when {

    "values exist in the cache map" must {

      "return the correct answer for Any Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.assetsPassingToDirectDescendantsId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.assetsPassingToDirectDescendants shouldBe Some(true)
      }

      "return the correct answer for Transfer Any Unused Allowance" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.transferAnyUnusedThresholdId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.transferAnyUnusedThreshold shouldBe Some(true)
      }

      "return the correct answer for Transfer Available When Property Changed" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.transferAvailableWhenPropertyChanged shouldBe Some(true)
      }

      "return the correct answer for Claim Downsizing Threshold" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.claimDownsizingThresholdId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.claimDownsizingThreshold shouldBe Some(true)
      }

      "return the correct answer for Exemptions And Relief Claimed" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.exemptionsAndReliefClaimedId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.exemptionsAndReliefClaimed shouldBe Some(true)
      }

      "return the correct answer for Property Passing To Direct Descendants" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.propertyPassingToDirectDescendantsId -> JsString("abc")))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyPassingToDirectDescendants shouldBe Some("abc")
      }

      "return the correct answer for Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.valueOfAssetsPassingId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueOfAssetsPassing shouldBe Some(1)
      }

      "return the correct answer for Value Being Transferred" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.valueBeingTransferredId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueBeingTransferred shouldBe Some(1)
      }

      "return the correct answer for Value Available When Property Changed" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.valueAvailableWhenPropertyChangedId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueAvailableWhenPropertyChanged shouldBe Some(1)
      }

      "return the correct answer for Chargeable Estate Value" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.chargeableEstateValueId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.chargeableEstateValue shouldBe Some(1)
      }

      "return the correct answer for Date Of Death" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.dateOfDeathId -> JsString("2018-01-01")))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.dateOfDeath shouldBe Some(new LocalDate(2018, 1, 1))
      }

      "return the correct answer for Date Property Was Changed" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.datePropertyWasChangedId -> JsString("2018-01-01")))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.datePropertyWasChanged shouldBe Some(new LocalDate(2018, 1, 1))
      }

      "return the correct answer for Grossing Up On Estate Assets" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.grossingUpOnEstateAssetsId -> JsBoolean(false)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.grossingUpOnEstateAssets shouldBe Some(false)
      }

      "return the correct answer for Grossing Up On Estate Property" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.grossingUpOnEstatePropertyId -> JsBoolean(false)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.grossingUpOnEstateProperty shouldBe Some(false)
      }

      "return the correct answer for Property In Estate" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.propertyInEstateId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyInEstate shouldBe Some(true)
      }

      "return the correct answer for Value Of Estate" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.valueOfEstateId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueOfEstate shouldBe Some(1)
      }

      "return the correct answer for Percentage Passed To Direct Descendants" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.percentagePassedToDirectDescendantsId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.percentagePassedToDirectDescendants shouldBe Some(1)
      }

      "return the correct answer for Property Value" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.propertyValueId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyValue shouldBe Some(1)
      }

      "return the correct answer for Property Value After Exemption" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.chargeablePropertyValueId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.chargeablePropertyValue shouldBe Some(1)
      }

      "return the correct answer for Property Value After Exemption Closely Inherited" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.chargeableInheritedPropertyValueId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.chargeableInheritedPropertyValue shouldBe Some(1)
      }

      "return the correct answer for Value Of Changed Property" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.valueOfChangedPropertyId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueOfChangedProperty shouldBe Some(1)
      }

      "return true from isTransferAvailableWhenPropertyChanged when " +
        "claim downsizing threshold is answered yes and " +
        "transferAnyUnusedThreshold is answered yes and " +
        "datePropertyWasChanged is answered with a date equal to eligibility date and " +
        "transferAvailableWhenPropertyChanged is answered yes" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.claimDownsizingThresholdId -> JsBoolean(true),
            Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
            Constants.datePropertyWasChangedId -> JsString("2017-04-06"),
            Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true)
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.isTransferAvailableWhenPropertyChanged shouldBe Some(true)
      }

      "return false from isTransferAvailableWhenPropertyChanged when " +
        "claim downsizing threshold is answered yes and " +
        "transferAnyUnusedThreshold is answered yes and " +
        "datePropertyWasChanged is answered with a date before eligibility date and " +
        "transferAvailableWhenPropertyChanged is answered yes" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.claimDownsizingThresholdId -> JsBoolean(true),
            Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
            Constants.datePropertyWasChangedId -> JsString("2017-04-05"),
            Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true)
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.isTransferAvailableWhenPropertyChanged shouldBe Some(false)
      }

      "return false from isTransferAvailableWhenPropertyChanged when " +
        "claim downsizing threshold is answered yes and " +
        "transferAnyUnusedThreshold is answered no and " +
        "transferAvailableWhenPropertyChanged is answered yes" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.claimDownsizingThresholdId -> JsBoolean(true),
            Constants.transferAnyUnusedThresholdId -> JsBoolean(false),
            Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true)
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.isTransferAvailableWhenPropertyChanged shouldBe Some(false)
      }

      "return transferAvailableWhenPropertyChanged from isTransferAvailableWhenPropertyChanged when " +
        "claim downsizing threshold is answered no" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.claimDownsizingThresholdId -> JsBoolean(false),
            Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true)
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.isTransferAvailableWhenPropertyChanged shouldBe Some(true)
      }

      "return 100 from getPercentagePassedToDirectDescendants when " +
        "propertyInEstate is true and " +
        "propertyPassingToDirectDescendants is all and" +
        "percentagePassedToDirectDescendants is 60.9999" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.propertyInEstateId -> JsBoolean(true),
            Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.all),
            Constants.percentagePassedToDirectDescendantsId -> JsString("60.9999")
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.getPercentagePassedToDirectDescendants shouldBe Constants.bigDecimal100
      }

      "return 60.9999 from getPercentagePassedToDirectDescendants when " +
        "propertyInEstate is true and " +
        "propertyPassingToDirectDescendants is some and" +
        "percentagePassedToDirectDescendants is 60.9999" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.propertyInEstateId -> JsBoolean(true),
            Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.some),
            Constants.percentagePassedToDirectDescendantsId -> JsString("60.9999")
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.getPercentagePassedToDirectDescendants shouldBe BigDecimal(60.9999)
      }

      "return 0 from getPercentagePassedToDirectDescendants when " +
        "propertyInEstate is false and " +
        "propertyPassingToDirectDescendants is some and" +
        "percentagePassedToDirectDescendants is 60.9999" in {
        val cacheMap = CacheMap(
          cacheMapKey, Map(
            Constants.propertyInEstateId -> JsBoolean(false),
            Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.some),
            Constants.percentagePassedToDirectDescendantsId -> JsString("60.9999")
          )
        )
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.getPercentagePassedToDirectDescendants shouldBe BigDecimal(0)
      }
    }

    "values don't exist in the cache map" must {
      val emptyCacheMap = CacheMap(cacheMapKey, Map())

      "return None for Any Assets Passing To Direct Descendant" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.assetsPassingToDirectDescendants shouldBe None
      }

      "return None for Transfer Any Unused Allowance" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.transferAnyUnusedThreshold shouldBe None
      }

      "return None for Transfer Available When Property Changed" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.transferAvailableWhenPropertyChanged shouldBe None
      }

      "return None for Claim Downsizing Threshold" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.claimDownsizingThreshold shouldBe None
      }

      "return None for Exemptions And Relief Claimed" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.exemptionsAndReliefClaimed shouldBe None
      }

      "return None for Property Passing To Direct Descendants" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.propertyPassingToDirectDescendants shouldBe None
      }

      "return None for Assets Passing To Direct Descendant" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.valueOfAssetsPassing shouldBe None
      }

      "return None for Value Being Transferred" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.valueBeingTransferred shouldBe None
      }

      "return None for Value Available When Property Changed" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.valueAvailableWhenPropertyChanged shouldBe None
      }

      "return None for Chargeable Estate Value" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.chargeableEstateValue shouldBe None
      }

      "return None for Date Of Death" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.dateOfDeath shouldBe None
      }

      "return None for Date Property Was Changed" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.datePropertyWasChanged shouldBe None
      }

      "return None for Property In Estate" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.propertyInEstate shouldBe None
      }

      "return None for Value Of Estate" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.valueOfEstate shouldBe None
      }

      "return None for Percentage Passed To Direct Descendants" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.percentagePassedToDirectDescendants shouldBe None
      }

      "return None for Property Value" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.propertyValue shouldBe None
      }

      "return None for Property Value After Exemption" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.chargeablePropertyValue shouldBe None
      }

      "return None for Value Of Changed Property" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.valueOfChangedProperty shouldBe None
      }


      "return None from isTransferAvailableWhenPropertyChanged" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.isTransferAvailableWhenPropertyChanged shouldBe None
      }

      "return 0 from getPercentagePassedToDirectDescendants" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.getPercentagePassedToDirectDescendants shouldBe BigDecimal(0)
      }
    }
  }
}
