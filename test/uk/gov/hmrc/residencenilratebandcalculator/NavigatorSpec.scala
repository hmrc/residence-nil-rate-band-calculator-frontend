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

import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes

class NavigatorSpec extends UnitSpec with WithFakeApplication with MockitoSugar {
  val navigator = new Navigator

  "Navigator" must {
    "When the current call is not found, return a function, that when executed against any parameter routes to the page not found controller" in {
      navigator.nextPage("")(mock[CacheMap]) shouldBe routes.PageNotFoundController.onPageLoad()
    }


    "when the ChargeableTransferAmount is used as the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the page not found controller" in {
      navigator.nextPage(Constants.chargeableTransferAmountControllerId)(mock[CacheMap]) shouldBe routes.PageNotFoundController.onPageLoad()
    }

    "when the GrossEstateValue is used at the class id, the navigator must return a function that when executed against any" +
      "parameter goes to the ChargeableTransferAmountController" in {
      navigator.nextPage(Constants.grossEstateValueControllerId)(mock[CacheMap]) shouldBe routes.ChargeableTransferAmountController.onPageLoad()
    }
  }
}
