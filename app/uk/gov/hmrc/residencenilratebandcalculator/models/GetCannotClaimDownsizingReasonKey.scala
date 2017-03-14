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

object GetCannotClaimDownsizingReasonKey {
  def apply(userAnswers: UserAnswers) =
    userAnswers.anyAssetsPassingToDirectDescendants match {
      case Some(false) => "cannot_claim_downsizing.no_assets_passing_to_direct_descendants_reason"
      case _ => "cannot_claim_downsizing.date_of_disposal_too_early_reason"
    }
}
