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

import uk.gov.hmrc.residencenilratebandcalculator.views.html.chargeable_transfer_amount
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes

import scala.language.reflectiveCalls

class ChargeableTransferAmountViewSpec extends HtmlSpec {

  def fixture() = new {
    val view = chargeable_transfer_amount(frontendAppConfig)(request, messages)
    val doc = asDocument(view)
  }

  "Chargeable Transfer Amount View" must {

    "display the correct browser title" in {
      val f = fixture()
      assertEqualsMessage(f.doc, "title", "chargeable_transfer_amount.browser_title")
    }

    "display the correct page title" in {
      val f = fixture()
      assertPageTitleEqualsMessage(f.doc, "chargeable_transfer_amount.title")
    }

    "display the correct guidance" in {
      val f = fixture()
      assertContainsMessages(f.doc, "chargeable_transfer_amount.guidance")
    }

    "contain a form that POSTs to the correct action" in {
      val f = fixture()
      val forms = f.doc.getElementsByTag("form")
      forms.size shouldBe 1
      val form = forms.first
      form.attr("method") shouldBe "POST"
      form.attr("action") shouldBe routes.ChargeableTransferAmountController.onSubmit().url
    }

    "contain an input for the value" in {
      val f = fixture()
      assertRenderedById(f.doc, "value")
    }

    "contain a submit button" in {
      val f = fixture()
      assertRenderedByCssSelector(f.doc, "input[type=submit]")
    }
  }
}
