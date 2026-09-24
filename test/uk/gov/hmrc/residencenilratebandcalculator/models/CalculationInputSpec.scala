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

import org.mockito.Mockito.*
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfter
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.residencenilratebandcalculator.Constants
import uk.gov.hmrc.residencenilratebandcalculator.common.CommonPlaySpec
import uk.gov.hmrc.residencenilratebandcalculator.models.InputValidationError.*

import java.time.LocalDate

class CalculationInputSpec extends CommonPlaySpec with BeforeAndAfter {

  val cacheMapKey                         = "a"
  val dateOfDeath: LocalDate              = LocalDate.of(2020, 1, 1)
  val valueOfEstate                       = 1
  val chargeableEstateValue               = 2
  val propertyValue                       = 3
  val percentagePassedToDirectDescendants = 4
  val valueBeingTransferred               = 5
  val valueOfChangedProperty              = 6
  val valueOfAssetsPassing                = 7
  val valueAvailableWhenPropertyChanged   = 8
  val datePropertyWasChanged: LocalDate   = LocalDate.of(2018, 2, 2)
  val chargeablePropertyValue             = 9
  val chargeableInheritedPropertyValue    = 10

  var userAnswers: UserAnswers = scala.compiletime.uninitialized

  before {
    userAnswers = mock[UserAnswers]
  }

  def setupMock(
      assetsPassingToDirectDescendants: Option[Boolean] = None,
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
    when(userAnswers.assetsPassingToDirectDescendants).thenReturn(assetsPassingToDirectDescendants)
    when(userAnswers.transferAnyUnusedThreshold).thenReturn(transferAnyUnusedThreshold)
    when(userAnswers.transferAvailableWhenPropertyChanged).thenReturn(transferAvailableWhenPropertyChanged)
    when(userAnswers.claimDownsizingThreshold).thenReturn(claimDownsizingThreshold)
    when(userAnswers.exemptionsAndReliefClaimed).thenReturn(exemptionsAndReliefClaimed)
    when(userAnswers.propertyPassingToDirectDescendants).thenReturn(propertyPassingToDirectDescendants)
    when(userAnswers.valueOfAssetsPassing).thenReturn(valueOfAssetsPassing)
    when(userAnswers.valueBeingTransferred).thenReturn(valueBeingTransferred)
    when(userAnswers.valueAvailableWhenPropertyChanged).thenReturn(valueAvailableWhenPropertyChanged)
    when(userAnswers.chargeableEstateValue).thenReturn(chargeableEstateValue)
    when(userAnswers.dateOfDeath).thenReturn(dateOfDeath)
    when(userAnswers.datePropertyWasChanged).thenReturn(datePropertyWasChanged)
    when(userAnswers.propertyInEstate).thenReturn(propertyInEstate)
    when(userAnswers.valueOfEstate).thenReturn(valueOfEstate)
    when(userAnswers.percentagePassedToDirectDescendants).thenReturn(percentagePassedToDirectDescendants)
    when(userAnswers.grossingUpOnEstateProperty).thenReturn(grossingUpOnEstateProperty)
    when(userAnswers.chargeablePropertyValue).thenReturn(chargeablePropertyValue)
    when(userAnswers.chargeableInheritedPropertyValue).thenReturn(chargeableInheritedPropertyValue)
    when(userAnswers.propertyValue).thenReturn(propertyValue)
    when(userAnswers.valueOfChangedProperty).thenReturn(valueOfChangedProperty)

    percentagePassedToDirectDescendants.foreach { percentage =>
      when(userAnswers.getPercentagePassedToDirectDescendants).thenReturn(percentage)
    }

    when(userAnswers.isTransferAvailableWhenPropertyChanged).thenReturn(transferAvailableWhenPropertyChanged)
  }

  private def validCalculationInput(): CalculationInput =
    CalculationInput(userAnswers) match {
      case Right(calculationInput) => calculationInput
      case Left(error)             => fail(s"Expected valid CalculationInput but received: ${error.errorMessage}")
    }

  private def assertValid(expected: CalculationInput): Unit =
    CalculationInput(userAnswers) mustBe Right(expected)

  private def assertJson(expected: JsObject): Unit =
    Json.toJson(validCalculationInput()) mustBe expected

  private def assertInvalid(expected: InputValidationError): Unit =
    CalculationInput(userAnswers) mustBe Left(expected)

