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
import uk.gov.hmrc.residencenilratebandcalculator.models.GetTransitionOutReason.{DateOfDeath, DirectDescendant, GrossingUpForOtherProperty, GrossingUpForResidence}

class GetTransitionOutReasonSpec extends UnitSpec with WithFakeApplication with MockitoSugar {
  "GetTransitionOutReason" must {
    "get DateOfDeath reason if date of death is before the eligibility date" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.dateOfDeath) thenReturn Some(Constants.eligibilityDate.minusDays(1))
      GetTransitionOutReason(userAnswers) shouldBe DateOfDeath
    }

    "get DirectDescendant reason if nothing has been left to direct descendants" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.partOfEstatePassingToDirectDescendants) thenReturn Some(false)
      GetTransitionOutReason(userAnswers) shouldBe DirectDescendant
    }

    "get GrossingUpForResidence reason when grossing up is applied to the residence" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.grossingUpOnEstateProperty) thenReturn Some(true)
      GetTransitionOutReason(userAnswers) shouldBe GrossingUpForResidence
    }

    "get GrossingUpForOtherProperty reason when grossing up is applied to other property" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.grossingUpOnEstateAssets) thenReturn Some(true)
      GetTransitionOutReason(userAnswers) shouldBe GrossingUpForOtherProperty
    }
  }
}
