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
import uk.gov.hmrc.play.test.WithFakeApplication
import uk.gov.hmrc.residencenilratebandcalculator.{BaseSpec, Constants}
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoDownsizingThresholdIncreaseReason.{DatePropertyWasChangedTooEarly, NoAssetsPassingToDirectDescendants}

class GetNoDownsizingThresholdIncreaseReasonSpec extends BaseSpec with WithFakeApplication with MockitoSugar {
  "Get No Downsizing Threshold Increase Reason" must {
    "get the 'no assets passing to a direct descendant' reason when there are no assets passing to direct descendants" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.assetsPassingToDirectDescendants) thenReturn Some(false)
      GetNoDownsizingThresholdIncreaseReason(userAnswers) shouldBe NoAssetsPassingToDirectDescendants
    }

    "get the 'Date Property Was Changed too early' reason when the property is disposed of before 8 July 2015" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.datePropertyWasChanged) thenReturn Some(Constants.downsizingEligibilityDate.minusDays(1))
      GetNoDownsizingThresholdIncreaseReason(userAnswers) shouldBe DatePropertyWasChangedTooEarly
    }
  }
}
