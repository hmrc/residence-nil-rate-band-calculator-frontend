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
import org.mockito.Mockito._
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class CalculationInputSpec extends UnitSpec with MockitoSugar with Matchers with BeforeAndAfter {

  val cacheMapKey = "a"
  val dateOfDeath = new LocalDate(2020, 1, 1)
  val grossEstateValue = 1
  val chargeableTransferAmount = 2
  val propertyValue = 3
  val percentageCloselyInherited = 4
  val broughtForwardAllowance = 5
  val valueOfDisposedProperty = 6
  val assetsPassingToDirectDescendants = 7
  val broughtForwardAllowanceOnDisposal = 8
  val dateOfDisposal = new LocalDate(2018, 2, 2)
  val propertyValueAfterExemption = 9
  val propertyValueAfterExemptionCloselyInherited = 10

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
                propertyValueAfterExemption: Option[Int] = None,
                propertyValueAfterExemptionCloselyInherited: Option[Int] = None,
                propertyValue: Option[Int] = None,
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
    when(userAnswers.propertyValueAfterExemptionCloselyInherited) thenReturn propertyValueAfterExemptionCloselyInherited
    when(userAnswers.propertyValue) thenReturn propertyValue
    when(userAnswers.valueOfDisposedProperty) thenReturn valueOfDisposedProperty
  }

  "Calculation Input" when {

    "there is no property, brought forward allowance or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue),
        chargeableTransferAmount = Some(chargeableTransferAmount), estateHasProperty = Some(false), anyBroughtForwardAllowance = Some(false),
        anyDownsizingAllowance = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount, 0, 0, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":0,
            |"percentageCloselyInherited":0,
            |"broughtForwardAllowance":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, none of which is closely inherited, and no brought forward allowance or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
        anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount, propertyValue, 0, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":0,
            |"broughtForwardAllowance":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, and no exemptions, brought forward allowance or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
        percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
        anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":4,
            |"broughtForwardAllowance":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, some exemptions, and no brought forward allowance or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
        percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(true),
        propertyValueAfterExemption = Some(propertyValueAfterExemption),
        propertyValueAfterExemptionCloselyInherited = Some(propertyValueAfterExemption),
        anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, 0, Some(PropertyValueAfterExemption(9,9)), None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":4,
            |"broughtForwardAllowance":0,
            |"propertyValueAfterExemption":{
            |  "value":9,
            |  "valueCloselyInherited":9
            |  }
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, some brought forward allowance and no downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
        percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
        anyBroughtForwardAllowance = Some(true), broughtForwardAllowance = Some(broughtForwardAllowance), anyDownsizingAllowance = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, broughtForwardAllowance, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":4,
            |"broughtForwardAllowance":5
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, no brought forward allowance and downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
        percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
        anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal),
        valueOfDisposedProperty = Some(valueOfDisposedProperty), anyAssetsPassingToDirectDescendants = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount,
          propertyValue, percentageCloselyInherited, 0, None, Some(DownsizingDetails(dateOfDisposal, valueOfDisposedProperty, 0, 0)))
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":4,
            |"broughtForwardAllowance":0,
            |"downsizingDetails":{
            |  "dateOfDisposal":"2018-02-02",
            |  "valueOfDisposedProperty":6,
            |  "valueCloselyInherited":0,
            |  "broughtForwardAllowanceAtDisposal":0
            |}
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, no brought forward allowance, " +
      "downsizing and other assets left to a direct descendant" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
        percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
        anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal),
        valueOfDisposedProperty = Some(valueOfDisposedProperty), anyAssetsPassingToDirectDescendants = Some(true),
        assetsPassingToDirectDescendants = Some(assetsPassingToDirectDescendants), anyBroughtForwardAllowanceOnDisposal = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount, propertyValue, percentageCloselyInherited,
          0, None, Some(DownsizingDetails(dateOfDisposal, valueOfDisposedProperty, assetsPassingToDirectDescendants, 0)))
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":4,
            |"broughtForwardAllowance":0,
            |"downsizingDetails":{
            |  "dateOfDisposal":"2018-02-02",
            |  "valueOfDisposedProperty":6,
            |  "valueCloselyInherited":7,
            |  "broughtForwardAllowanceAtDisposal":0
            |}
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, brought forward allowance, downsizing and " +
      "other assets left to a direct descendant" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
        estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true),
        percentageCloselyInherited = Some(percentageCloselyInherited), anyExemption = Some(false),
        anyBroughtForwardAllowance = Some(true), broughtForwardAllowance = Some(broughtForwardAllowance), anyDownsizingAllowance = Some(true),
        dateOfDisposal = Some(dateOfDisposal), valueOfDisposedProperty = Some(valueOfDisposedProperty), anyAssetsPassingToDirectDescendants = Some(true),
        assetsPassingToDirectDescendants = Some(assetsPassingToDirectDescendants), anyBroughtForwardAllowanceOnDisposal = Some(true),
        broughtForwardAllowanceOnDisposal = Some(broughtForwardAllowanceOnDisposal))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, grossEstateValue, chargeableTransferAmount, propertyValue, percentageCloselyInherited,
          broughtForwardAllowance, None, Some(DownsizingDetails(dateOfDisposal, valueOfDisposedProperty, assetsPassingToDirectDescendants,
            broughtForwardAllowanceOnDisposal)))
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"grossEstateValue":1,
            |"chargeableTransferAmount":2,
            |"propertyValue":3,
            |"percentageCloselyInherited":4,
            |"broughtForwardAllowance":5,
            |"downsizingDetails":{
            |  "dateOfDisposal":"2018-02-02",
            |  "valueOfDisposedProperty":6,
            |  "valueCloselyInherited":7,
            |  "broughtForwardAllowanceAtDisposal":8
            |}
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is no value for 'date of death'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock()
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Date of Death was not answered"
      }
    }

    "there is no value for 'gross estate value'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Gross Estate Value was not answered"
      }
    }

    "there is no value for 'chargeable transfer amount'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Chargeable Transfer Amount was not answered"
      }
    }

    "there is no value for 'estate has property'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Estate Has Property was not answered"
      }
    }

    "'estate has property' is true but there is no value for 'property value'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Property Value was not answered"
      }
    }

    "'estate has property' is true but there is no value for 'any property closely inherited'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Any Property Closely Inherited was not answered"
      }
    }

    "'estate has property' is true and 'any property closely inherited' is true but there is no value for 'percentage closely inherited'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(true))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Percentage Closely Inherited was not answered"
      }
    }

    "there is no value for 'any brought forward allowance'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Any Brought Forward Allowance was not answered"
      }
    }

    "'any brought forward allowance' is true but there is no value for 'brought forward allowance'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(true))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Brought Forward Allowance was not answered"
      }
    }

    "there is no value for 'any downsizing allowance'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Any Downsizing Allowance was not answered"
      }
    }

    "'any downsizing allowance' is true but there is no value for 'date of disposal'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Date of Disposal was not answered"
      }
    }

    "'any downsizing allowance' is true but there is no value for 'value of disposed property'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Value of Disposed Property was not answered"
      }
    }

    "'any downsizing allowance' is true but there is no value for 'any assets passing to direct descendants'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal),
            valueOfDisposedProperty = Some(valueOfDisposedProperty))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Any Assets Passing to Direct Descendants was not answered"
      }
    }

    "'any downsizing allowance' is true and 'any assets passing to direct descendants' is true but there is no value for " +
      "'assets passing to direct descendants'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal),
            valueOfDisposedProperty = Some(valueOfDisposedProperty), anyAssetsPassingToDirectDescendants = Some(true))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Assets Passing to Direct Descendants was not answered"
      }
    }

    "'any downsizing allowance' is true and 'any assets passing to direct descendants' is true but there is no value for " +
      "'any brought forward allowance on disposal'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal),
            valueOfDisposedProperty = Some(valueOfDisposedProperty), anyAssetsPassingToDirectDescendants = Some(true),
            assetsPassingToDirectDescendants = Some(assetsPassingToDirectDescendants))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Any Brought Forward Allowance on Disposal was not answered"
      }
    }

    "'any downsizing allowance' is true, 'any assets passing to direct descendants' is true and 'any brought forward allownace on disposal' is true " +
      "but there is no value for 'any brought forward allowance on disposal'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), grossEstateValue = Some(grossEstateValue), chargeableTransferAmount = Some(chargeableTransferAmount),
            estateHasProperty = Some(true), propertyValue = Some(propertyValue), anyPropertyCloselyInherited = Some(false),
            anyBroughtForwardAllowance = Some(false), anyDownsizingAllowance = Some(true), dateOfDisposal = Some(dateOfDisposal),
            valueOfDisposedProperty = Some(valueOfDisposedProperty), anyAssetsPassingToDirectDescendants = Some(true),
            assetsPassingToDirectDescendants = Some(assetsPassingToDirectDescendants), anyBroughtForwardAllowanceOnDisposal = Some(true))
          val calculationInput = CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Brought Forward Allowance on Disposal was not answered"
      }
    }
  }
}
