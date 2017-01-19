package uk.gov.hmrc.residencenilratebandcalculator.connectors

import play.api.libs.json.Writes
import uk.gov.hmrc.http.cache.client.CacheMap
import scala.concurrent.Future

class AltSessionConnector {
  val funcMap: Map[String, CacheMap => CacheMap] = ???

  def update[A](key: String, value: A)(implicit write: Writes[A]): Future[Boolean] = ???
  def delete(key: String): Future[Boolean] = ???
  def associateFunc(key: String, fn: CacheMap => CacheMap): Boolean = ???
}
