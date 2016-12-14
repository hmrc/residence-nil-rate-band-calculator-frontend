/*
 * Copyright 2016 HM Revenue & Customs
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

    "when the GrossEstateValue is used at the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the ChargeableTransferAmountController" in {
      navigator.nextPage(Constants.grossEstateValueId)(mock[CacheMap]) shouldBe routes.ChargeableTransferAmountController.onPageLoad()
    }

    "when the PropertyValue is used at the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the Results controller" in {
      navigator.nextPage(Constants.propertyValueId)(mock[CacheMap]) shouldBe routes.ResultsController.onPageLoad()
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
  }
}
