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

import org.joda.time.LocalDate
import org.scalatest.{BeforeAndAfter, Matchers}
import org.scalatest.mock.MockitoSugar
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import uk.gov.hmrc.play.test.UnitSpec

class CalculationInputSpec extends UnitSpec with MockitoSugar with Matchers with BeforeAndAfter {

  val cacheMapKey = "a"
  val dateOfDeath = new LocalDate(2020, 1, 1)
  val grossEstateValue = 1
  val chargeableTransferAmount = 2
  val propertyValue = 3
  val percentageCloselyInherited = 4
  val broughtForwardAllowance = 5
  val dateOfDisposal = new LocalDate(2018, 2, 2)
  val propertyValueAfterExemption = PropertyValueAfterExemption(6, 7)

  var userAnswers: UserAnswers = _

  before {
    userAnswers = mock[UserAnswers]
  }
  
  def setupMock(anyAssetsPassingToDirectDescendants: Option[Boolean] = None,
                anyBroughtForwardAllowance: Option[Boolean] = None,
                anyBroughtForwardAllowanceOnDisposal: Option[Boolean] = None,
                anyDownsizingAllowance: Option[Boolean] = None,
                anyExemption: Option[Boolean] = None,
                anyPropertyCloselyInherited: Option[Boolean] = None,
                assetsPassingToDirectDescendants: Option[Int] = None,
                broughtForwardAllowance: Option[Int] = None,
                broughtForwardAllowanceOnDisposal: Option[Int] = None,
                chargeableTransferAmount: Option[Int] = None,
                dateOfDeath: Option[LocalDate] = None,
                dateOfDisposal: Option[LocalDate] = None,
                estateHasProperty: Option[Boolean] = None,
                grossEstateValue: Option[Int] = None,
                percentageCloselyInherited: Option[Int] = None,
                propertyValueAfterExemption: Option[PropertyValueAfterExemption] = None,
                propertyValue: Option[Int] = None,
                purposeOfUse: Option[String] = None,
                valueOfDisposedProperty: Option[Int] = None
               ) = {
    when(userAnswers.anyAssetsPassingToDirectDescendants) thenReturn anyAssetsPassingToDirectDescendants
    when(userAnswers.anyBroughtForwardAllowance) thenReturn anyBroughtForwardAllowance
    when(userAnswers.anyBroughtForwardAllowanceOnDisposal) thenReturn anyBroughtForwardAllowanceOnDisposal
    when(userAnswers.anyDownsizingAllowance) thenReturn anyDownsizingAllowance
    when(userAnswers.anyExemption) thenReturn anyExemption
    when(userAnswers.anyPropertyCloselyInherited) thenReturn anyPropertyCloselyInherited
    when(userAnswers.assetsPassingToDirectDescendants) thenReturn assetsPassingToDirectDescendants
    when(userAnswers.broughtForwardAllowance) thenReturn broughtForwardAllowance
    when(userAnswers.broughtForwardAllowanceOnDisposal) thenReturn broughtForwardAllowanceOnDisposal
    when(userAnswers.chargeableTransferAmount) thenReturn chargeableTransferAmount
    when(userAnswers.dateOfDeath) thenReturn dateOfDeath
    when(userAnswers.dateOfDisposal) thenReturn dateOfDisposal
    when(userAnswers.estateHasProperty) thenReturn estateHasProperty
    when(userAnswers.grossEstateValue) thenReturn grossEstateValue
    when(userAnswers.percentageCloselyInherited) thenReturn percentageCloselyInherited
    when(userAnswers.propertyValueAfterExemption) thenReturn propertyValueAfterExemption
    when(userAnswers.propertyValue) thenReturn propertyValue
    when(userAnswers.purposeOfUse) thenReturn purposeOfUse
    when(userAnswers.valueOfDisposedProperty) thenReturn valueOfDisposedProperty
  }

  "Calculation Input" when {

    "the user's answers are in a consistent state" must {

      "construct correctly when there is no property, brought forward allowance or downsizing" in {
        setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
          estateHasProperty = Some(false), anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

        val calculationInput = CalculationInput(userAnswers)

        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount, 0, 0, 0, None, None)
      }

      "construct correctly when there is a property, none of which is closely inherited, and no brought forward allowance or downsizing" in {
        setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
          estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
          anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

        val calculationInput = CalculationInput(userAnswers)

        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount, propertyValue, 0, 0, None, None)
      }

      "construct correctly when there is a property, some of which is closely inherited, and no exemptions, brought forward allowance or downsizing" in {
        setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
          estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
          percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
          anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

        val calculationInput = CalculationInput(userAnswers)

        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, 0, None, None)
      }

      "construct correctly when there is a property, some of which is closely inherited, some exemptions, and no brought forward allowance or downsizing" in {
        setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
          estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
          percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(true),
          propertyValueAfterExemption = Some(propertyValueAfterExemption),
          anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

        val calculationInput = CalculationInput(userAnswers)

        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, 0, Some(propertyValueAfterExemption), None)
      }

      "construct correctly when there is a property, some of which is closely inherited, no exemptions, some brought forward allowance and no downsizing" in {
        setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
          estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
          percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
          anyBroughtForwardAllowance = Some(true), broughtForwardAllowance = Some(broughtForwardAllowance), anyDownsizingAllowance = Some(false))

        val calculationInput = CalculationInput(userAnswers)

        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, broughtForwardAllowance, None, None)
      }
    }
  }
}
