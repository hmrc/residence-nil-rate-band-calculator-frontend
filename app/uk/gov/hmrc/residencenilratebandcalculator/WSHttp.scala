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

package uk.gov.hmrc.residencenilratebandcalculator

import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Mode}
import play.api.Mode.Mode
import uk.gov.hmrc.http.{HttpDelete, HttpGet, HttpPost, HttpPut}
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.config.{AppName, RunMode}
import uk.gov.hmrc.play.http.ws.{WSDelete, WSGet, WSPost, WSPut}

@Singleton
class WSHttp @Inject()(override val auditConnector: FrontendAuditConnector,
                       override val appNameConfiguration : Configuration,
                       override val runModeConfiguration: Configuration) extends WSGet with HttpGet
  with WSPut with HttpPut
  with WSPost with HttpPost
  with WSDelete with HttpDelete with AppName with RunMode with HttpAuditing {
  override protected def mode: Mode = env match {
    case "dev" => Mode.Dev
    case "test" => Mode.Test
    case "prod" => Mode.Prod
  }
  override val hooks = Seq(AuditingHook)
}
