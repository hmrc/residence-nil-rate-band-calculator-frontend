/*
 * Copyright 2020 HM Revenue & Customs
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
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec
import uk.gov.hmrc.residencenilratebandcalculator.models.GetUnableToCalculateThresholdIncreaseReason.{GrossingUpForOtherProperty, GrossingUpForResidence}

class GetUnableToCalculateThresholdIncreaseReasonSpec extends BaseSpec with MockitoSugar {
  "GetUnableToCalculateThresholdIncreaseReason" must {

    "get GrossingUpForResidence reason when grossing up is applied to the residence" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.grossingUpOnEstateProperty) thenReturn Some(true)
      GetUnableToCalculateThresholdIncreaseReason(userAnswers) shouldBe GrossingUpForResidence
    }

    "get GrossingUpForOtherProperty reason when grossing up is applied to other property" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.grossingUpOnEstateAssets) thenReturn Some(true)
      GetUnableToCalculateThresholdIncreaseReason(userAnswers) shouldBe GrossingUpForOtherProperty
    }
  }
}
