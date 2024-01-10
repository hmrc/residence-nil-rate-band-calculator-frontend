/*
 * Copyright 2023 HM Revenue & Customs
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

import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.common.CommonPlaySpec

import java.time.LocalDate

class CalculationInputSpec extends CommonPlaySpec with MockitoSugar with Matchers with BeforeAndAfter {

  val cacheMapKey = "a"
  val dateOfDeath: LocalDate = LocalDate.of(2020, 1, 1)
  val valueOfEstate = 1
  val chargeableEstateValue = 2
  val propertyValue = 3
  val percentagePassedToDirectDescendants = 4
  val valueBeingTransferred = 5
  val valueOfChangedProperty = 6
  val valueOfAssetsPassing = 7
  val valueAvailableWhenPropertyChanged = 8
  val datePropertyWasChanged: LocalDate = LocalDate.of(2018, 2, 2)
  val chargeablePropertyValue = 9
  val chargeableInheritedPropertyValue = 10

  var userAnswers: UserAnswers = _

  before {
    userAnswers = mock[UserAnswers]
  }
  
  def setupMock(assetsPassingToDirectDescendants: Option[Boolean] = None,
                transferAnyUnusedThreshold: Option[Boolean] = None,
                transferAvailableWhenPropertyChanged: Option[Boolean] = None,
                claimDownsizingThreshold: Option[Boolean] = None,
                exemptionsAndReliefClaimed: Option[Boolean] = None,
                propertyPassingToDirectDescendants: Option[String] = None,
                valueOfAssetsPassing: Option[Int] = None,
                valueBeingTransferred: Option[Int] = None,
                valueAvailableWhenPropertyChanged: Option[Int] = None,
                chargeableEstateValue: Option[Int] = None,
                dateOfDeath: Option[LocalDate] = None,
                datePropertyWasChanged: Option[LocalDate] = None,
                propertyInEstate: Option[Boolean] = None,
                valueOfEstate: Option[Int] = None,
                percentagePassedToDirectDescendants: Option[BigDecimal] = None,
                grossingUpOnEstateProperty: Option[Boolean] = None,
                chargeablePropertyValue: Option[Int] = None,
                chargeableInheritedPropertyValue: Option[Int] = None,
                propertyValue: Option[Int] = None,
                valueOfChangedProperty: Option[Int] = None
               ): OngoingStubbing[Option[Boolean]] = {
    when(userAnswers.assetsPassingToDirectDescendants) thenReturn assetsPassingToDirectDescendants
    when(userAnswers.transferAnyUnusedThreshold) thenReturn transferAnyUnusedThreshold
    when(userAnswers.transferAvailableWhenPropertyChanged) thenReturn transferAvailableWhenPropertyChanged
    when(userAnswers.claimDownsizingThreshold) thenReturn claimDownsizingThreshold
    when(userAnswers.exemptionsAndReliefClaimed) thenReturn exemptionsAndReliefClaimed
    when(userAnswers.propertyPassingToDirectDescendants) thenReturn propertyPassingToDirectDescendants
    when(userAnswers.valueOfAssetsPassing) thenReturn valueOfAssetsPassing
    when(userAnswers.valueBeingTransferred) thenReturn valueBeingTransferred
    when(userAnswers.valueAvailableWhenPropertyChanged) thenReturn valueAvailableWhenPropertyChanged
    when(userAnswers.chargeableEstateValue) thenReturn chargeableEstateValue
    when(userAnswers.dateOfDeath) thenReturn dateOfDeath
    when(userAnswers.datePropertyWasChanged) thenReturn datePropertyWasChanged
    when(userAnswers.propertyInEstate) thenReturn propertyInEstate
    when(userAnswers.valueOfEstate) thenReturn valueOfEstate
    when(userAnswers.percentagePassedToDirectDescendants) thenReturn percentagePassedToDirectDescendants
    when(userAnswers.grossingUpOnEstateProperty) thenReturn grossingUpOnEstateProperty
    when(userAnswers.chargeablePropertyValue) thenReturn chargeablePropertyValue
    when(userAnswers.chargeableInheritedPropertyValue) thenReturn chargeableInheritedPropertyValue
    when(userAnswers.propertyValue) thenReturn propertyValue
    when(userAnswers.valueOfChangedProperty) thenReturn valueOfChangedProperty
    percentagePassedToDirectDescendants.foreach { percentage =>
      when(userAnswers.getPercentagePassedToDirectDescendants) thenReturn percentage
    }
    when(userAnswers.isTransferAvailableWhenPropertyChanged) thenReturn transferAvailableWhenPropertyChanged
  }

  "Calculation Input" when {

    "there is no property, value being transferred or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue), propertyInEstate = Some(false), transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(false),
        percentagePassedToDirectDescendants = Some(BigDecimal(0))
      )

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":0,
            |"percentagePassedToDirectDescendants":0,
            |"valueBeingTransferred":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, none of which is closely inherited, and no value being transferred or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
        transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(false),
        percentagePassedToDirectDescendants = Some(BigDecimal(0))
      )

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, propertyValue, 0, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":0,
            |"valueBeingTransferred":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, all of which is closely inherited, and no exemptions, value being transferred or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.all),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue,
          propertyValue, percentagePassedToDirectDescendants, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }


    "there is a property, some of which is closely inherited, and no exemptions, value being transferred or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue,
          propertyValue, percentagePassedToDirectDescendants, 0, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":0
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, some exemptions, and no value being transferred or downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(true), grossingUpOnEstateProperty = Some(false),
        chargeablePropertyValue = Some(chargeablePropertyValue),
        chargeableInheritedPropertyValue = Some(chargeableInheritedPropertyValue),
        transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue,
          propertyValue, percentagePassedToDirectDescendants, 0,
          Some(PropertyValueAfterExemption(chargeablePropertyValue, chargeableInheritedPropertyValue)), None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":0,
            |"propertyValueAfterExemption":{
            |  "value":9,
            |  "inheritedValue":10
            |  }
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, some value being transferred and no downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(true), valueBeingTransferred = Some(valueBeingTransferred), claimDownsizingThreshold = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue,
          propertyValue, percentagePassedToDirectDescendants, valueBeingTransferred, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":5
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, no value being transferred and downsizing" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true), datePropertyWasChanged = Some(datePropertyWasChanged),
        valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue,
          propertyValue, percentagePassedToDirectDescendants, 0, None, Some(DownsizingDetails(datePropertyWasChanged, valueOfChangedProperty, 0, 0)))
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":0,
            |"downsizingDetails":{
            |  "datePropertyWasChanged":"2018-02-02",
            |  "valueOfChangedProperty":6,
            |  "valueOfAssetsPassing":0,
            |  "valueAvailableWhenPropertyChanged":0
            |}
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, no value being transferred, " +
      "downsizing and other assets left to a direct descendant" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true), datePropertyWasChanged = Some(datePropertyWasChanged),
        valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(true),
        valueOfAssetsPassing = Some(valueOfAssetsPassing), transferAvailableWhenPropertyChanged = Some(false))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, propertyValue, percentagePassedToDirectDescendants,
          0, None, Some(DownsizingDetails(datePropertyWasChanged, valueOfChangedProperty, valueOfAssetsPassing, 0)))
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":0,
            |"downsizingDetails":{
            |  "datePropertyWasChanged":"2018-02-02",
            |  "valueOfChangedProperty":6,
            |  "valueOfAssetsPassing":7,
            |  "valueAvailableWhenPropertyChanged":0
            |}
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, value being transferred, downsizing and " +
      "other assets left to a direct descendant" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(true), valueBeingTransferred = Some(valueBeingTransferred), claimDownsizingThreshold = Some(true),
        datePropertyWasChanged = Some(datePropertyWasChanged), valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(true),
        valueOfAssetsPassing = Some(valueOfAssetsPassing), transferAvailableWhenPropertyChanged = Some(true),
        valueAvailableWhenPropertyChanged = Some(valueAvailableWhenPropertyChanged))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, propertyValue, percentagePassedToDirectDescendants,
          valueBeingTransferred, None, Some(DownsizingDetails(datePropertyWasChanged, valueOfChangedProperty, valueOfAssetsPassing,
            valueAvailableWhenPropertyChanged)))
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":5,
            |"downsizingDetails":{
            |  "datePropertyWasChanged":"2018-02-02",
            |  "valueOfChangedProperty":6,
            |  "valueOfAssetsPassing":7,
            |  "valueAvailableWhenPropertyChanged":8
            |}
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, value being transferred, downsizing is claimed " +
      "but the Date Property Was Changed is before the eligibility date" must {

      def buildAnswers = setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(true), valueBeingTransferred = Some(valueBeingTransferred), claimDownsizingThreshold = Some(true),
        datePropertyWasChanged = Some(Constants.downsizingEligibilityDate.minusDays(1)))

      "construct correctly from user answers" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        calculationInput shouldBe CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, propertyValue, percentagePassedToDirectDescendants,
          valueBeingTransferred, None, None)
      }

      "render to JSON" in {
        buildAnswers
        val calculationInput = CalculationInput(userAnswers)
        Json.toJson(calculationInput).toString shouldBe
          """{
            |"dateOfDeath":"2020-01-01",
            |"valueOfEstate":1,
            |"chargeableEstateValue":2,
            |"propertyValue":3,
            |"percentagePassedToDirectDescendants":4,
            |"valueBeingTransferred":5
            |}""".stripMargin.replaceAll("\\s+", "")
      }
    }

    "there is no value for 'date of death'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock()
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Date of Death was not answered"
      }
    }

    "there is no value for 'value of estate'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Value Of Estate was not answered"
      }
    }

    "there is no value for 'chargeable transfer amount'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Chargeable Estate Value was not answered"
      }
    }

    "there is no value for 'property in estate'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Property In Estate was not answered"
      }
    }

    "'property in estate' is true but there is no value for 'property value'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Property Value was not answered"
      }
    }

    "'property in estate' is true but there is no value for 'property passing to direct descendants'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Property Passing To Direct Descendants was not answered"
      }
    }

    "'property in estate' is true and 'property passing to direct descendants' is 'some' but there is no value for 'percentage passed to direct descendants'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Percentage Passed To Direct Descendants was not answered"
      }
    }

    "'property passing to direct descendants' is 'all' but there is no value for 'exemptions and relief claimed'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.all),
            percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Exemptions And Relief Claimed was not answered"
      }
    }

    "'property passing to direct descendants' is 'some' but there is no value for 'exemptions and relief claimed'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
            percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Exemptions And Relief Claimed was not answered"
      }
    }

    "'property passing to direct descendants' is 'some' and 'exemptions and relief claimed' is true but there is no value for 'grossing up on estate property'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
            percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(true))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Grossing Up On Estate Property was not answered"
      }
    }

    "'grossing up on estate property' is false and there is no value for 'chargeable property value'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
            percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(true), grossingUpOnEstateProperty = Some(false))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Chargeable Property Value was not answered"
      }
    }

    "'grossing up on estate property' is false and there is no value for 'chargeable inhertied property value'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.some),
            percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants), exemptionsAndReliefClaimed = Some(true), grossingUpOnEstateProperty = Some(false),
            chargeablePropertyValue = Some(chargeablePropertyValue))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Chargeable Inherited Property Value was not answered"
      }
    }

    "there is no value for 'Transfer Any Unused Allowance'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Transfer Any Unused Allowance was not answered"
      }
    }

    "'Transfer Any Unused Allowance' is true but there is no value for 'value being transferred'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(true))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Value Being Transferred was not answered"
      }
    }

    "there is no value for 'claim downsizing threshold'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(false))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Claim Downsizing Threshold was not answered"
      }
    }

    "'claim downsizing threshold' is true but there is no value for 'Date Property Was Changed'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Date Property Was Changed was not answered"
      }
    }

    "'claim downsizing threshold' is true but there is no value for 'value of changed property'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true), datePropertyWasChanged = Some(datePropertyWasChanged))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Value Of Changed Property was not answered"
      }
    }

    "'claim downsizing threshold' is true but there is no value for 'assets passing to direct descendants'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true), datePropertyWasChanged = Some(datePropertyWasChanged),
            valueOfChangedProperty = Some(valueOfChangedProperty))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Assets Passing To Direct Descendants was not answered"
      }
    }

    "'claim downsizing threshold' is true and 'assets passing to direct descendants' is true but there is no value for " +
      "'value of assets passing'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true), datePropertyWasChanged = Some(datePropertyWasChanged),
            valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(true))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Value Of Assets Passing was not answered"
      }
    }

    "there is value being transferred, 'claim downsizing threshold' is true and 'assets passing to direct descendants' is true but there is no value for " +
      "'transfer available when property changed'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(true), valueBeingTransferred = Some(valueBeingTransferred), claimDownsizingThreshold = Some(true),
            datePropertyWasChanged = Some(datePropertyWasChanged), valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(true),
            valueOfAssetsPassing = Some(valueOfAssetsPassing))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Transfer Available When Property Changed was not answered"
      }
    }

    "there is no value being transferred, 'claim downsizing threshold' is true and 'assets passing to direct descendants' is true but there is no value " +
      "for 'transfer available when property changed'" must {
      "not throw an exception" in {
        setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
          transferAnyUnusedThreshold = Some(false), claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged), valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(true),
          valueOfAssetsPassing = Some(valueOfAssetsPassing))

        CalculationInput(userAnswers)
      }
    }

    "'claim downsizing threshold' is true, 'assets passing to direct descendants' is true and 'transfer available when property changed' is true " +
      "but there is no value for 'transfer available when property changed'" must {
      "throw an exception" in {
        val exception = intercept[IllegalArgumentException] {
          setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate), chargeableEstateValue = Some(chargeableEstateValue),
            propertyInEstate = Some(true), propertyValue = Some(propertyValue), propertyPassingToDirectDescendants = Some(Constants.none),
            transferAnyUnusedThreshold = Some(true), valueBeingTransferred = Some(valueBeingTransferred), claimDownsizingThreshold = Some(true),
            datePropertyWasChanged = Some(datePropertyWasChanged), valueOfChangedProperty = Some(valueOfChangedProperty), assetsPassingToDirectDescendants = Some(true),
            valueOfAssetsPassing = Some(valueOfAssetsPassing), transferAvailableWhenPropertyChanged = Some(true))
          CalculationInput(userAnswers)
        }

        exception.getMessage shouldBe "requirement failed: Value Available When Property Changed was not answered"
      }
    }
  }
}
