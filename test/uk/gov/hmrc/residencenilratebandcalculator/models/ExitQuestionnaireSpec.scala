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

import uk.gov.hmrc.residencenilratebandcalculator.BaseSpec

class ExitQuestionnaireSpec extends BaseSpec {

  "Exit Questionnaire" must {
    "give the correct options for service difficulty" in {
      ExitQuestionnaire.serviceDifficultyOptions shouldBe Seq(
        RadioOption("service_difficulty.very_easy", "very_easy", "service_difficulty.very_easy"),
        RadioOption("service_difficulty.easy", "easy", "service_difficulty.easy"),
        RadioOption("service_difficulty.neither", "neither", "service_difficulty.neither"),
        RadioOption("service_difficulty.difficult", "difficult", "service_difficulty.difficult"),
        RadioOption("service_difficulty.very_difficult", "very_difficult", "service_difficulty.very_difficult")
      )
    }

    "give the correct options for service feel" in {
      ExitQuestionnaire.serviceFeelOptions shouldBe Seq(
        RadioOption("service_feel.very_satisfied", "very_satisfied", "service_feel.very_satisfied"),
        RadioOption("service_feel.satisfied", "satisfied", "service_feel.satisfied"),
        RadioOption("service_feel.neither", "neither", "service_feel.neither"),
        RadioOption("service_feel.dissatisfied", "dissatisfied", "service_feel.dissatisfied"),
        RadioOption("service_feel.very_dissatisfied", "very_dissatisfied", "service_feel.very_dissatisfied")
      )
    }
  }
}
