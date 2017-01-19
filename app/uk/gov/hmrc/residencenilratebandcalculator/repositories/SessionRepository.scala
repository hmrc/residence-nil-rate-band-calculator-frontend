package uk.gov.hmrc.residencenilratebandcalculator.repositories

import uk.gov.hmrc.http.cache.client.CacheMap
import scala.concurrent.Future

trait SessionRepository {
  def upsert(sessionId: String, body: CacheMap): Future[Boolean]
  def get(sessionId: String): Future[Option[CacheMap]]
}

object SessionRepository {
  def apply = ???
}