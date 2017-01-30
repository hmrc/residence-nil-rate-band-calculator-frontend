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

package uk.gov.hmrc.residencenilratebandcalculator.views

import play.api.data.Form
import uk.gov.hmrc.residencenilratebandcalculator.views.html.purpose_of_use

import scala.language.reflectiveCalls

class PurposeOfUseViewSpec extends ViewSpecBase {

  val messageKeyPrefix = "purpose_of_use"

  def createView(form: Option[Form[String]] = None) = purpose_of_use(frontendAppConfig, backUrl, form)(request, messages)

  "Purpose of Use View" must {

    behave like rnrbPage[String](createView, messageKeyPrefix)

  }
}
