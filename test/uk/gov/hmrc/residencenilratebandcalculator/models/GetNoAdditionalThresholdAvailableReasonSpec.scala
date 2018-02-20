/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.residencenilratebandcalculator.models.GetNoAdditionalThresholdAvailableReason.{NoProperty, NotCloselyInherited}

class GetNoAdditionalThresholdAvailableReasonSpec extends BaseSpec with WithFakeApplication with MockitoSugar {
  "GetNoAdditionalThresholdAvailableReason" must {
    "get the 'Not closely inherited' reason when there is no closely inherited property" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyPassingToDirectDescendants) thenReturn Some(Constants.none)
      GetNoAdditionalThresholdAvailableReason(userAnswers) shouldBe NotCloselyInherited
    }

    "get the 'No property' reason when there is no property in the estate" in {
      val userAnswers = mock[UserAnswers]
      when(userAnswers.propertyInEstate) thenReturn Some(false)
      GetNoAdditionalThresholdAvailableReason(userAnswers) shouldBe NoProperty
    }
  }
}
