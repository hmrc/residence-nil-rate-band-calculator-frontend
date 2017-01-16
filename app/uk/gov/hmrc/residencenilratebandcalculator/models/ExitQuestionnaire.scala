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

import play.api.libs.json.Json

case class ExitQuestionnaire (serviceDifficulty: Option[String],
                              serviceFeel: Option[String],
                              comments: Option[String],
                              fullName: Option[String],
                              email: Option[String],
                              phoneNumber: Option[String])

object ExitQuestionnaire {
  val VERY_SATISFIED = "very_satisfied"
  val SATISFIED = "satisfied"
  val NEITHER_SU = "neither"
  val UNSATISFIED = "unsatisfied"
  val VERY_UNSATISFIED = "very_unsatisfied"

  val VERY_EASY = "very_easy"
  val EASY = "easy"
  val NEITHER_ED = "neither"
  val DIFFICULT = "difficult"
  val VERY_DIFFICULT = "very_difficult"

  implicit val formats = Json.format[ExitQuestionnaire]
}

sealed abstract class ExitQuestionnaireKey
case object ServiceDifficulty extends ExitQuestionnaireKey
case object ServiceFeel extends ExitQuestionnaireKey
case object Comments extends ExitQuestionnaireKey
case object FullName extends ExitQuestionnaireKey
case object PhoneNumber extends ExitQuestionnaireKey
case object Email extends ExitQuestionnaireKey
