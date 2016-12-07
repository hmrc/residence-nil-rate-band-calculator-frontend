package uk.gov.hmrc.residencenilratebandcalculator.dispatch

import uk.gov.hmrc.residencenilratebandcalculator.controllers.routes._
import uk.gov.hmrc.http.cache.client.CacheMap

object HofRouter {
  private val routeMap: Map[String, CacheMap => String] =
    Map(
        "/" -> (_ => ChargeableTransferAmountController.onPageLoad.url),
        "/chargeable-transfer-amount" -> (_ => GrossEstateValueController.onPageLoad.url))

  def next(path: String): CacheMap => String = {
    routeMap.getOrElse(path, _ => ChargeableTransferAmountController.onPageLoad.url)
  }
}
