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

package uk.gov.hmrc.residencenilratebandcalculator.filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Configuration
import play.api.mvc.{EssentialAction, EssentialFilter}
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAuditConnector
import uk.gov.hmrc.play.frontend.filters.DeviceIdCookieFilter

class DeviceId @Inject()(config: Configuration, frontendAuditConnector: FrontendAuditConnector)(implicit val mat: Materializer)
  extends EssentialFilter {
  lazy val appName = config.getString("appName").getOrElse("APP NAME NOT SET")
  val filter = DeviceIdCookieFilter(appName, frontendAuditConnector)
  override def apply(next: EssentialAction): EssentialAction = filter(next)
}
