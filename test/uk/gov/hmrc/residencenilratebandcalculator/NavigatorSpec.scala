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

package uk.gov.hmrc.residencenilratebandcalculator

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers

class NavigatorSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication {
  val navigator = new Navigator

  "Navigator" must {
    "When the current call is not found, return a function, that when executed against any parameter routes to the page not found controller" in {
      navigator.nextPage("")(mock[UserAnswers]) shouldBe routes.PageNotFoundController.onPageLoad()
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

    "return a function that goes to the Transition Out Controller controller when given Part of Estate Passing to Direct Descendants with a value of false" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.partOfEstatePassingToDirectDescendants) thenReturn Some(false)
      navigator.nextPage(Constants.partOfEstatePassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a function that goes to the Transition controller when given DateOfDeath, and the date of death is 5 April 2017" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn Some(new LocalDate(2017, 4, 5))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a function that goes to the Transition controller when given DateOfDeath, and the date of death is before 5 April 2017" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn Some(new LocalDate(2017, 4, 4))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a function that goes to the Home controller when given DateOfDeath, and the date of death does not exist in keystore" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn None
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.HomeController.onPageLoad()
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

    "return a function that goes to the Cannot Claim RNRB controller when given Property Passing To Direct Descendants with a value of none" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      navigator.nextPage(Constants.propertyPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.CannotClaimRNRBController.onPageLoad()
    }

    "return a call to the AnyBroughtForwardAllowance onPageLoad method when there is no property in the estate and we're on the Cannot Claim RNRB page" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn Some(false)
      navigator.nextPage(Constants.cannotClaimRNRB)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the AnyBroughtForwardAllowance onPageLoad method when there is no Property Passing To Direct Descendants" +
      "and we're on the Cannot Claim RNRB page" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      navigator.nextPage(Constants.cannotClaimRNRB)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Results onPageLoad method when we're on the Cannot Claim Downsizing page" in {
      navigator.nextPage(Constants.cannotClaimDownsizingId)(mock[UserAnswers]) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a function that goes to the Exemptions And Relief Claimed controller when given PercentagePassedToDirectDescendants" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.percentagePassedToDirectDescendants) thenReturn Some(1)
      navigator.nextPage(Constants.percentagePassedToDirectDescendantsId)(mockCacheMap) shouldBe routes.ExemptionsAndReliefClaimedController.onPageLoad()
    }

    "return a call to the PropertyValueController onPageLoad method when there is a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn Some(true)
      navigator.nextPage(Constants.propertyInEstateId)(mockCacheMap) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the AnyBroughtForwardAllowance onPageLoad method when there is not a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn Some(false)
      navigator.nextPage(Constants.propertyInEstateId)(mockCacheMap) shouldBe routes.CannotClaimRNRBController.onPageLoad()
    }

