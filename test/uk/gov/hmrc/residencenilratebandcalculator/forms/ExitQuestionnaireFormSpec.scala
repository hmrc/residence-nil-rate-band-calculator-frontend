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

package uk.gov.hmrc.residencenilratebandcalculator.forms

import play.api.libs.json.Json
import uk.gov.hmrc.residencenilratebandcalculator.models.ExitQuestionnaire

import scala.util.Random

class ExitQuestionnaireFormSpec extends FormSpec {

  "Exit Questionnaire Form" must {

    "bind when no keys are supplied" in {
      val form = ExitQuestionnaireForm().bind(Map("" -> ""))
      form.get shouldBe ExitQuestionnaire(None, None, None, None, None, None)
      form.errors.size shouldBe 0
    }

    "bind when values are supplied" in {
      val form = ExitQuestionnaireForm().bind(Json.toJson(ExitQuestionnaire(Some("a"), Some("b"), Some("c"), Some("d"), Some("e"), Some("f"))))
      form.get shouldBe ExitQuestionnaire(Some("a"), Some("b"), Some("c"), Some("d"), Some("e"), Some("f"))
      form.errors.size shouldBe 0
    }

    "fail to bind when the comments field is too long" in {
      val longString = Random.alphanumeric.take(ExitQuestionnaireForm.MAX_COMMENT_LENGTH + 1).mkString
      val form = ExitQuestionnaireForm().bind(Json.toJson(ExitQuestionnaire(None, None, Some(longString), None, None, None)))
      form.errors.size shouldBe 1
    }
  }
}
