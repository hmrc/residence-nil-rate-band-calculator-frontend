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
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector => Auditing}
import uk.gov.hmrc.play.config.AppName
import uk.gov.hmrc.play.bootstrap.config.LoadAuditingConfig

@Singleton
class FrontendAuditConnector @Inject()(override val appNameConfiguration : Configuration,
                                       environment : Environment,
                                       val mode : Mode) extends Auditing with AppName {
  override lazy val auditingConfig = LoadAuditingConfig(appNameConfiguration, mode, s"auditing")
}
