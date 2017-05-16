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

package uk.gov.hmrc.residencenilratebandcalculator

import com.google.inject.Provider
import org.mockito.Mockito._
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.Application
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class BaseSpec extends UnitSpec with MockitoSugar with Matchers with WithFakeApplication {
  def applicationProvider: Provider[Application] = {
    val mockedProviderApplication: Provider[Application] = mock[Provider[Application]]
    when(mockedProviderApplication.get) thenReturn fakeApplication
    mockedProviderApplication
  }
}