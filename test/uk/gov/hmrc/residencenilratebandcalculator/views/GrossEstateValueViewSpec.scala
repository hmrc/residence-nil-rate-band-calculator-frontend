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

package uk.gov.hmrc.residencenilratebandcalculator.views

import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes
import uk.gov.hmrc.residencenilratebandcalculator.views.html.gross_estate_value

import scala.language.reflectiveCalls

class GrossEstateValueViewSpec extends RnrbViewBehaviour with InputViewBehaviour {

  def viewAsDocument = asDocument(gross_estate_value(frontendAppConfig)(request, messages))

  "Gross Estate Value View" must {

    behave like rnrbView(
      viewAsDocument,
      "gross_estate_value.browser_title",
      "gross_estate_value.title",
      "gross_estate_value.guidance"
    )

    behave like inputView(viewAsDocument, routes.GrossEstateValueController.onSubmit().url)
  }
}
