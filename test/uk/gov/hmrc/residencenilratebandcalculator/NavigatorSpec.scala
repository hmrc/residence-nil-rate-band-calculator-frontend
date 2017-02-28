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
import org.mockito.Matchers._
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

    "return a function that goes to the Any Estate Passed To Descendants controller when given DateOfDeath, and the date of death is after 5 April 2017" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDeath) thenReturn Some(new LocalDate(2017, 4, 6))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.AnyEstatePassedToDescendantsController.onPageLoad()
    }

    "return a function that goes to the Gross Estate Value controller when given Any Estate Passed To Descendants with a value of true" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyEstatePassedToDescendants) thenReturn Some(true)
      navigator.nextPage(Constants.anyEstatePassedToDescendantsId)(mockCacheMap) shouldBe routes.GrossEstateValueController.onPageLoad()
    }

    "return a function that goes to the Transition Out Controller controller when given Any Estate Passed To Descendants with a value of false" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyEstatePassedToDescendants) thenReturn Some(false)
      navigator.nextPage(Constants.anyEstatePassedToDescendantsId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
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

    "when the ChargeableTransferAmount is used as the class id, the navigator must return a function that when executed against any" +
      "parameter goes to EstateHasProperty controller" in {
      navigator.nextPage(Constants.chargeableTransferAmountId)(mock[UserAnswers]) shouldBe routes.EstateHasPropertyController.onPageLoad()
    }

    "return a call to the ChargeableTransferAmountController onPageLoad method when given Gross Estate Value" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.grossEstateValue) thenReturn Some(2000000)
      navigator.nextPage(Constants.grossEstateValueId)(mockCacheMap) shouldBe routes.ChargeableTransferAmountController.onPageLoad()
    }

    "when the PropertyValue is used at the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the Any Property Closely Inherited controller" in {
      navigator.nextPage(Constants.propertyValueId)(mock[UserAnswers]) shouldBe routes.AnyPropertyCloselyInheritedController.onPageLoad()
    }

    "return a function that goes to the Percentage Closely Inherited controller when given Any Property Closely Inherited with a value of true" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyPropertyCloselyInherited) thenReturn Some(true)
      navigator.nextPage(Constants.anyPropertyCloselyInheritedId)(mockCacheMap) shouldBe routes.PercentageCloselyInheritedController.onPageLoad()
    }

    "return a function that goes to the Any Brought Forward Allowance controller when given Any Property Closely Inherited with a value of false" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyPropertyCloselyInherited) thenReturn Some(false)
      navigator.nextPage(Constants.anyPropertyCloselyInheritedId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a function that goes to the Any Exemption controller when given PercentageCloselyInherited" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.percentageCloselyInherited) thenReturn Some(1)
      navigator.nextPage(Constants.percentageCloselyInheritedId)(mockCacheMap) shouldBe routes.AnyExemptionController.onPageLoad()
    }

    "return a call to the PropertyValueController onPageLoad method when there is a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.estateHasProperty) thenReturn Some(true)
      navigator.nextPage(Constants.estateHasPropertyId)(mockCacheMap) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the AnyBroughtForwardAllowance onPageLoad method when there is not a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.estateHasProperty) thenReturn Some(false)
      navigator.nextPage(Constants.estateHasPropertyId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the HomeController onPageLoad method when there is no indication that there is a property in the estate" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.estateHasProperty) thenReturn None
      navigator.nextPage(Constants.estateHasPropertyId)(mockCacheMap) shouldBe routes.HomeController.onPageLoad()
    }

    "return a call to the BroughtForwardAllowanceController onPageLoad method when there is some brought forward allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(true)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceId)(mockCacheMap) shouldBe routes.BroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the AnyDownsizingAllowanceController onPageLoad method when there is no brought forward allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowance) thenReturn Some(false)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceId)(mockCacheMap) shouldBe routes.AnyDownsizingAllowanceController.onPageLoad()
    }

    "return a call to the AnyDownsizingAllowanceController onPageLoad method from the BroughtForwardController" in {
      navigator.nextPage(Constants.broughtForwardAllowanceId)(mock[UserAnswers]) shouldBe routes.AnyDownsizingAllowanceController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance onPageLoad method when no exemptions apply to the property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyExemption) thenReturn Some(false)
      navigator.nextPage(Constants.anyExemptionId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Property Value After Exemption onPageLoad method when exemptions apply to the property" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyExemption) thenReturn Some(true)
      navigator.nextPage(Constants.anyExemptionId)(mockCacheMap) shouldBe routes.PropertyValueAfterExemptionController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance onPageLoad method from the Property Value After Exemption controller" in {
      navigator.nextPage(Constants.propertyValueAfterExemptionId)(mock[UserAnswers]) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    val userAnswers = new UserAnswers(CacheMap("", Map()))

    "return a call to the Date Of Death when back linking from the Any Estate Passed To Descendant page" in {
      navigator.lastPage(Constants.anyEstatePassedToDescendantsId)(userAnswers) shouldBe routes.DateOfDeathController.onPageLoad()
    }

    "return a call to the Any Estate Passed To Descendants when back linking from the Gross Estate Value page" in {
      navigator.lastPage(Constants.grossEstateValueId)(userAnswers) shouldBe routes.AnyEstatePassedToDescendantsController.onPageLoad()
    }

    "return a call to the Gross Estate Value when back linking from the Chargeable Transfer Amount page" in {
      navigator.lastPage(Constants.chargeableTransferAmountId)(userAnswers) shouldBe routes.GrossEstateValueController.onPageLoad()
    }

    "return a call to the Chargeable Transfer Amount when back linking from the Estate Has Property page" in {
      navigator.lastPage(Constants.estateHasPropertyId)(userAnswers) shouldBe routes.ChargeableTransferAmountController.onPageLoad()
    }

    "return a call to the Estate Has Property when back linking from the Property Value page" in {
      navigator.lastPage(Constants.propertyValueId)(userAnswers) shouldBe routes.EstateHasPropertyController.onPageLoad()
    }

    "return a call to the Property Value when back linking from the Any Property Closely Inherited page" in {
      navigator.lastPage(Constants.anyPropertyCloselyInheritedId)(userAnswers) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the Any Property Closely Inherited when back linking from the Percentage Closely Inherited page" in {
      navigator.lastPage(Constants.percentageCloselyInheritedId)(userAnswers) shouldBe routes.AnyPropertyCloselyInheritedController.onPageLoad()
    }

    "return a call to the Percentage Closely Inherited when back linking from the Any Exemption page" in {
      navigator.lastPage(Constants.anyExemptionId)(userAnswers) shouldBe routes.PercentageCloselyInheritedController.onPageLoad()
    }

    "return a call to the Any Exemption when back linking from the Property Value After Exemption page" in {
      navigator.lastPage(Constants.propertyValueAfterExemptionId)(userAnswers) shouldBe routes.AnyExemptionController.onPageLoad()
    }

    "return a call to the Estate Has Property when back linking from the Any Brought Forward Allowance page" in {
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.EstateHasPropertyController.onPageLoad()
    }

    "return a call to the Property Value After Exemption when back linking from the Any Brought Forward Allowance page" +
      "when the user has positively answered Any Exemption" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.anyExemption) thenReturn Some(true)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.PropertyValueAfterExemptionController.onPageLoad()
    }

    "return a call to the Any Exemption when back linking from the Any Brought Forward Allowance page" +
      "when the user has positively answered Property Closely Inherited" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.anyPropertyCloselyInherited) thenReturn Some(true)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.AnyExemptionController.onPageLoad()
    }

    "return a call to the Property Value when back linking from the Any Brought Forward Allowance page" +
      "when the user has positively answered Estate Has Property" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.estateHasProperty) thenReturn Some(true)
      navigator.lastPage(Constants.anyBroughtForwardAllowanceId)(userAnswers) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance After Exemption when back linking from the Brought Forward Allowance page" in {
      navigator.lastPage(Constants.broughtForwardAllowanceId)(userAnswers) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance when back linking from the Any Downsizing Allowance page" in {
      navigator.lastPage(Constants.anyDownsizingAllowanceId)(userAnswers) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Brought Forward Allowance when back linking from the Any Downsizing Allowance page when the" +
      "user has positively answered Any Brought Forward Allowance" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.anyBroughtForwardAllowance) thenReturn Some(true)
      navigator.lastPage(Constants.anyDownsizingAllowanceId)(userAnswers) shouldBe routes.BroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Any Downsizing Allowance After Exemption when back linking from the Date of Disposal page" in {
      navigator.lastPage(Constants.dateOfDisposalId)(userAnswers) shouldBe routes.AnyDownsizingAllowanceController.onPageLoad()
    }

    "return a call to the Date of Disposal After Exemption when back linking from the Value Of Disposed Property page" in {
      navigator.lastPage(Constants.valueOfDisposedPropertyId)(userAnswers) shouldBe routes.DateOfDisposalController.onPageLoad()
    }

    "return a call to the Value Of Disposed Property when back linking from the Any Assets Passing To Direct Decendants page" in {
      navigator.lastPage(Constants.anyAssetsPassingToDirectDescendantsId)(userAnswers) shouldBe routes.ValueOfDisposedPropertyController.onPageLoad()
    }

    "return a call to the Any Assets Passing To Direct Descendants when back linking from the Assets Passing To Direct Decendants page" in {
      navigator.lastPage(Constants.assetsPassingToDirectDescendantsId)(userAnswers) shouldBe routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Assets Passing To Direct Descendants when back linking from the Any Brought Forward Allowance On Disposal page" in {
      navigator.lastPage(Constants.anyBroughtForwardAllowanceOnDisposalId)(userAnswers) shouldBe routes.AssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance On Disposal when back linking from the Brought Forward Allowance On Disposal page" in {
      navigator.lastPage(Constants.broughtForwardAllowanceOnDisposalId)(userAnswers) shouldBe routes.AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()
    }

    "return a call to the check answers Controller onPageLoad method when there is no downsizing allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyDownsizingAllowance) thenReturn Some(false)
      navigator.nextPage(Constants.anyDownsizingAllowanceId)(mockCacheMap) shouldBe routes.CheckAnswersController.onPageLoad()
    }

    "return a call to the date of property disposal controller onPageLoad method when there is some downsizing allowance" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyDownsizingAllowance) thenReturn Some(true)
      navigator.nextPage(Constants.anyDownsizingAllowanceId)(mockCacheMap) shouldBe routes.DateOfDisposalController.onPageLoad()
    }

    "return a call to the TransitionOutController Controller onPageLoad method when a date before 8th July 2015 is" +
      "supplied as the Date of Disposal " in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDisposal) thenReturn Some(new LocalDate(2015, 7, 7))
      navigator.nextPage(Constants.dateOfDisposalId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a call to the Value of Disposed Property Controller onPageLoad method when a date on or after 8th July 2015 is supplied as the Date of Disposal" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.dateOfDisposal) thenReturn Some(new LocalDate(2015, 7, 8))
      navigator.nextPage(Constants.dateOfDisposalId)(mockCacheMap) shouldBe routes.ValueOfDisposedPropertyController.onPageLoad()
    }

    "return a call to the any assets passing to direct descendants controller onPageLoad method from the ValueOfDisposedProperty controller" in {
      navigator.nextPage(Constants.valueOfDisposedPropertyId)(mock[UserAnswers]) shouldBe routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the assets passing to direct descendant onPageLoad method when there are some assest passing to the direct descendant" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyAssetsPassingToDirectDescendants) thenReturn Some(true)
      navigator.nextPage(Constants.anyAssetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.AssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the check answers onPageLoad method when there are no assets passing to the direct descendant" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyAssetsPassingToDirectDescendants) thenReturn Some(false)
      navigator.nextPage(Constants.anyAssetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.CheckAnswersController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance On Disposal controller onPageLoad method from the AssetsPassingToDirectDescendants controller" in {
      navigator.nextPage(Constants.assetsPassingToDirectDescendantsId)(mock[UserAnswers]) shouldBe routes.AnyBroughtForwardAllowanceOnDisposalController.onPageLoad()
    }

    "return a call to the check answers onPageLoad method when there is no brought forward allowance on disposal" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowanceOnDisposal) thenReturn Some(false)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceOnDisposalId)(mockCacheMap) shouldBe routes.CheckAnswersController.onPageLoad()
    }

    "return a call to the Brought Forward Allowance on Disposal onPageLoad method when there is some brought forward allowance on disposal" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.anyBroughtForwardAllowanceOnDisposal) thenReturn Some(true)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceOnDisposalId)(mockCacheMap) shouldBe routes.BroughtForwardAllowanceOnDisposalController.onPageLoad()
    }

    "return a call to the check answers controller onPageLoad method from the BroughtForwardAllowanceOnDisposal controller" in {
      navigator.nextPage(Constants.broughtForwardAllowanceOnDisposalId)(mock[UserAnswers]) shouldBe routes.CheckAnswersController.onPageLoad()
    }

    "return a call to the results controller onPageLoad method from the CheckAnswers controller" in {
      navigator.nextPage(Constants.checkAnswersId)(mock[UserAnswers]) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the planning controller from the Purpose of Use controller when the value is 'planning'" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.purposeOfUse) thenReturn Some(Constants.planning)
      navigator.nextPage(Constants.purposeOfUseId)(mockCacheMap) shouldBe routes.PlanningController.onPageLoad()
    }

    "return a call to the date of death controller from the Purpose of Use controller when the value is 'dealing_with_estate'" in {
      val mockCacheMap = mock[UserAnswers]
      when(mockCacheMap.purposeOfUse) thenReturn Some(Constants.dealingWithEstate)
      navigator.nextPage(Constants.purposeOfUseId)(mockCacheMap) shouldBe routes.DateOfDeathController.onPageLoad()
    }
  }
}
