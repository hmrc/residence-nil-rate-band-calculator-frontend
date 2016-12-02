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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.mockito.ArgumentCaptor
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, Matchers}
import uk.gov.hmrc.play.test.UnitSpec

trait MockSessionConnector extends UnitSpec with MockitoSugar with Matchers with BeforeAndAfter {

  var mockSessionConnector = mock[SessionConnector]

  before {
    mockSessionConnector = mock[SessionConnector]
  }

  def verifyValueIsCached(value: Int) = {
    val captor = ArgumentCaptor.forClass(classOf[Int])
    verify(mockSessionConnector).storeValue(captor.capture)
    captor.getValue shouldBe value
  }
}