  "Calculation Input" when {

    "there is no property, value being transferred or downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(false),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(false),
        percentagePassedToDirectDescendants = Some(BigDecimal(0))
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, 0, 0, 0, None, None))
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 0,
            "percentagePassedToDirectDescendants" -> 0,
            "valueBeingTransferred"               -> 0
          )
        )
      }
    }

    "there is a property, none of which is closely inherited, and no value being transferred or downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.none),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(false),
        percentagePassedToDirectDescendants = Some(BigDecimal(0))
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(dateOfDeath, valueOfEstate, chargeableEstateValue, propertyValue, 0, 0, None, None)
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 0,
            "valueBeingTransferred"               -> 0
          )
        )
      }
    }

    "there is a property, all of which is closely inherited, and no exemptions, value being transferred or downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.all),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            0,
            None,
            None
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 0
          )
        )
      }
    }

    "there is a property, some of which is closely inherited, and no exemptions, value being transferred or downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            0,
            None,
            None
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 0
          )
        )
      }
    }

    "there is a property, some of which is closely inherited, some exemptions, and no value being transferred or downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(true),
        grossingUpOnEstateProperty = Some(false),
        chargeablePropertyValue = Some(chargeablePropertyValue),
        chargeableInheritedPropertyValue = Some(chargeableInheritedPropertyValue),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            0,
            Some(PropertyValueAfterExemption(chargeablePropertyValue, chargeableInheritedPropertyValue)),
            None
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 0,
            "propertyValueAfterExemption" -> Json.obj(
              "value"          -> 9,
              "inheritedValue" -> 10
            )
          )
        )
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, some value being transferred and no downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(true),
        valueBeingTransferred = Some(valueBeingTransferred),
        claimDownsizingThreshold = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            valueBeingTransferred,
            None,
            None
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 5
          )
        )
      }
    }

    "there is a property, some of which is closely inherited, no exemptions, no value being transferred and downsizing" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(true),
        datePropertyWasChanged = Some(datePropertyWasChanged),
        valueOfChangedProperty = Some(valueOfChangedProperty),
        assetsPassingToDirectDescendants = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            0,
            None,
            Some(DownsizingDetails(datePropertyWasChanged, valueOfChangedProperty, 0, 0))
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 0,
            "downsizingDetails" -> Json.obj(
              "datePropertyWasChanged"            -> "2018-02-02",
              "valueOfChangedProperty"            -> 6,
              "valueOfAssetsPassing"              -> 0,
              "valueAvailableWhenPropertyChanged" -> 0
            )
          )
        )
      }
    }

    "there is a property, downsizing, and other assets left to a direct descendant" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(false),
        claimDownsizingThreshold = Some(true),
        datePropertyWasChanged = Some(datePropertyWasChanged),
        valueOfChangedProperty = Some(valueOfChangedProperty),
        assetsPassingToDirectDescendants = Some(true),
        valueOfAssetsPassing = Some(valueOfAssetsPassing),
        transferAvailableWhenPropertyChanged = Some(false)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            0,
            None,
            Some(DownsizingDetails(datePropertyWasChanged, valueOfChangedProperty, valueOfAssetsPassing, 0))
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 0,
            "downsizingDetails" -> Json.obj(
              "datePropertyWasChanged"            -> "2018-02-02",
              "valueOfChangedProperty"            -> 6,
              "valueOfAssetsPassing"              -> 7,
              "valueAvailableWhenPropertyChanged" -> 0
            )
          )
        )
      }
    }

    "there is value being transferred, downsizing, and other assets left to a direct descendant" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(true),
        valueBeingTransferred = Some(valueBeingTransferred),
        claimDownsizingThreshold = Some(true),
        datePropertyWasChanged = Some(datePropertyWasChanged),
        valueOfChangedProperty = Some(valueOfChangedProperty),
        assetsPassingToDirectDescendants = Some(true),
        valueOfAssetsPassing = Some(valueOfAssetsPassing),
        transferAvailableWhenPropertyChanged = Some(true),
        valueAvailableWhenPropertyChanged = Some(valueAvailableWhenPropertyChanged)
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            valueBeingTransferred,
            None,
            Some(
              DownsizingDetails(
                datePropertyWasChanged,
                valueOfChangedProperty,
                valueOfAssetsPassing,
                valueAvailableWhenPropertyChanged
              )
            )
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 5,
            "downsizingDetails" -> Json.obj(
              "datePropertyWasChanged"            -> "2018-02-02",
              "valueOfChangedProperty"            -> 6,
              "valueOfAssetsPassing"              -> 7,
              "valueAvailableWhenPropertyChanged" -> 8
            )
          )
        )
      }
    }

    "downsizing is claimed but the date property was changed is before the eligibility date" must {

      def buildAnswers: OngoingStubbing[Option[Boolean]] = setupMock(
        dateOfDeath = Some(dateOfDeath),
        valueOfEstate = Some(valueOfEstate),
        chargeableEstateValue = Some(chargeableEstateValue),
        propertyInEstate = Some(true),
        propertyValue = Some(propertyValue),
        propertyPassingToDirectDescendants = Some(Constants.some),
        percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
        exemptionsAndReliefClaimed = Some(false),
        transferAnyUnusedThreshold = Some(true),
        valueBeingTransferred = Some(valueBeingTransferred),
        claimDownsizingThreshold = Some(true),
        datePropertyWasChanged = Some(Constants.downsizingEligibilityDate.minusDays(1))
      )

      "construct correctly from user answers" in {
        buildAnswers
        assertValid(
          CalculationInput(
            dateOfDeath,
            valueOfEstate,
            chargeableEstateValue,
            propertyValue,
            percentagePassedToDirectDescendants,
            valueBeingTransferred,
            None,
            None
          )
        )
      }

      "render to JSON" in {
        buildAnswers
        assertJson(
          Json.obj(
            "dateOfDeath"                         -> "2020-01-01",
            "valueOfEstate"                       -> 1,
            "chargeableEstateValue"               -> 2,
            "propertyValue"                       -> 3,
            "percentagePassedToDirectDescendants" -> 4,
            "valueBeingTransferred"               -> 5
          )
        )
      }
    }

    "there is no value for date of death" must {
      "return DateOfDeathNotDefined" in {
        setupMock()
        assertInvalid(DateOfDeathNotDefined)
      }
    }

    "there is no value for value of estate" must {
      "return ValueOfEstateNotDefined" in {
        setupMock(dateOfDeath = Some(dateOfDeath))
        assertInvalid(ValueOfEstateNotDefined)
      }
    }

    "there is no value for chargeable transfer amount" must {
      "return ChargeableEstateValueNotDefined" in {
        setupMock(dateOfDeath = Some(dateOfDeath), valueOfEstate = Some(valueOfEstate))
        assertInvalid(ChargeableEstateValueNotDefined)
      }
    }

    "there is no value for property in estate" must {
      "return PropertyInEstateNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue)
        )
        assertInvalid(PropertyInEstateNotDefined)
      }
    }

    "property in estate is true but there is no property value" must {
      "return PropertyValueNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true)
        )
        assertInvalid(PropertyValueNotDefined)
      }
    }

    "property in estate is true but there is no property passing to direct descendants" must {
      "return PropertyPassingToDirectDescendantsNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue)
        )
        assertInvalid(PropertyPassingToDirectDescendantsNotDefined)
      }
    }

    "property passing to direct descendants is some but no percentage is given" must {
      "return PercentagePassedToDirectDescendantsNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.some)
        )
        assertInvalid(PercentagePassedToDirectDescendantsNotDefined)
      }
    }

    "property passing to direct descendants is all but exemptions and relief claimed is missing" must {
      "return ExemptionsAndReliefClaimedNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.all),
          percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants)
        )
        assertInvalid(ExemptionsAndReliefClaimedNotDefined)
      }
    }

    "property passing to direct descendants is some but exemptions and relief claimed is missing" must {
      "return ExemptionsAndReliefClaimedNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.some),
          percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants)
        )
        assertInvalid(ExemptionsAndReliefClaimedNotDefined)
      }
    }

    "exemptions and relief are claimed but grossing up on estate property is missing" must {
      "return GrossingUpOnEstatePropertyNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.some),
          percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
          exemptionsAndReliefClaimed = Some(true)
        )
        assertInvalid(GrossingUpOnEstatePropertyNotDefined)
      }
    }

    "grossing up is false and chargeable property value is missing" must {
      "return ChargeablePropertyValueNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.some),
          percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
          exemptionsAndReliefClaimed = Some(true),
          grossingUpOnEstateProperty = Some(false)
        )
        assertInvalid(ChargeablePropertyValueNotDefined)
      }
    }

    "grossing up is false and chargeable inherited property value is missing" must {
      "return ChargeableInheritedPropertyValueNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.some),
          percentagePassedToDirectDescendants = Some(percentagePassedToDirectDescendants),
          exemptionsAndReliefClaimed = Some(true),
          grossingUpOnEstateProperty = Some(false),
          chargeablePropertyValue = Some(chargeablePropertyValue)
        )
        assertInvalid(ChargeableInheritedPropertyValueNotDefined)
      }
    }

    "transfer any unused allowance is missing" must {
      "return TransferAnyUnusedThresholdNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0))
        )
        assertInvalid(TransferAnyUnusedThresholdNotDefined)
      }
    }

    "transfer any unused allowance is true but value being transferred is missing" must {
      "return ValueBeingTransferredNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(true)
        )
        assertInvalid(ValueBeingTransferredNotDefined)
      }
    }

    "claim downsizing threshold is missing" must {
      "return ClaimDownsizingThresholdNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(false)
        )
        assertInvalid(ClaimDownsizingThresholdNotDefined)
      }
    }

    "claim downsizing threshold is true but date property was changed is missing" must {
      "return DatePropertyWasChangedNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(false),
          claimDownsizingThreshold = Some(true)
        )
        assertInvalid(DatePropertyWasChangedNotDefined)
      }
    }

    "claim downsizing threshold is true but value of changed property is missing" must {
      "return ValueOfChangedPropertyNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(false),
          claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged)
        )
        assertInvalid(ValueOfChangedPropertyNotDefined)
      }
    }

    "claim downsizing threshold is true but assets passing to direct descendants is missing" must {
      "return AssetsPassingToDirectDescendantsNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(false),
          claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged),
          valueOfChangedProperty = Some(valueOfChangedProperty)
        )
        assertInvalid(AssetsPassingToDirectDescendantsNotDefined)
      }
    }

    "assets pass to direct descendants but value of assets passing is missing" must {
      "return ValueOfAssetsPassingNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(false),
          claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged),
          valueOfChangedProperty = Some(valueOfChangedProperty),
          assetsPassingToDirectDescendants = Some(true)
        )
        assertInvalid(ValueOfAssetsPassingNotDefined)
      }
    }

    "value is transferred and transfer available when property changed is missing" must {
      "return TransferAvailableWhenPropertyChangedNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(true),
          valueBeingTransferred = Some(valueBeingTransferred),
          claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged),
          valueOfChangedProperty = Some(valueOfChangedProperty),
          assetsPassingToDirectDescendants = Some(true),
          valueOfAssetsPassing = Some(valueOfAssetsPassing)
        )
        assertInvalid(TransferAvailableWhenPropertyChangedNotDefined)
      }
    }

    "there is no value being transferred and transfer available when property changed is missing" must {
      "return a valid calculation input" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(false),
          claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged),
          valueOfChangedProperty = Some(valueOfChangedProperty),
          assetsPassingToDirectDescendants = Some(true),
          valueOfAssetsPassing = Some(valueOfAssetsPassing)
        )

        CalculationInput(userAnswers).isRight mustBe true
      }
    }

    "transfer available when property changed is true but its value is missing" must {
      "return ValueAvailableWhenPropertyChangedNotDefined" in {
        setupMock(
          dateOfDeath = Some(dateOfDeath),
          valueOfEstate = Some(valueOfEstate),
          chargeableEstateValue = Some(chargeableEstateValue),
          propertyInEstate = Some(true),
          propertyValue = Some(propertyValue),
          propertyPassingToDirectDescendants = Some(Constants.none),
          percentagePassedToDirectDescendants = Some(BigDecimal(0)),
          transferAnyUnusedThreshold = Some(true),
          valueBeingTransferred = Some(valueBeingTransferred),
          claimDownsizingThreshold = Some(true),
          datePropertyWasChanged = Some(datePropertyWasChanged),
          valueOfChangedProperty = Some(valueOfChangedProperty),
          assetsPassingToDirectDescendants = Some(true),
          valueOfAssetsPassing = Some(valueOfAssetsPassing),
          transferAvailableWhenPropertyChanged = Some(true)
        )
        assertInvalid(ValueAvailableWhenPropertyChangedNotDefined)
      }
    }
  }

}
