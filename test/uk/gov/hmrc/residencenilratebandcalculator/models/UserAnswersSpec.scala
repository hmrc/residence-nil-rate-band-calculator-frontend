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

  "User Answers" when {

    "values exist in the cache map" must {

      "return the correct answer for Any Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap("a", Map(Constants.anyAssetsPassingToDirectDescendantsId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyAssetsPassingToDirectDescendants shouldBe Some(true)
      }

      "return the correct answer for Any Brought Forward Allowance" in {
        val cacheMap = CacheMap("a", Map(Constants.anyBroughtForwardAllowanceId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyBroughtForwardAllowance shouldBe Some(true)
      }

      "return the correct answer for Any Brought Forward Allowance On Disposal" in {
        val cacheMap = CacheMap("a", Map(Constants.anyBroughtForwardAllowanceOnDisposalId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyBroughtForwardAllowanceOnDisposal shouldBe Some(true)
      }

      "return the correct answer for Any Downsizing Allowance" in {
        val cacheMap = CacheMap("a", Map(Constants.anyDownsizingAllowanceId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyDownsizingAllowance shouldBe Some(true)
      }

      "return the correct answer for Any Exemption" in {
        val cacheMap = CacheMap("a", Map(Constants.anyExemptionId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyExemption shouldBe Some(true)
      }

      "return the correct answer for Any Property Closely Inherited" in {
        val cacheMap = CacheMap("a", Map(Constants.anyPropertyCloselyInheritedId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyPropertyCloselyInherited shouldBe Some(true)
      }

      "return the correct answer for Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap("a", Map(Constants.assetsPassingToDirectDescendantsId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.assetsPassingToDirectDescendants shouldBe Some(1)
      }

      "return the correct answer for Brought Forward Allowance" in {
        val cacheMap = CacheMap("a", Map(Constants.broughtForwardAllowanceId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.broughtForwardAllowance shouldBe Some(1)
      }

      "return the correct answer for Brought Forward Allowance On Disposal" in {
        val cacheMap = CacheMap("a", Map(Constants.broughtForwardAllowanceOnDisposalId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.broughtForwardAllowanceOnDisposal shouldBe Some(1)
      }

      "return the correct answer for Chargeable Transfer Amount" in {
        val cacheMap = CacheMap("a", Map(Constants.chargeableTransferAmountId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.chargeableTransferAmount shouldBe Some(1)
      }

      "return the correct answer for Date Of Death" in {
        val cacheMap = CacheMap("a", Map(Constants.dateOfDeathId -> JsString("2018-01-01")))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.dateOfDeath shouldBe Some(new LocalDate(2018, 1, 1))
      }

      "return the correct answer for Date Of Disposal" in {
        val cacheMap = CacheMap("a", Map(Constants.dateOfDisposalId -> JsString("2018-01-01")))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.dateOfDisposal shouldBe Some(new LocalDate(2018, 1, 1))
      }

      "return the correct answer for Estate Has Property" in {
        val cacheMap = CacheMap("a", Map(Constants.estateHasPropertyId -> JsBoolean(true)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.estateHasProperty shouldBe Some(true)
      }

      "return the correct answer for Gross Estate Value" in {
        val cacheMap = CacheMap("a", Map(Constants.grossEstateValueId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.grossEstateValue shouldBe Some(1)
      }

      "return the correct answer for Percentage Closely Inherited" in {
        val cacheMap = CacheMap("a", Map(Constants.percentageCloselyInheritedId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.percentageCloselyInherited shouldBe Some(1)
      }

      "return the correct answer for Property Value" in {
        val cacheMap = CacheMap("a", Map(Constants.propertyValueId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyValue shouldBe Some(1)
      }

      "return the correct answer for Property Value After Exemption" in {
        val cacheMap = CacheMap("a", Map(
          Constants.propertyValueAfterExemptionId -> JsObject(Map(
            "value" -> JsNumber(1),
            "valueCloselyInherited" -> JsNumber(2)
          ))
        ))

        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyValueAfterExemption shouldBe Some(PropertyValueAfterExemption(1, 2))
      }

      "return the correct answer for Purpose Of Use" in {
        val cacheMap = CacheMap("a", Map(Constants.purposeOfUseId -> JsString(Constants.planning)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.purposeOfUse shouldBe Some(Constants.planning)
      }

      "return the correct answer for Value Of Disposed Property" in {
        val cacheMap = CacheMap("a", Map(Constants.valueOfDisposedPropertyId -> JsNumber(1)))
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueOfDisposedProperty shouldBe Some(1)
      }
    }

    "values don't exist in the cache map" must {
      "return None for Any Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyAssetsPassingToDirectDescendants shouldBe None
      }

      "return None for Any Brought Forward Allowance" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyBroughtForwardAllowance shouldBe None
      }

      "return None for Any Brought Forward Allowance On Disposal" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyBroughtForwardAllowanceOnDisposal shouldBe None
      }

      "return None for Any Downsizing Allowance" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyDownsizingAllowance shouldBe None
      }

      "return None for Any Exemption" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyExemption shouldBe None
      }

      "return None for Any Property Closely Inherited" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.anyPropertyCloselyInherited shouldBe None
      }

      "return None for Assets Passing To Direct Descendant" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.assetsPassingToDirectDescendants shouldBe None
      }

      "return None for Brought Forward Allowance" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.broughtForwardAllowance shouldBe None
      }

      "return None for Brought Forward Allowance On Disposal" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.broughtForwardAllowanceOnDisposal shouldBe None
      }

      "return None for Chargeable Transfer Amount" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.chargeableTransferAmount shouldBe None
      }

      "return None for Date Of Death" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.dateOfDeath shouldBe None
      }

      "return None for Date Of Disposal" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.dateOfDisposal shouldBe None
      }

      "return None for Estate Has Property" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.estateHasProperty shouldBe None
      }

      "return None for Gross Estate Value" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.grossEstateValue shouldBe None
      }

      "return None for Percentage Closely Inherited" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.percentageCloselyInherited shouldBe None
      }

      "return None for Property Value" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyValue shouldBe None
      }

      "return None for Property Value After Exemption" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.propertyValueAfterExemption shouldBe None
      }

      "return None for Purpose Of Use" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.purposeOfUse shouldBe None
      }

      "return None for Value Of Disposed Property" in {
        val cacheMap = CacheMap("a", Map())
        val userAnswers = new UserAnswers(cacheMap)
        userAnswers.valueOfDisposedProperty shouldBe None
      }
    }
  }
}
