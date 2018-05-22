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

package uk.gov.hmrc.residencenilratebandcalculator.controllers

import javax.inject.{Inject, _}
import play.api.Configuration
import play.api.Mode.Mode
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call}
import uk.gov.hmrc.play.config.RunMode
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.residencenilratebandcalculator.FrontendAppConfig
import play.api.mvc._

@Singleton
class CustomLanguageController @Inject()(val appConfig: FrontendAppConfig,
                                         implicit val messagesApi: MessagesApi,
                                         override val mode : Mode,
                                         override val runModeConfiguration : Configuration)
extends Controller with RunMode with I18nSupport {

  val englishLang = Lang("en")

  def langToCall(lang: String): Call = {
    if(appConfig.isWelshEnabled) {
      uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.CustomLanguageController.switchToLanguage(lang)
    } else {
      uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.CustomLanguageController.switchToLanguage("english")
    }
  }

  def switchToLanguage(language: String): Action[AnyContent] =  Action { implicit request =>
    val lang =
      if(appConfig.isWelshEnabled) {
        languageMap.getOrElse(language, LanguageUtils.getCurrentLang)
      } else {
        englishLang
      }
    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)

    Redirect(redirectURL).withLang(Lang.apply(lang.code)).flashing(LanguageUtils.FlashWithSwitchIndicator)
  }

  protected def fallbackURL: String = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.CalculateThresholdIncreaseController.onPageLoad().url

  def languageMap: Map[String, Lang] = Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))

}