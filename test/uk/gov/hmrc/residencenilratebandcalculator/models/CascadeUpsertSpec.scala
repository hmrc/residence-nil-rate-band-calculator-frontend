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

import play.api.libs.json.{JsBoolean, JsNumber, JsString}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.residencenilratebandcalculator.Constants

class CascadeUpsertSpec extends UnitSpec {

  val cacheMapKey = "a"
  val testNumber = 123
  val newTestNumber = 456
  val oneDayBeforeDownsizingEligibilityDate = Constants.downsizingEligibilityDate.minusDays(1)
  val oneDayBeforeEligibilityDate = Constants.eligibilityDate.minusDays(1)

  val fullCacheMap = CacheMap(cacheMapKey, Map(
    Constants.dateOfDeathId -> JsString("2020-01-01"),
    Constants.partOfEstatePassingToDirectDescendantsId -> JsBoolean(true),
    Constants.valueOfEstateId -> JsNumber(testNumber),
    Constants.chargeableEstateValueId -> JsNumber(testNumber),
    Constants.propertyInEstateId -> JsBoolean(true),
    Constants.propertyValueId -> JsNumber(testNumber),
    Constants.propertyPassingToDirectDescendantsId -> JsString(Constants.some),
    Constants.percentagePassedToDirectDescendantsId -> JsNumber(testNumber),
    Constants.exemptionsAndReliefClaimedId -> JsBoolean(true),
    Constants.grossingUpOnEstatePropertyId -> JsBoolean(false),
    Constants.chargeablePropertyValueId -> JsNumber(testNumber),
    Constants.chargeableInheritedPropertyValueId -> JsNumber(testNumber),
    Constants.transferAnyUnusedThresholdId -> JsBoolean(true),
    Constants.valueBeingTransferredId -> JsNumber(testNumber),
    Constants.claimDownsizingThresholdId -> JsBoolean(true),
    Constants.datePropertyWasChangedId -> JsString("2019-01-01"),
    Constants.valueOfChangedPropertyId -> JsNumber(testNumber),
    Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
    Constants.grossingUpOnEstateAssetsId -> JsBoolean(false),
    Constants.valueOfAssetsPassingId -> JsNumber(testNumber),
    Constants.transferAvailableWhenPropertyChangedId -> JsBoolean(true),
    Constants.valueAvailableWhenPropertyChangedId -> JsNumber(testNumber)
  ))

  "Cascade Upsert" when {

    "asked to save the answer 'false' for Property In Estate" must {

      "delete the existing 'Property Value'" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.propertyValueId
      }

      "delete the existing 'Property Passing To Direct Descendants'" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.propertyPassingToDirectDescendantsId
      }

