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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import org.joda.time.LocalDate
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, Matchers}
import play.api.libs.json.{JsValue, Reads, Writes}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.residencenilratebandcalculator.connectors.SessionConnector
import uk.gov.hmrc.residencenilratebandcalculator.models.Date

import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.classTag

trait MockSessionConnector extends UnitSpec with MockitoSugar with Matchers with BeforeAndAfter {

  var mockSessionConnector: SessionConnector = _
  var mockCacheMap: CacheMap = _
  implicit val headnapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
  implicit val writesnapper = ArgumentCaptor.forClass(classOf[Writes[Any]])
  implicit val dateWritesNapper = ArgumentCaptor.forClass(classOf[Writes[LocalDate]])

  before {
    mockCacheMap = mock[CacheMap]
    when(mockCacheMap.data) thenReturn Map[String, JsValue]()
    when(mockCacheMap.getEntry[Boolean](anyString())(ArgumentCaptor.forClass(classOf[Reads[Boolean]]).capture())) thenReturn None
    when(mockCacheMap.getEntry[Int](anyString())(ArgumentCaptor.forClass(classOf[Reads[Int]]).capture())) thenReturn None
    when(mockCacheMap.getEntry[Date](anyString())(ArgumentCaptor.forClass(classOf[Reads[Date]]).capture())) thenReturn None

    mockSessionConnector = mock[SessionConnector]
    when(mockSessionConnector.cache(anyString(), anyInt())(any(), any[HeaderCarrier])) thenReturn Future.successful(mockCacheMap)
    when(mockSessionConnector.fetchAndGetEntry[Int](anyString())(any[HeaderCarrier], any())) thenReturn Future.successful(None)

    when(mockSessionConnector.cache(anyString(), anyBoolean())(any(), any[HeaderCarrier])) thenReturn Future.successful(mockCacheMap)
    when(mockSessionConnector.fetchAndGetEntry[Boolean](anyString())(any[HeaderCarrier], any())) thenReturn Future.successful(None)

    when(mockSessionConnector.cache(anyString(), any[LocalDate]())(any(), any[HeaderCarrier])) thenReturn Future.successful(mockCacheMap)
    when(mockSessionConnector.fetchAndGetEntry[LocalDate](anyString())(any[HeaderCarrier], any())) thenReturn Future.successful(None)

    when(mockSessionConnector.fetch()(any[HeaderCarrier])) thenReturn Future.successful(Some(mockCacheMap))
  }

  def verifyValueIsCached[A: ClassTag](key: String, value: A) = {
    implicit val hc = new HeaderCarrier()
    val valueCaptor = ArgumentCaptor.forClass(classTag[A].runtimeClass)
    verify(mockSessionConnector).cache(matches(key), valueCaptor.capture)(writesnapper.capture, headnapper.capture)
    valueCaptor.getValue shouldBe value
  }

  def verifyValueIsNotCached() = verify(mockSessionConnector, never()).cache(anyString(), any())(any(), any[HeaderCarrier])

  def setCacheValue[A](key: String, value: A) = {
    when(mockSessionConnector.fetchAndGetEntry[A](matches(key))(any[HeaderCarrier], any())) thenReturn Future.successful(Some(value))
    when(mockCacheMap.getEntry[A](matches(key))(any())) thenReturn Some(value)
  }

  def setCacheMap(cacheMap: CacheMap) = {
    when(mockSessionConnector.fetch()(any[HeaderCarrier])) thenReturn Future.successful(Some(cacheMap))
  }

  def expireSessionConnector() = {
    when(mockSessionConnector.fetch()(any[HeaderCarrier])) thenReturn Future.successful(None)
  }
}
