/*
 * Copyright 2016 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.models._

object FormHelpers {

  def getValue[A](form: Option[Form[A]]) = {
    val either = for {
      theForm <- form.toRight("").right
      theValue <- theForm.value.toRight("").right
    } yield theValue

    either.merge
  }

  def getDatePart(form: Option[Form[Date]], datePart: DatePart) = {
    val either = for {
      theForm <- form.toRight("").right
      theDate <- theForm.value.toRight("").right
    } yield theDate

    either match {
      case Left(_) => ""
      case Right(d) => datePart match {
        case Day => d.day
        case Month => d.month
        case Year => d.year
      }
    }
  }
}
