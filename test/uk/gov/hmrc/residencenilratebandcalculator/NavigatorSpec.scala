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
import org.scalatest.{BeforeAndAfter, Matchers}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes

class NavigatorSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication {
  val navigator = new Navigator

  "Navigator" must {
    "When the current call is not found, return a function, that when executed against any parameter routes to the page not found controller" in {
      navigator.nextPage("")(mock[CacheMap]) shouldBe routes.PageNotFoundController.onPageLoad()
    }

    "return a function that goes to the Gross Estate Value controller when given DateOfDeath, and the date of death is after 5 April 2017" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[LocalDate](matches(Constants.dateOfDeathId))(any())) thenReturn Some(new LocalDate(2017, 4, 6))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.GrossEstateValueController.onPageLoad()
    }

    "return a function that goes to the Transition controller when given DateOfDeath, and the date of death is 5 April 2017" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[LocalDate](matches(Constants.dateOfDeathId))(any())) thenReturn Some(new LocalDate(2017, 4, 5))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a function that goes to the Transition controller when given DateOfDeath, and the date of death is before 5 April 2017" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[LocalDate](matches(Constants.dateOfDeathId))(any())) thenReturn Some(new LocalDate(2017, 4, 4))
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a function that goes to the Home controller when given DateOfDeath, and the date of death does not exist in keystore" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[LocalDate](matches(Constants.dateOfDeathId))(any())) thenReturn None
      navigator.nextPage(Constants.dateOfDeathId)(mockCacheMap) shouldBe routes.HomeController.onPageLoad()
    }

    "when the ChargeableTransferAmount is used as the class id, the navigator must return a function that when executed against any" +
      "parameter goes to EstateHasProperty controller" in {
      navigator.nextPage(Constants.chargeableTransferAmountId)(mock[CacheMap]) shouldBe routes.EstateHasPropertyController.onPageLoad()
    }

    "return a function that goes to the Chargeable Transfer Amount controller when given Gross Estate Value, and the value is £2 million" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Int](matches(Constants.grossEstateValueId))(any())) thenReturn Some(1999999)
      navigator.nextPage(Constants.grossEstateValueId)(mockCacheMap) shouldBe routes.ChargeableTransferAmountController.onPageLoad()
    }

    "return a function that goes to the Chargeable Transfer Amount controller when given Gross Estate Value, and the value is under £2 million" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Int](matches(Constants.grossEstateValueId))(any())) thenReturn Some(2000000)
      navigator.nextPage(Constants.grossEstateValueId)(mockCacheMap) shouldBe routes.ChargeableTransferAmountController.onPageLoad()
    }

    "return a function that goes to the Transition Out controller when given Gross Estate Value, and the value is over £2 million" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Int](matches(Constants.grossEstateValueId))(any())) thenReturn Some(2000001)
      navigator.nextPage(Constants.grossEstateValueId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "when the PropertyValue is used at the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the PercentageCloselyInherited controller" in {
      navigator.nextPage(Constants.propertyValueId)(mock[CacheMap]) shouldBe routes.PercentageCloselyInheritedController.onPageLoad()
    }

    "return a function that goes to the Any Exemption controller when given PercentageCloselyInherited greater than 0" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Int](matches(Constants.percentageCloselyInheritedId))(any())) thenReturn Some(1)
      navigator.nextPage(Constants.percentageCloselyInheritedId)(mockCacheMap) shouldBe routes.AnyExemptionController.onPageLoad()
    }

    "return a function that goes to the Any Brought Forward Allowance controller when given PercentageCloselyInherited of 0" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Int](matches(Constants.percentageCloselyInheritedId))(any())) thenReturn Some(0)
      navigator.nextPage(Constants.percentageCloselyInheritedId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the PropertyValueController onPageLoad method when there is a property in the estate" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.estateHasPropertyId))(any())) thenReturn Some(true)
      navigator.nextPage(Constants.estateHasPropertyId)(mockCacheMap) shouldBe routes.PropertyValueController.onPageLoad()
    }

    "return a call to the TransitionOutController onPageLoad method when there is not a property in the estate" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.estateHasPropertyId))(any())) thenReturn Some(false)
      navigator.nextPage(Constants.estateHasPropertyId)(mockCacheMap) shouldBe routes.TransitionOutController.onPageLoad()
    }

    "return a call to the HomeController onPageLoad method when there is no indication that there is a property in the estate" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.estateHasPropertyId))(any())) thenReturn None
      navigator.nextPage(Constants.estateHasPropertyId)(mockCacheMap) shouldBe routes.HomeController.onPageLoad()
    }

    "return a call to the BroughtForwardAllowanceController onPageLoad method when there is some brought forward allowance" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyBroughtForwardAllowanceId))(any())) thenReturn Some(true)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceId)(mockCacheMap) shouldBe routes.BroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the AnyDownsizingAllowanceController onPageLoad method when there is no brought forward allowance" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyBroughtForwardAllowanceId))(any())) thenReturn Some(false)
      navigator.nextPage(Constants.anyBroughtForwardAllowanceId)(mockCacheMap) shouldBe routes.AnyDownsizingAllowanceController.onPageLoad()
    }

    "return a call to the AnyDownsizingAllowanceController onPageLoad method from the BroughtForwardController" in {
      navigator.nextPage(Constants.broughtForwardAllowanceId)(mock[CacheMap]) shouldBe routes.AnyDownsizingAllowanceController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance onPageLoad method when no exemptions apply to the property" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyExemptionId))(any())) thenReturn Some(false)
      navigator.nextPage(Constants.anyExemptionId)(mockCacheMap) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Property Value After Exemption onPageLoad method when exemptions apply to the property" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyExemptionId))(any())) thenReturn Some(true)
      navigator.nextPage(Constants.anyExemptionId)(mockCacheMap) shouldBe routes.PropertyValueAfterExemptionController.onPageLoad()
    }

    "return a call to the Any Brought Forward Allowance onPageLoad method from the Property Value After Exemption controller" in {
      navigator.nextPage(Constants.propertyValueAfterExemptionId)(mock[CacheMap]) shouldBe routes.AnyBroughtForwardAllowanceController.onPageLoad()
    }

    "return a call to the Results Controller onPageLoad method when there is no downsizing allowance" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyDownsizingAllowanceId))(any())) thenReturn Some(false)
      navigator.nextPage(Constants.anyDownsizingAllowanceId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the date of property disposal controller onPageLoad method when there is some downsizing allowance" in {
      pending
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyDownsizingAllowanceId))(any())) thenReturn Some(true)
      navigator.nextPage(Constants.anyDownsizingAllowanceId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the any assets passing to direct descendants controller onPageLoad method from the ValueOfDisposedProperty controller" in {
      navigator.nextPage(Constants.valueOfDisposedPropertyId)(mock[CacheMap]) shouldBe routes.AnyAssetsPassingToDirectDescendantsController.onPageLoad()
    }

    "return a call to the assest passing to direct descendant onPageLoad method when there are some assest passing to the direct descendant" in {
      pending
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyAssetsPassingToDirectDescendantsId))(any())) thenReturn Some(true)
      navigator.nextPage(Constants.anyAssetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()
    }

    "return a call to the results onPageLoad method when there are no assest passing to the direct descendant" in {
      val mockCacheMap = mock[CacheMap]
      when(mockCacheMap.getEntry[Boolean](matches(Constants.anyAssetsPassingToDirectDescendantsId))(any())) thenReturn Some(false)
      navigator.nextPage(Constants.anyAssetsPassingToDirectDescendantsId)(mockCacheMap) shouldBe routes.ResultsController.onPageLoad()

    }
  }
}
