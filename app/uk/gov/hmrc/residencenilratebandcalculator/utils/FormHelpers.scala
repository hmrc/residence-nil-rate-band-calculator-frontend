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

package uk.gov.hmrc.residencenilratebandcalculator.utils

import play.api.data.{Form, FormError}
import uk.gov.hmrc.residencenilratebandcalculator.models._

import scala.util.Try

object FormHelpers {

  /**
    * Converts each element in a string seq into a number. If any of the elements are blank then
    * a Left of anyBlankValue is returned. If any of the elements are non-numeric then a Left of
    * anyNonNumericValue is returned. Otherwise a Right of an integer seq is returned.
    */
  def convertToNumbers(dateElements: Seq[String],
                       anyBlankValue: String,
                       anyNonNumericValue: String): Either[String, Seq[Int]] = {
    if (dateElements.exists(x => x.trim().isEmpty)) {
      Left(anyBlankValue)
    } else {
      val attemptedConvertedElements = dateElements.map { x =>
        Try(x.toInt)
      }
      if (attemptedConvertedElements.exists(_.isFailure)) {
        Left(anyNonNumericValue)
      } else {
        Right(attemptedConvertedElements.map(_.get))
      }
    }
  }

  def getOrException[A](option: Option[A], errorMessage: String = "No element found"): A =
    option.fold(throw new RuntimeException(errorMessage))(identity)
}
