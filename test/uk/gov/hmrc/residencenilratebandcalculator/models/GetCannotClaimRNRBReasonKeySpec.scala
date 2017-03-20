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

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.models.GetCannotClaimRNRBReason.{NoProperty, NotCloselyInherited}

class GetCannotClaimRNRBReasonSpec extends UnitSpec with WithFakeApplication with MockitoSugar {
  "GetCannotClaimRNRBReason" must {
    "get the 'Not closely inherited' reason when there is no closely inherited property" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.anyPropertyCloselyInherited) thenReturn Some(Constants.none)
      GetCannotClaimRNRBReason(userAnswers) shouldBe NotCloselyInherited
    }

    "get the 'No property' reason when there is no property in the estate" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.estateHasProperty) thenReturn Some(false)
      GetCannotClaimRNRBReason(userAnswers) shouldBe NoProperty
    }
  }
}
