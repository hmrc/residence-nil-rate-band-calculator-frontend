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

package uk.gov.hmrc.residencenilratebandcalculator.filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc.EssentialFilter
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAuditConnector
import uk.gov.hmrc.residencenilratebandcalculator.controllers.ControllerConfiguration
import uk.gov.hmrc.play.audit.filters.FrontendAuditFilter
import uk.gov.hmrc.play.config.{AppName, RunMode}

class Audit @Inject()(controllerConfiguration: ControllerConfiguration, frontendAuditConnector: FrontendAuditConnector)
                     (implicit val mat: Materializer)
  extends EssentialFilter with FrontendAuditFilter with RunMode with AppName {

  override lazy val maskedFormFields = Seq("password")

  override lazy val applicationPort = None

  override lazy val auditConnector = frontendAuditConnector

  override def controllerNeedsAuditing(controllerName: String) = controllerConfiguration.paramsForController(controllerName).needsAuditing
}
