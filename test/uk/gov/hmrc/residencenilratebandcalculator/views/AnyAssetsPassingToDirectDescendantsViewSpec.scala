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
import uk.gov.hmrc.residencenilratebandcalculator.views.html.any_assets_passing_to_direct_descendants
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

import scala.language.reflectiveCalls

class AnyAssetsPassingToDirectDescendantsViewSpec extends BooleanViewSpecBase {

  val messageKeyPrefix = "any_assets_passing_to_direct_descendants"

  val formattedPropertyValue = "£123,456.78"

  def createView(form: Option[Form[Boolean]] = None) =
    any_assets_passing_to_direct_descendants(frontendAppConfig, backUrl, form, Seq(), None)(request, messages)

  "Any Assets Passing to Direct Descendants View" must {

    behave like rnrbPage[Boolean](createView, messageKeyPrefix)

    behave like pageWithBackLink[Boolean](createView)

    behave like booleanPage(createView, messageKeyPrefix, AnyAssetsPassingToDirectDescendantsController.onSubmit().url)

    behave like pageContainingPreviousAnswers(createView)
  }

  "Any Assets Passing to Direct Descendants View" when {

    "there is a property in the estate" must {
      "contain guidance that includes the property value" in {
        val doc = asDocument(any_assets_passing_to_direct_descendants(frontendAppConfig, backUrl, None, Seq(), Some(formattedPropertyValue))(request, messages))
        assertContainsText(doc, messages("any_assets_passing_to_direct_descendants.guidance", formattedPropertyValue))
      }
    }

    "there is no property in the estate" must {
      "contain guidance that does not include the property value" in {
        val doc = asDocument(createView(None))
        assertContainsText(doc, messages("any_assets_passing_to_direct_descendants.guidance_no_property"))
      }
    }
  }
}
