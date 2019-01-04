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

package uk.gov.hmrc.residencenilratebandcalculator.models

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, Constants}
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoThresholdIncreaseReason.{DateOfDeath, DirectDescendant}

class GetNoThresholdIncreaseReasonSpec extends BaseSpec with MockitoSugar {

  "Get No Threshold Increase Reason" must {

    "get DateOfDeath reason if date of death is before the eligibility date" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.dateOfDeath) thenReturn Some(Constants.eligibilityDate.minusDays(1))
      GetNoThresholdIncreaseReason(userAnswers) shouldBe DateOfDeath
    }

    "get DirectDescendant reason if nothing has been left to direct descendants" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.partOfEstatePassingToDirectDescendants) thenReturn Some(false)
      GetNoThresholdIncreaseReason(userAnswers) shouldBe DirectDescendant
    }
  }
}
