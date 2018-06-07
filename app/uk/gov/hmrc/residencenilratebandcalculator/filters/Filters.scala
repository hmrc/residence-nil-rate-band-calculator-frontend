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

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import uk.gov.hmrc.play.bootstrap.filters._
import uk.gov.hmrc.play.bootstrap.filters.frontend.CSRFExceptionsFilter

class Filters @Inject()(frontendFilters: FrontendFilters,
                        csrfExceptions: CSRFExceptionsFilter, // TODO is this being used?
                        sessionId: SessionId,
                        recovery: Recovery) // TODO not available in bootstrap-25 - is this required any more (see what happens on 404 with/without this)?
  extends DefaultHttpFilters(frontendFilters.filters ++ Seq(csrfExceptions, sessionId, recovery): _*)

class FiltersWithWhitelist @Inject()(frontendFilters: FrontendFilters,
                                     csrfExceptions: CSRFExceptionsFilter,
                                     sessionId: SessionId,
                                     recovery: Recovery,
                                     whitelist: Whitelist)
  extends DefaultHttpFilters(frontendFilters.filters ++ Seq(csrfExceptions, sessionId, recovery, whitelist): _*)
