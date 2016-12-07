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

package uk.gov.hmrc.residencenilratebandcalculator

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._

@Singleton
class Navigator @Inject()() {
  private val routeMap: Map[String, CacheMap => Call] =
    Map(
      ChargeableTransferAmountController.onSubmit().url -> (_ => ChargeableTransferAmountController.onPageLoad()),
      GrossEstateValueController.onSubmit().url -> (_ => GrossEstateValueController.onPageLoad()),
      HomeController.onPageLoad().url -> (_ => HomeController.onPageLoad()))


  def next(path: String): CacheMap => Call = {
    routeMap.getOrElse(path, _ => PageNotFoundController.onPageLoad())
  }
}