      "delete the existing 'Percentage Passed To Direct Descendants'" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.percentagePassedToDirectDescendantsId
      }

      "delete the existing 'Exemptions And Relief Claimed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.exemptionsAndReliefClaimedId
      }

      "delete the existing 'Chargeable Property Value' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeablePropertyValueId
      }

      "delete the existing 'Chargeable Inherited Property Value' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableInheritedPropertyValueId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 6
      }
    }

    "asked to save the answer 'true' for Property In Estate" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyInEstateId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the answer 'none' for Property Passing To Direct Descendants" must {
      "delete the existing 'Percentage Passed To Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.percentagePassedToDirectDescendantsId
      }

      "delete the existing 'Exemptions And Relief Claimed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.exemptionsAndReliefClaimedId
      }

      "delete the existing 'Chargeable Property Value' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeablePropertyValueId
      }

      "delete the existing 'Chargeable Inherited Property Value' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableInheritedPropertyValueId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 4
      }
    }

    "asked to save the answer 'all' for Property Passing To Direct Descendants" must {

      "delete the existing 'Percentage Passed To Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.all, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.percentagePassedToDirectDescendantsId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.all, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 1
      }
    }

    "asked to save the answer 'some' for Property Passing To Direct Descendants" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.propertyPassingToDirectDescendantsId, Constants.some, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the answer 'false' for exemptions and relief claimed" must {

      "delete the existing 'Chargeable Property Value' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.exemptionsAndReliefClaimedId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeablePropertyValueId
      }

      "delete the existing 'Chargeable Inherited Property Value' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.exemptionsAndReliefClaimedId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableInheritedPropertyValueId
      }

      "delete the existing 'Grossing Up On Estate Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.exemptionsAndReliefClaimedId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.grossingUpOnEstatePropertyId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.exemptionsAndReliefClaimedId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 3
      }
    }

    "asked to save the answer 'true' for exemptions and relief claimed" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.exemptionsAndReliefClaimedId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Transfer Any Unused Allowance" must {

      "delete the existing 'Value Being Transferred' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAnyUnusedThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueBeingTransferredId
      }

      "delete the existing 'Transfer Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAnyUnusedThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.transferAvailableWhenPropertyChangedId
      }

      "delete the existing 'Value Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAnyUnusedThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueAvailableWhenPropertyChangedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAnyUnusedThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 3
      }
    }

    "asked to save the value 'true' for Transfer Any Unused Allowance" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAnyUnusedThresholdId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Claim Downsizing Threshold" must {

      "delete the existing 'Date Property Was Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.datePropertyWasChangedId
      }

      "delete the existing 'Value Of Changed Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfChangedPropertyId
      }

      "delete the existing 'Any Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyAssetsPassingToDirectDescendantsId
      }

      "delete the existing 'Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfAssetsPassingId
      }

      "delete the existing 'Transfer Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.transferAvailableWhenPropertyChangedId
      }

      "delete the existing 'Grossing Up On Estate Assets' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.grossingUpOnEstateAssetsId
      }

      "delete the existing 'Value Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueAvailableWhenPropertyChangedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 7
      }
    }

    "asked to save the value 'true' for Claim Downsizing Threshold" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.claimDownsizingThresholdId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Any Assets Passing to Direct Descendants" must {

      "delete the existing 'Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfAssetsPassingId
      }

      "delete the existing 'Transfer Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.transferAvailableWhenPropertyChangedId
      }

      "delete the existing 'Grossing Up On Estate Assets' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.grossingUpOnEstateAssetsId
      }

      "delete the existing 'Value Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueAvailableWhenPropertyChangedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 4
      }
    }

    "asked to save the value 'true' for Any Assets Passing to Direct Descendants" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Transfer Available When Property Changed" must {

      "delete the existing 'Value Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAvailableWhenPropertyChangedId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueAvailableWhenPropertyChangedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAvailableWhenPropertyChangedId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 1
      }
    }

    "asked to save the value 'true' for Transfer Available When Property Changed" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.transferAvailableWhenPropertyChangedId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save a value before 8 July 2015 for Date Property Was Changed" must {
      "delete the existing 'Value Of Changed Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfChangedPropertyId
      }

      "delete the existing 'Any Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyAssetsPassingToDirectDescendantsId
      }

      "delete the existing 'Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfAssetsPassingId
      }

      "delete the existing 'Transfer Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.transferAvailableWhenPropertyChangedId
      }

      "delete the existing 'Grossing Up On Estate Assets' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.grossingUpOnEstateAssetsId
      }

      "delete the existing 'Value Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueAvailableWhenPropertyChangedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 6
      }
    }

    "asked to save a value between 8 July 2015 and 5 April 2017 for Date Property Was Changed" must {

      "delete the existing 'Transfer Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.transferAvailableWhenPropertyChangedId
      }

      "delete the existing 'Value Available When Property Changed' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueAvailableWhenPropertyChangedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, oneDayBeforeEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 2
      }
    }

    "asked to save a value on or after 6 April 2017 for Date Property Was Changed" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.datePropertyWasChangedId, Constants.eligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }
  }
}
