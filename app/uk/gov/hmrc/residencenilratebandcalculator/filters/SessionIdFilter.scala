/*
 * Copyright 2019 HM Revenue & Customs
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

import java.util.UUID

import javax.inject.Inject
import akka.stream.Materializer
import play.api.http.DefaultHttpFilters
import play.api.mvc._
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.http.{HeaderNames => HMRCHeaderNames}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.bootstrap.filters.FrontendFilters


class Filters @Inject()(
                         frontendFilters: FrontendFilters,
                         sessionIdFilters: SessionIdFilter) extends DefaultHttpFilters(frontendFilters.filters :+ sessionIdFilters: _*)

class SessionIdFilter (
                        override val mat: Materializer,
                        uuid: => UUID,
                        implicit val ec: ExecutionContext
                      ) extends Filter {

  @Inject
  def this(mat: Materializer, ec: ExecutionContext) {
    this(mat, UUID.randomUUID(), ec)
  }

  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {

    lazy val sessionId: String = s"session-$uuid"

    if (rh.session.get(SessionKeys.sessionId).isEmpty) {

      val cookies: String = {

        val session: Session =
          rh.session + (SessionKeys.sessionId -> sessionId)

        val cookies =
          rh.cookies ++ Seq(Session.encodeAsCookie(session))

        Cookies.encodeCookieHeader(cookies.toSeq)
      }

      val headers = rh.headers.add(
        HMRCHeaderNames.xSessionId -> sessionId,
        HeaderNames.COOKIE -> cookies
      )

      f(rh.copy(headers = headers)).map {
        result =>

          val cookies =
            Cookies.fromSetCookieHeader(result.header.headers.get(HeaderNames.SET_COOKIE))

          val session = Session.decodeFromCookie(cookies.get(Session.COOKIE_NAME)).data
            .foldLeft(rh.session) {
              case (m, n) =>
                m + n
            }

          result.withSession(session + (SessionKeys.sessionId -> sessionId))
      }
    } else {
      f(rh)
    }
  }
}
