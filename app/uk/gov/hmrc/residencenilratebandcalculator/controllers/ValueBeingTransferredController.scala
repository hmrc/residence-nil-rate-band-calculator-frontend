/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import java.text.NumberFormat
import java.util.Locale
import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.data.{Form, FormError}
import play.api.i18n.Messages
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, AnyContent, DefaultMessagesControllerComponents}
import uk.gov.hmrc.residencenilratebandcalculator.models.CacheMap
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.connectors.{RnrbConnector, SessionConnector}
import uk.gov.hmrc.residencenilratebandcalculator.controllers.predicates.ValidatedSession
import uk.gov.hmrc.residencenilratebandcalculator.exceptions.NoCacheMapException
import uk.gov.hmrc.residencenilratebandcalculator.forms.NonNegativeIntForm
import uk.gov.hmrc.residencenilratebandcalculator.models.UserAnswers
import uk.gov.hmrc.residencenilratebandcalculator.utils.TaxYear
import uk.gov.hmrc.residencenilratebandcalculator.views.html.value_being_transferred
import uk.gov.hmrc.residencenilratebandcalculator.{Constants, Navigator}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValueBeingTransferredController @Inject() (
    cc: DefaultMessagesControllerComponents,
    val sessionConnector: SessionConnector,
    val navigator: Navigator,
    val rnrbConnector: RnrbConnector,
    validatedSession: ValidatedSession,
    valueBeingTransferredView: value_being_transferred
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with Logging {

  val controllerId: String = Constants.valueBeingTransferredId

  def form: () => Form[Int] = () =>
    NonNegativeIntForm(
      "value_being_transferred.error.blank",
      "error.whole_pounds",
      "error.non_numeric",
      "error.value_too_large"
    )

  private def getCacheMap(implicit hc: HeaderCarrier): Future[CacheMap] = sessionConnector.fetch().map {
    case Some(cacheMap) => cacheMap
    case None           => throw new NoCacheMapException("CacheMap not available")
  }

  def microserviceValues(implicit hc: HeaderCarrier): Future[(HttpResponse, CacheMap)] = for {
    dateOfDeath      <- getCacheMap.map(_.data(Constants.dateOfDeathId).toString().replaceAll("\"", ""))
    nilRateValueJson <- rnrbConnector.getNilRateBand(dateOfDeath)
    cacheMap         <- getCacheMap
  } yield (nilRateValueJson, cacheMap)

  private def formatJsonNumber(numberStr: String): String = {
    val number = Integer.parseInt(numberStr)
    NumberFormat.getCurrencyInstance(Locale.UK).format(number)
  }

  def onPageLoad(implicit rds: Reads[Int]): Action[AnyContent] = Action.async { implicit request =>
    microserviceValues
      .map { case (nilRateValueJson, cacheMap) =>

        val nilRateBand                 = formatJsonNumber(nilRateValueJson.json.toString())
        implicit val messages: Messages = messagesApi.preferred(request)
        Ok(
          valueBeingTransferredView(
            nilRateBand,
            cacheMap.getEntry(controllerId).fold(form())(value => form().fill(value))
          )
        )
      }
      .recover {
        case n: NoCacheMapException =>
          Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
        case r: RuntimeException =>
          logger.error(r.getMessage, r)
          throw r
      }
  }

  def onSubmit(implicit wts: Writes[Int]): Action[AnyContent] = validatedSession.async { implicit request =>
    microserviceValues
      .flatMap { case (nilRateValueJson, cacheMap) =>
        val boundForm                   = form().bindFromRequest()
        val nilRateBand                 = nilRateValueJson.json.toString()
        val formattedNilRateBand        = formatJsonNumber(nilRateBand)
        val userAnswers                 = new UserAnswers(cacheMap)
        implicit val messages: Messages = messagesApi.preferred(request)
        boundForm.fold(
          formWithErrors =>
            Future.successful(BadRequest(valueBeingTransferredView(formattedNilRateBand, formWithErrors))),
          value =>
            validate(value, nilRateBand, userAnswers).flatMap {
              case Some(error) =>
                Future.successful(
                  BadRequest(valueBeingTransferredView(formattedNilRateBand, form().fill(value).withError(error)))
                )
              case None =>
                sessionConnector
                  .cache[Int](controllerId, value)
                  .map(cacheMap => Redirect(navigator.nextPage(controllerId)(new UserAnswers(cacheMap))))
            }
        )
      }
      .recover {
        case n: NoCacheMapException =>
          Redirect(uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.SessionExpiredController.onPageLoad)
        case r: RuntimeException =>
          logger.error(r.getMessage, r)
          throw r
      }
  }

  def validate(value: Int, nilRateBandStr: String, userAnswers: UserAnswers): Future[Option[FormError]] = {
    val nrb =
      try
        Integer.parseInt(nilRateBandStr)
      catch {
        case e: NumberFormatException =>
          logger.error(e.getMessage, e)
          throw new NumberFormatException("Bad value in nil rate band")
      }

    if (value <= nrb) {
      Future.successful(None)
    } else {
      val dateOfDeath = userAnswers.dateOfDeath.getOrElse(throw new RuntimeException("Date of death was not answered"))
      val taxYear     = TaxYear.taxYearFor(dateOfDeath)
      Future.successful(
        Some(
          FormError(
            "value",
            "value_being_transferred.error",
            Seq(nrb, s"${taxYear.startYear}", s"${taxYear.finishYear}")
          )
        )
      )
    }
  }

}
