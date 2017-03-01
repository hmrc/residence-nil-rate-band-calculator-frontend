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

import play.api.libs.json._
import play.api.libs.json.Writes._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class ChargeableValueOfResidence(value: Int, valueCloselyInherited: Int)

object ChargeableValueOfResidence {
  val propertyValueAfterExemptionReads: Reads[ChargeableValueOfResidence] = (
    (__ \ "value").read[Int] and
    (__ \ "valueCloselyInherited").read[Int]
  )(ChargeableValueOfResidence.apply _)

  val propertyValueAfterExemptionWrites: Writes[ChargeableValueOfResidence] = (
    (__ \ "value").write[Int] and
    (__ \ "valueCloselyInherited").write[Int]
  )(unlift(ChargeableValueOfResidence.unapply _))

  implicit val propertyValueAfterExemptionFormat: Format[ChargeableValueOfResidence] =
    Format(propertyValueAfterExemptionReads, propertyValueAfterExemptionWrites)
}

sealed abstract class ChargeableValueOfResidenceKey
case object Value extends ChargeableValueOfResidenceKey
case object ValueCloselyInherited extends ChargeableValueOfResidenceKey
