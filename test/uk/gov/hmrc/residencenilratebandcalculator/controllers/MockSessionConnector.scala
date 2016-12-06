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
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, Matchers}
import play.api.libs.json.Writes
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector

import scala.concurrent.Future

trait MockSessionConnector extends UnitSpec with MockitoSugar with Matchers with BeforeAndAfter {

  var mockSessionConnector: SessionConnector = null
  implicit val headnapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
  implicit val writesnapper = ArgumentCaptor.forClass(classOf[Writes[Int]])

  before {
    mockSessionConnector = mock[SessionConnector]
    when(mockSessionConnector.cache(anyString(), anyInt())(any(), any[HeaderCarrier])) thenReturn Future.successful(mock[CacheMap])
    when(mockSessionConnector.fetchAndGetEntry[Int](anyString())(any[HeaderCarrier], any())) thenReturn Future.successful(None)
  }

  def verifyValueIsCached(value: Int) = {
    implicit val hc = new HeaderCarrier()
    val valueCaptor = ArgumentCaptor.forClass(classOf[Int])
    val keyCaptor = ArgumentCaptor.forClass(classOf[String])
    verify(mockSessionConnector).cache(keyCaptor.capture, valueCaptor.capture)(writesnapper.capture, headnapper.capture)
    valueCaptor.getValue shouldBe value
  }

  def verifyValueIsNotCached() = verifyZeroInteractions(mockSessionConnector)

  def setCacheValue(key: String, value: Int) =
    when(mockSessionConnector.fetchAndGetEntry[Int](matches(key))(any[HeaderCarrier], any())) thenReturn Future.successful(Some(value))
}
