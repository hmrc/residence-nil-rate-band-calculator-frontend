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

import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.residencenilratebandcalculator.models.ExitQuestionnaire

object ExitQuestionnaireForm {

  val MAX_COMMENT_LENGTH: Int = 1200

  def apply(): Form[ExitQuestionnaire] = Form(
    mapping(
      "serviceDifficulty" -> optional(text),
      "serviceFeel" -> optional(text),
      "comments" -> optional(text(maxLength = MAX_COMMENT_LENGTH)),
      "fullName" -> optional(text),
      "email" -> optional(text),
      "phoneNumber" -> optional(text)
    )(ExitQuestionnaire.apply)(ExitQuestionnaire.unapply)
  )
}