    "return a call to the HomeController onPageLoad method when there is no indication that there is a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.propertyInEstate) thenReturn None
      navigator.nextPage(Constants.propertyInEstateId)(mockCacheMap) shouldBe routes.HomeController.onPageLoad()
    }

    "return a call to the BroughtForwardAllowanceController onPageLoad method when there is some brought forward allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(true)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceId)(mockCacheMap) shouldBe routes.BroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the ClaimDownsizingThresholdController onPageLoad method when there is no brought forward allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(false)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceId)(mockCacheMap) shouldBe routes.ClaimDownsizingThresholdController.onPageLoad()
    }

    "return a call to the ClaimDownsizingThresholdController onPageLoad method from the BroughtForwardController" in {
      navigator.nextPage(Constants.broughtForwardAllowanceId)(mock[UserAnswers]) shouldBe routes.ClaimDownsizingThresholdController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance onPageLoad method when no exemptions apply to the property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.exemptionsAndReliefClaimed) thenReturn Some(false)
      navigator.nextPage(Constants.exemptionsAndReliefClaimedId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
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
      navigator.nextPage(Constants.grossingUpOnEstatePropertyId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance onPageLoad method from the " +
      "Chargeable Inherited Property Value controller" in {
      navigator.nextPage(Constants.chargeableInheritedPropertyValueId)(mock[UserAnswers]) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    val userAnswers = new UserAnswers(CacheMap("", Map()))

    "return a call to the Date Of Death when back linking from the Part of Estate Passing to Direct Descendants page" in {
      navigator.lastPage(Constants.partOfEstatePassingToDirectDescendantsId)(userAnswers) shouldBe routes.DateOfDeathController.onPageLoad()
    }

    "return a call to the Part Of Estate Passing To Direct Descendants when back linking from the Value Of Estate page" in {
      navigator.lastPage(Constants.valueOfEstateId)(userAnswers) shouldBe routes.PartOfEstatePassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Value Of Estate when back linking from the Chargeable Estate Value page" in {
      navigator.lastPage(Constants.chargeableEstateValueId)(userAnswers) shouldBe routes.ValueOfEstateController.onPageLoad()
    }

    "return a call to the Chargeable Estate Value when back linking from the Property In Estate page" in {
      navigator.lastPage(Constants.propertyInEstateId)(userAnswers) shouldBe routes.ChargeableEstateValueController.onPageLoad()
    }

    "return a call to the Property In Estate when back linking from the Property Value page" in {
      navigator.lastPage(Constants.propertyValueId)(userAnswers) shouldBe routes.PropertyInEstateController.onPageLoad()
    }

    "return a call to the Property Value when back linking from the Property Passing To Direct Descendants page" in {
      navigator.lastPage(Constants.propertyPassingToDirectDescendantsId)(userAnswers) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the Property Passing To Direct Descendants when back linking from the Percentage Passed To Direct Descendants page" in {
      navigator.lastPage(Constants.percentagePassedToDirectDescendantsId)(userAnswers) shouldBe routes.PropertyPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to Property Passing To Direct Descendants when back linking from the Exemptions And Relief Claimed page " +
      "when the user has given an answer of 'all' for 'Property Passing To Direct Descendants'" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.all)
      navigator.lastPage(Constants.exemptionsAndReliefClaimedId)(userAnswers) shouldBe routes.PropertyPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Percentage Passed To Direct Descendants when back linking from the Exemptions And Relief Claimed page " +
      "when the user has given an answer of 'some' for 'Property Passing To Direct Descendants'" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.some)
      navigator.lastPage(Constants.exemptionsAndReliefClaimedId)(userAnswers) shouldBe routes.PercentagePassedToDirectDescendantsController.onPageLoad()
    }

    "return a call to Property Passing To Direct Descendants when back linking from the Exemptions And Relief Claimed page " +
      "when the user has given an answer of 'none' for 'Property Passing To Direct Descendants'" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      navigator.lastPage(Constants.exemptionsAndReliefClaimedId)(userAnswers) shouldBe routes.PropertyPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Exemptions And Relief Claimedwhen back linking from the Grossing Up On Estate Property page" in {
      navigator.lastPage(Constants.grossingUpOnEstatePropertyId)(userAnswers) shouldBe routes.ExemptionsAndReliefClaimedController.onPageLoad()
    }

    "return a call to the Does Grossing Up Apply when back linking from the Property Value After Exemption page" in {
      navigator.lastPage(Constants.chargeablePropertyValueId)(userAnswers) shouldBe routes.GrossingUpOnEstatePropertyController.onPageLoad()
    }

    "return a call to the Property In Estate when back linking from the Any Brought Forward Allowance page" in {
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.PropertyInEstateController.onPageLoad()
    }

    "return a call to the Chargeable Inherited Property Value when back linking from the Any Brought Forward Allowance page" +
      "when the user has positively answered Exemptions And Relief Claimed" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.exemptionsAndReliefClaimed) thenReturn Some(true)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.ChargeableInheritedPropertyValueController.onPageLoad()
    }

    "return a call to the Exemptions And Relief Claimed when back linking from the Any Brought Forward Allowance page" +
      "when the user has answered Property Closely Inherited as all" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.all)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.ExemptionsAndReliefClaimedController.onPageLoad()
    }

    "return a call to the Exemptions And Relief Claimed when back linking from the Any Brought Forward Allowance page" +
      "when the user has answered Property Closely Inherited as some" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.some)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.ExemptionsAndReliefClaimedController.onPageLoad()
    }

    "return a call to the Property Passing To Direct Descendants when back linking from the Any Brought Forward Allowance page" +
      "when the user has answered Property Passing To Direct Descendants as none" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.PropertyPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Property Value when back linking from the Any Brought Forward Allowance page" +
      "when the user has positively answered Property In Estate" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyInEstate) thenReturn Some(true)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance After Exemption when back linking from the Brought Forward Allowance page" in {
      navigator.lastPage(Constants.broughtForwardAllowanceId)(userAnswers) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance when back linking from the Claim Downsizing Threshold page" in {
      navigator.lastPage(Constants.claimDownsizingThresholdId)(userAnswers) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Brought Forward Allowance when back linking from the Claim Downsizing Threshold page when the" +
      "user has positively answered Any Brought Forward Allowance" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.anyBroughtForwardAllowance) thenReturn Some(true)
      navigator.lastPage(Constants.claimDownsizingThresholdId)(userAnswers) shouldBe routes.BroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Claim Downsizing Threshold After Exemption when back linking from the Date Property Was Changed page" in {
      navigator.lastPage(Constants.datePropertyWasChangedId)(userAnswers) shouldBe routes.ClaimDownsizingThresholdController.onPageLoad()
    }

    "return a call to the Date Property Was Changed After Exemption when back linking from the Value Of Disposed Property page" in {
      navigator.lastPage(Constants.valueOfDisposedPropertyId)(userAnswers) shouldBe routes.DatePropertyWasChangedController.onPageLoad()
    }

    "return a call to the Value Of Disposed Property when back linking from the Any Assets Passing To Direct Decendants page" in {
      navigator.lastPage(Constants.anyAssetsPassingToDirectDescendantsId)(userAnswers) shouldBe routes.ValueOfDisposedPropertyController.onPageLoad()
    }

    "return a call to the Any Assets Passing To Direct Descendants when back linking from the Does Grossing Up Apply To Other Property page" in {
      navigator.lastPage(Constants.doesGrossingUpApplyToOtherPropertyId)(userAnswers) shouldBe routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Does Grossing Up Apply To Other Property when back linking from the Assets Passing To Direct Descendants page" in {
      navigator.lastPage(Constants.assetsPassingToDirectDescendantsId)(userAnswers) shouldBe routes.DoesGrossingUpApplyToOtherPropertyController.onPageLoad()
    }

    "return a call to the Assets Passing To Direct Descendants when back linking from the Any Brought Forward Allowance On Disposal page" in {
      navigator.lastPage(Constants.anyBroughtForwardAllowanceOnDisposalId)(userAnswers) shouldBe routes.AssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance On Disposal when back linking from the Brought Forward Allowance On Disposal page" in {
      navigator.lastPage(Constants.broughtForwardAllowanceOnDisposalId)(userAnswers) shouldBe routes.AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()
    }

    "return a call to the Results Controller onPageLoad method when there is no downsizing allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.claimDownsizingThreshold) thenReturn Some(false)
      navigator.nextPage(Constants.claimDownsizingThresholdId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the date property was changed controller onPageLoad method when there is some downsizing allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.claimDownsizingThreshold) thenReturn Some(true)
      navigator.nextPage(Constants.claimDownsizingThresholdId)(mockCacheMap) shouldBe routes.DatePropertyWasChangedController.onPageLoad()
    }

    "return a call to the Cannot Claim Downsizing Controller onPageLoad method when a date before 8th July 2015 is" +
      "supplied as the Date Property Was Changed " in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(new LocalDate(2015, 7, 7))
      navigator.nextPage(Constants.datePropertyWasChangedId)(mockCacheMap) shouldBe routes.CannotClaimDownsizingController.onPageLoad()
    }

    "return a call to the Value of Disposed Property Controller onPageLoad method when a date on or after 8th July 2015 is supplied as the Date Property Was Changed" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(new LocalDate(2015, 7, 8))
      navigator.nextPage(Constants.datePropertyWasChangedId)(mockCacheMap) shouldBe routes.ValueOfDisposedPropertyController.onPageLoad()
    }

    "return a call to the any assets passing to direct descendants controller onPageLoad method from the ValueOfDisposedProperty controller" in {
      navigator.nextPage(Constants.valueOfDisposedPropertyId)(mock[UserAnswers]) shouldBe routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the assets passing to direct descendant onPageLoad method when grossing up does not apply to the other property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.doesGrossingUpApplyToOtherProperty) thenReturn Some(false)
      navigator.nextPage(Constants.doesGrossingUpApplyToOtherPropertyId)(mockCacheMap) shouldBe routes.AssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the transition out onPageLoad method when grossing up does apply to the other property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.doesGrossingUpApplyToOtherProperty) thenReturn Some(true)
      navigator.nextPage(Constants.doesGrossingUpApplyToOtherPropertyId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a call to the does grossing up apply to other property onPageLoad method when there are some assets passing to the direct descendant" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyAssetsPassingToDirectDescendants) thenReturn Some(true)
      navigator.nextPage(Constants.anyAssetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.DoesGrossingUpApplyToOtherPropertyController.onPageLoad()
    }

    "return a call to the Cannot Claim Downsizing onPageLoad method when there are no assets passing to the direct descendant" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyAssetsPassingToDirectDescendants) thenReturn Some(false)
      navigator.nextPage(Constants.anyAssetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.CannotClaimDownsizingController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance On Disposal controller onPageLoad method from the AssetsPassingToDirectDescendants controller " +
      "when there is brought forward allowance and the property was changed on (or after) the eligibility date" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(true)
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(Constants.eligibilityDate)
      navigator.nextPage(Constants.assetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()
    }

    "return a call to the Results controller onPageLoad method from AssetsPassingToDirectDescendants when there is no brought forward allowance, " +
      "even though the property was changed on the eligibility date" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(false)
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(Constants.eligibilityDate)
      navigator.nextPage(Constants.assetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the Results controller onPageLoad method from AssetsPassingToDirectDescendants when there is brought forward allowance " +
      "but the property was changed before the eligibility date" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(true)
      when(mockCacheMap.datePropertyWasChanged) thenReturn Some(Constants.eligibilityDate.minusDays(1))
      navigator.nextPage(Constants.assetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the Results onPageLoad method when there is no brought forward allowance on disposal" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowanceOnDisposal) thenReturn Some(false)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceOnDisposalId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the Brought Forward Allowance on Disposal onPageLoad method when there is some brought forward allowance on disposal" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowanceOnDisposal) thenReturn Some(true)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceOnDisposalId)(mockCacheMap) shouldBe routes.BroughtForwardAllowanceOnDisposalController.onPageLoad()
    }

    "return a call to the Results controller onPageLoad method from the BroughtForwardAllowanceOnDisposal controller" in {
      navigator.nextPage(Constants.broughtForwardAllowanceOnDisposalId)(mock[UserAnswers]) shouldBe routes.ResultsController.onPageLoad()
    }
  }
}
