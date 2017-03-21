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
    Constants.grossEstateValueId -> JsNumber(testNumber),
    Constants.chargeableTransferAmountId -> JsNumber(testNumber),
    Constants.estateHasPropertyId -> JsBoolean(true),
    Constants.propertyValueId -> JsNumber(testNumber),
    Constants.anyPropertyCloselyInheritedId -> JsString(Constants.some),
    Constants.percentageCloselyInheritedId -> JsNumber(testNumber),
    Constants.anyExemptionId -> JsBoolean(true),
    Constants.doesGrossingUpApplyToResidenceId -> JsBoolean(false),
    Constants.chargeableValueOfResidenceId -> JsNumber(testNumber),
    Constants.chargeableValueOfResidenceCloselyInheritedId -> JsNumber(testNumber),
    Constants.anyBroughtForwardAllowanceId -> JsBoolean(true),
    Constants.broughtForwardAllowanceId -> JsNumber(testNumber),
    Constants.anyDownsizingAllowanceId -> JsBoolean(true),
    Constants.dateOfDisposalId -> JsString("2019-01-01"),
    Constants.valueOfDisposedPropertyId -> JsNumber(testNumber),
    Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true),
    Constants.doesGrossingUpApplyToOtherPropertyId -> JsBoolean(false),
    Constants.assetsPassingToDirectDescendantsId -> JsNumber(testNumber),
    Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true),
    Constants.broughtForwardAllowanceOnDisposalId -> JsNumber(testNumber)
  ))

  "Cascade Upsert" when {

    "asked to save the answer 'false' for Estate Has Property" must {

      "delete the existing 'Property Value'" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.propertyValueId
      }

      "delete the existing 'Any Property Closely Inherited'" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyPropertyCloselyInheritedId
      }

      "delete the existing 'Percentage Closely Inherited'" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.percentageCloselyInheritedId
      }

      "delete the existing 'Any Exemption' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyExemptionId
      }

      "delete the existing 'Chargeable Value of Residence' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableValueOfResidenceId
      }

      "delete the existing 'Chargeable Value of Residence Closely Inherited' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableValueOfResidenceCloselyInheritedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 6
      }
    }

    "asked to save the answer 'true' for Estate Has Property" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.estateHasPropertyId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the answer 'none' for Any Property Closely Inherited" must {
      "delete the existing 'Percentage Closely Inherited' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.percentageCloselyInheritedId
      }

      "delete the existing 'Any Exemption' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyExemptionId
      }

      "delete the existing 'Chargeable Value of Residence' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableValueOfResidenceId
      }

      "delete the existing 'Chargeable Value of Residence Closely Inherited' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableValueOfResidenceCloselyInheritedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.none, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 4
      }
    }

    "asked to save the answer 'all' for Any Property Closely Inherited" must {

      "delete the existing 'Percentage Closely Inherited' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.all, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.percentageCloselyInheritedId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.all, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 1
      }
    }

    "asked to save the answer 'some' for Any Property Closely Inherited" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyPropertyCloselyInheritedId, Constants.some, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the answer 'false' for Any Exemptions" must {

      "delete the existing 'Chargeable Value of Residence' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyExemptionId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableValueOfResidenceId
      }

      "delete the existing 'Chargeable Value of Residence Closely Inherited' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyExemptionId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.chargeableValueOfResidenceCloselyInheritedId
      }

      "delete the existing 'Does Grossing Up Apply To Residence' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyExemptionId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.doesGrossingUpApplyToResidenceId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyExemptionId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 3
      }
    }

    "asked to save the answer 'true' for Any Exemptions" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyExemptionId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Any Brought Forward Allowance" must {

      "delete the existing 'Brought Forward Allowance' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceId
      }

      "delete the existing 'Any Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyBroughtForwardAllowanceOnDisposalId
      }

      "delete the existing 'Brought Forward Allowance on DIsposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceOnDisposalId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 3
      }
    }

    "asked to save the value 'true' for Any Brought Forward Allowance" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Any Downsizing Allowance" must {

      "delete the existing 'Date of Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.dateOfDisposalId
      }

      "delete the existing 'Value of Disposed Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfDisposedPropertyId
      }

      "delete the existing 'Any Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyAssetsPassingToDirectDescendantsId
      }

      "delete the existing 'Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.assetsPassingToDirectDescendantsId
      }

      "delete the existing 'Any Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyBroughtForwardAllowanceOnDisposalId
      }

      "delete the existing 'Does Grossing Up Apply to Other Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.doesGrossingUpApplyToOtherPropertyId
      }

      "delete the existing 'Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceOnDisposalId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 7
      }
    }

    "asked to save the value 'true' for Any Downsizing Allowance" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyDownsizingAllowanceId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save the value 'false' for Any Assets Passing to Direct Descendants" must {

      "delete the existing 'Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.assetsPassingToDirectDescendantsId
      }

      "delete the existing 'Any Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyBroughtForwardAllowanceOnDisposalId
      }

      "delete the existing 'Does Grossing Up Apply to Other Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.doesGrossingUpApplyToOtherPropertyId
      }

      "delete the existing 'Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyAssetsPassingToDirectDescendantsId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceOnDisposalId
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

    "asked to save the value 'false' for Any Brought Forward Allowance on Disposal" must {

      "delete the existing 'Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceOnDisposalId, false, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceOnDisposalId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceOnDisposalId, false, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 1
      }
    }

    "asked to save the value 'true' for Any Brought Forward Allowance on Disposal" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.anyBroughtForwardAllowanceOnDisposalId, true, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }

    "asked to save a value before 8 July 2015 for Date of Disposal" must {
      "delete the existing 'Value of Disposed Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.valueOfDisposedPropertyId
      }

      "delete the existing 'Any Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyAssetsPassingToDirectDescendantsId
      }

      "delete the existing 'Assets Passing to Direct Descendants' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.assetsPassingToDirectDescendantsId
      }

      "delete the existing 'Any Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyBroughtForwardAllowanceOnDisposalId
      }

      "delete the existing 'Does Grossing Up Apply to Other Property' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.doesGrossingUpApplyToOtherPropertyId
      }

      "delete the existing 'Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceOnDisposalId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeDownsizingEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 6
      }
    }

    "asked to save a value between 8 July 2015 and 5 April 2017 for Date of Disposal" must {

      "delete the existing 'Any Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.anyBroughtForwardAllowanceOnDisposalId
      }

      "delete the existing 'Brought Forward Allowance on Disposal' answer" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys should not contain Constants.broughtForwardAllowanceOnDisposalId
      }

      "not delete any other answers" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, oneDayBeforeEligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size - 2
      }
    }

    "asked to save a value on or after 6 April 2017 for Date of Disposal" must {

      "not delete existing answers for other questions" in {
        val updatedCacheMap = (new CascadeUpsert)(Constants.dateOfDisposalId, Constants.eligibilityDate, fullCacheMap)
        updatedCacheMap.data.keys.size shouldBe fullCacheMap.data.keys.size
      }
    }
  }
}
