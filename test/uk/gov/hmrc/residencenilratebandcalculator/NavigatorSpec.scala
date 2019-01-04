/*
 * Copyright 2019 HM Revenue & Customs
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

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers

class NavigatorSpec extends BaseSpec with MockitoSugar with Matchers {
  val navigator = new Navigator

  "Navigator" must {
    "When the current call is not found, return a function, that when executed against any parameter throws an exception to load a technical error page" in {
      the[Exception] thrownBy navigator.nextPage("")(mock[UserAnswers]) should have message "Page not found for controller id: "
    }

    "return a function that goes to the Part of Estate Passing to Direct Descendants controller when given DateOfDeath, and the date of death is after 5 April 2017" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn Some(new LocalDate(2017, 4, 6))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.PartOfEstatePassingToDirectDescendantsController.onPageLoad()
    }

    "return a function that goes to the Value Of Estate controller when given Part Of Estate Passing To Direct Descendants with a value of true" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.partOfEstatePassingToDirectDescendants) thenReturn Some(true)
      navigator.nextPage(Constants.partOfEstatePassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.ValueOfEstateController.onPageLoad()
    }

    "return a function that goes to the No Threshold Increase Controller controller when given Part of Estate Passing to Direct Descendants with a value of false" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.partOfEstatePassingToDirectDescendants) thenReturn Some(false)
      navigator.nextPage(Constants.partOfEstatePassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.NoThresholdIncreaseController.onPageLoad()
    }

    "return a function that goes to the No Threshold Increase controller when given DateOfDeath, and the date of death is 5 April 2017" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn Some(new LocalDate(2017, 4, 5))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.NoThresholdIncreaseController.onPageLoad()
    }

    "return a function that goes to the No Threshold Increase controller when given DateOfDeath, and the date of death is before 5 April 2017" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn Some(new LocalDate(2017, 4, 4))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.NoThresholdIncreaseController.onPageLoad()
    }

    "return a function that goes to the Calculate Threshold Increase controller when given DateOfDeath, and the date of death does not exist in keystore" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn None
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.CalculateThresholdIncreaseController.onPageLoad()
    }

    "when the ChargeableEstateValue is used as the class id, the navigator must return a function that when executed against any" +
      "parameter goes to PropertyInEstate controller" in {
      navigator.nextPage(Constants.chargeableEstateValueId)(mock[UserAnswers]) shouldBe routes.PropertyInEstateController.onPageLoad()
    }

    "return a call to the ChargeableEstateValueController onPageLoad method when given Value Of Estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.valueOfEstate) thenReturn Some(2000000)
      navigator.nextPage(Constants.valueOfEstateId)(mockCacheMap) shouldBe routes.ChargeableEstateValueController.onPageLoad()
    }

    "when the PropertyValue is used at the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the Property Passing To Direct Descendants controller" in {
      navigator.nextPage(Constants.propertyValueId)(mock[UserAnswers]) shouldBe routes.PropertyPassingToDirectDescendantsController.onPageLoad()
    }

    "return a function that goes to the Exemptions And Relief Claimedcontroller when given Property Passing To Direct Descendants with a value of all" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyPassingToDirectDescendants) thenReturn Some(Constants.all)
      navigator.nextPage(Constants.propertyPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.ExemptionsAndReliefClaimedController.onPageLoad()
    }

    "return a function that goes to the Percentage Passed To Direct Descendants controller when given Property Passing To Direct Descendants with a value of some" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyPassingToDirectDescendants) thenReturn Some(Constants.some)
      navigator.nextPage(Constants.propertyPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.PercentagePassedToDirectDescendantsController.onPageLoad()
    }

    "return a function that goes to the No Additional Threshold Available controller when given Property Passing To Direct Descendants with a value of none" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      navigator.nextPage(Constants.propertyPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.NoAdditionalThresholdAvailableController.onPageLoad()
    }

    "return a call to the TransferAnyUnusedThreshold onPageLoad method when there is no property in the estate and we're on the No Additional Threshold Available page" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn Some(false)
      navigator.nextPage(Constants.noAdditionalThresholdAvailableId)(mockCacheMap) shouldBe routes.TransferAnyUnusedThresholdController.onPageLoad()
    }

    "return a call to the TransferAnyUnusedThreshold onPageLoad method when there is no Property Passing To Direct Descendants" +
      "and we're on the No Additional Threshold Available page" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      navigator.nextPage(Constants.noAdditionalThresholdAvailableId)(mockCacheMap) shouldBe routes.TransferAnyUnusedThresholdController.onPageLoad()
    }

    "return a call to the ThresholdCalculationResult onPageLoad method when we're on the No Downsizing Threshold Increase page" in {
      navigator.nextPage(Constants.noDownsizingThresholdIncrease)(mock[UserAnswers]) shouldBe routes.ThresholdCalculationResultController.onPageLoad()
    }

    "return a function that goes to the Exemptions And Relief Claimed controller when given PercentagePassedToDirectDescendants" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.percentagePassedToDirectDescendants) thenReturn Some(BigDecimal(1))
      navigator.nextPage(Constants.percentagePassedToDirectDescendantsId)(mockCacheMap) shouldBe routes.ExemptionsAndReliefClaimedController.onPageLoad()
    }

    "return a call to the PropertyValueController onPageLoad method when there is a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn Some(true)
      navigator.nextPage(Constants.propertyInEstateId)(mockCacheMap) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the TransferAnyUnusedThreshold onPageLoad method when there is not a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn Some(false)
      navigator.nextPage(Constants.propertyInEstateId)(mockCacheMap) shouldBe routes.NoAdditionalThresholdAvailableController.onPageLoad()
    }

    "return a call to the Calculate Threshold Increase onPageLoad method when there is no indication that there is a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn None
      navigator.nextPage(Constants.propertyInEstateId)(mockCacheMap) shouldBe routes.CalculateThresholdIncreaseController.onPageLoad()
    }

    "return a call to the ValueBeingTransferredController onPageLoad method when there is some value being transferred" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAnyUnusedThreshold) thenReturn Some(true)
      navigator.nextPage(Constants.transferAnyUnusedThresholdId)(mockCacheMap) shouldBe routes.ValueBeingTransferredController.onPageLoad()
    }

    "return a call to the ClaimDownsizingThresholdController onPageLoad method when there is no value being transferred" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAnyUnusedThreshold) thenReturn Some(false)
      navigator.nextPage(Constants.transferAnyUnusedThresholdId)(mockCacheMap) shouldBe routes.ClaimDownsizingThresholdController.onPageLoad()
    }

    "return a call to the ClaimDownsizingThresholdController onPageLoad method from the ValueBeingTransferred" in {
      navigator.nextPage(Constants.valueBeingTransferredId)(mock[UserAnswers]) shouldBe routes.ClaimDownsizingThresholdController.onPageLoad()
    }

    "return a call to the Transfer Any Unused Allowance onPageLoad method when no exemptions apply to the property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.exemptionsAndReliefClaimed) thenReturn Some(false)
      navigator.nextPage(Constants.exemptionsAndReliefClaimedId)(mockCacheMap) shouldBe routes.TransferAnyUnusedThresholdController.onPageLoad()
    }

    "return a call to the Grossing Up On Estate Property onPageLoad method when exemptions apply to the property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.exemptionsAndReliefClaimed) thenReturn Some(true)
      navigator.nextPage(Constants.exemptionsAndReliefClaimedId)(mockCacheMap) shouldBe routes.GrossingUpOnEstatePropertyController.onPageLoad()
    }

    "return a call to Chargeable Property Value onPageLoad method when grossing up does not apply" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.grossingUpOnEstateProperty) thenReturn Some(false)
      navigator.nextPage(Constants.grossingUpOnEstatePropertyId)(mockCacheMap) shouldBe routes.ChargeablePropertyValueController.onPageLoad()
    }

    "return a call to Transition Out onPageLoad method when grossing up does apply to the residence" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.grossingUpOnEstateProperty) thenReturn Some(true)
      navigator.nextPage(Constants.grossingUpOnEstatePropertyId)(mockCacheMap) shouldBe routes.UnableToCalculateThresholdIncreaseController.onPageLoad()
    }

    "return a call to the Transfer Any Unused Allowance onPageLoad method from the " +
      "Chargeable Inherited Property Value controller" in {
      navigator.nextPage(Constants.chargeableInheritedPropertyValueId)(mock[UserAnswers]) shouldBe routes.TransferAnyUnusedThresholdController.onPageLoad()
    }

    "return a call to the ThresholdCalculationResult Controller onPageLoad method when there is no downsizing allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.claimDownsizingThreshold) thenReturn Some(false)
      navigator.nextPage(Constants.claimDownsizingThresholdId)(mockCacheMap) shouldBe routes.ThresholdCalculationResultController.onPageLoad()
    }

    "return a call to the date property was changed controller onPageLoad method when there is some downsizing allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.claimDownsizingThreshold) thenReturn Some(true)
      navigator.nextPage(Constants.claimDownsizingThresholdId)(mockCacheMap) shouldBe routes.DatePropertyWasChangedController.onPageLoad()
    }

    "return a call to the No Downsizing Threshold Increase Controller onPageLoad method when a date before 8th July 2015 is" +
      "supplied as the Date Property Was Changed " in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(new LocalDate(2015, 7, 7))
      navigator.nextPage(Constants.datePropertyWasChangedId)(mockCacheMap) shouldBe routes.NoDownsizingThresholdIncreaseController.onPageLoad()
    }

    "return a call to the Value Of Changed Property Controller onPageLoad method when a date on or after 8th July 2015 is supplied as the Date Property Was Changed" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(new LocalDate(2015, 7, 8))
      navigator.nextPage(Constants.datePropertyWasChangedId)(mockCacheMap) shouldBe routes.ValueOfChangedPropertyController.onPageLoad()
    }

    "return a call to the assets passing to direct descendants controller onPageLoad method from the ValueOfChangedProperty controller" in {
      navigator.nextPage(Constants.valueOfChangedPropertyId)(mock[UserAnswers]) shouldBe routes.AssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the assets passing to direct descendant onPageLoad method when grossing up does not apply to the other property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.grossingUpOnEstateAssets) thenReturn Some(false)
      navigator.nextPage(Constants.grossingUpOnEstateAssetsId)(mockCacheMap) shouldBe routes.ValueOfAssetsPassingController.onPageLoad()
    }

    "return a call to the transition out onPageLoad method when grossing up does apply to the other property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.grossingUpOnEstateAssets) thenReturn Some(true)
      navigator.nextPage(Constants.grossingUpOnEstateAssetsId)(mockCacheMap) shouldBe routes.UnableToCalculateThresholdIncreaseController.onPageLoad()
    }

    "return a call to the Grossing Up On Estate Assets onPageLoad method when there are some assets passing to the direct descendant" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.assetsPassingToDirectDescendants) thenReturn Some(true)
      navigator.nextPage(Constants.assetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.GrossingUpOnEstateAssetsController.onPageLoad()
    }

    "return a call to the No Downsizing Threshold Increase onPageLoad method when there are no assets passing to the direct descendant" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.assetsPassingToDirectDescendants) thenReturn Some(false)
      navigator.nextPage(Constants.assetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.NoDownsizingThresholdIncreaseController.onPageLoad()
    }

    "return a call to the Transfer Available When Property Changed controller onPageLoad method from the AssetsPassingToDirectDescendants controller " +
      "when there is value being transferred and the property was changed on (or after) the eligibility date" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAnyUnusedThreshold) thenReturn Some(true)
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(Constants.eligibilityDate)
      navigator.nextPage(Constants.valueOfAssetsPassingId)(mockCacheMap) shouldBe routes.TransferAvailableWhenPropertyChangedController.onPageLoad()
    }

    "return a call to the Threshold Calculation Result controller onPageLoad method from AssetsPassingToDirectDescendants when there is no value being transferred, " +
      "even though the property was changed on the eligibility date" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAnyUnusedThreshold) thenReturn Some(false)
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(Constants.eligibilityDate)
      navigator.nextPage(Constants.valueOfAssetsPassingId)(mockCacheMap) shouldBe routes.ThresholdCalculationResultController.onPageLoad()
    }

    "return a call to the Threshold Calculation Result controller onPageLoad method from AssetsPassingToDirectDescendants when there is value being transferred " +
      "but the property was changed before the eligibility date" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAnyUnusedThreshold) thenReturn Some(true)
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(Constants.eligibilityDate.minusDays(1))
      navigator.nextPage(Constants.valueOfAssetsPassingId)(mockCacheMap) shouldBe routes.ThresholdCalculationResultController.onPageLoad()
    }

    "return a call to the Threshold Calculation Result onPageLoad method when there is no Value Available When Property Changed" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAvailableWhenPropertyChanged) thenReturn Some(false)
      navigator.nextPage(Constants.transferAvailableWhenPropertyChangedId)(mockCacheMap) shouldBe routes.ThresholdCalculationResultController.onPageLoad()
    }

    "return a call to the Value Available When Property Changed onPageLoad method when there is some Value Available When Property Changed" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.transferAvailableWhenPropertyChanged) thenReturn Some(true)
      navigator.nextPage(Constants.transferAvailableWhenPropertyChangedId)(mockCacheMap) shouldBe routes.ValueAvailableWhenPropertyChangedController.onPageLoad()
    }

    "return a call to the Threshold Calculation Result controller onPageLoad method from the ValueAvailableWhenPropertyChanged controller" in {
      navigator.nextPage(Constants.valueAvailableWhenPropertyChangedId)(mock[UserAnswers]) shouldBe routes.ThresholdCalculationResultController.onPageLoad()
    }
  }
}
