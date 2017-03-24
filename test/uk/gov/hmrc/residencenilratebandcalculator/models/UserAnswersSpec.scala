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
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class UserAnswersSpec extends UnitSpec {

  val cacheMapKey = "aa"
  
  "User Answers" when {

    "values exist in the cache map" must {

      "return the correct answer for Any Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyAssetsPassingToDirectDescendants shouldBe Some(true)
      }

      "return the correct answer for Any Brought Forward Allowance" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.anyBroughtForwardAllowanceId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyBroughtForwardAllowance shouldBe Some(true)
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
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.assetsPassingToDirectDescendantsId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.assetsPassingToDirectDescendants shouldBe Some(1)
      }

      "return the correct answer for Brought Forward Allowance" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.broughtForwardAllowanceId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.broughtForwardAllowance shouldBe Some(1)
      }

      "return the correct answer for Brought Forward Allowance On Disposal" in {
        val cacheMap = CacheMap(cacheMapKey, Map(Constants.broughtForwardAllowanceOnDisposalId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.broughtForwardAllowanceOnDisposal shouldBe Some(1)
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
    }

    "values don't exist in the cache map" must {
      val emptyCacheMap = CacheMap(cacheMapKey, Map())

      "return None for Any Assets Passing To Direct Descendant" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.anyAssetsPassingToDirectDescendants shouldBe None
      }

      "return None for Any Brought Forward Allowance" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.anyBroughtForwardAllowance shouldBe None
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
        userAnswers.assetsPassingToDirectDescendants shouldBe None
      }

      "return None for Brought Forward Allowance" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.broughtForwardAllowance shouldBe None
      }

      "return None for Brought Forward Allowance On Disposal" in {
        val userAnswers = new UserAnswers(emptyCacheMap)
        userAnswers.broughtForwardAllowanceOnDisposal shouldBe None
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
    }
  }
}
