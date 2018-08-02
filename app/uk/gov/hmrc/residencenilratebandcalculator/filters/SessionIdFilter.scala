/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.http.HeaderNames.COOKIE
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.SessionKeys

class SessionIdFilter @Inject()()(implicit val mat: Materializer) extends Filter {

  private def addSessionId(rh: RequestHeader, sessionId: (String, String)) = {
    val session = rh.session + sessionId
    val sessionAsCookie: Cookie = Session.encodeAsCookie(session)
    val cookies = Cookies.mergeCookieHeader(rh.headers.get(COOKIE).getOrElse(""), Seq(sessionAsCookie))
    val updatedHeaders = rh.headers.replace(COOKIE -> cookies)
    rh copy (headers = updatedHeaders)
  }

  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    if (rh.session.get(SessionKeys.sessionId).isEmpty) {
      val sessionId = SessionKeys.sessionId -> s"sessionId-${UUID.randomUUID.toString}"
      f(addSessionId(rh, sessionId)).map {
        result => {
          result.withSession(result.session(rh) + sessionId)
        }
      }
    } else {
      f(rh)
    }
  }
}
