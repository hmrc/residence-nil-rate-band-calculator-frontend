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

class GetCannotClaimDowisizingReasonKeySpec extends UnitSpec with WithFakeApplication with MockitoSugar {
  "Get Cannot Claim Dowisizing Reason Key" must {
    "get the 'no assets passing to a direct descendant' reason key when there are no assets passing to direct descendants" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.anyAssetsPassingToDirectDescendants) thenReturn Some(false)
      GetCannotClaimDownsizingReasonKey(userAnswers) shouldBe "cannot_claim_downsizing.no_assets_passing_to_direct_descendants_reason"
    }

    "get the 'date of disposal too early' reason key when the property is disposed of before 8 July 2015" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.dateOfDisposal) thenReturn Some(Constants.downsizingEligibilityDate.minusDays(1))
      GetCannotClaimDownsizingReasonKey(userAnswers) shouldBe "cannot_claim_downsizing.date_of_disposal_too_early_reason"
    }
  }
}
