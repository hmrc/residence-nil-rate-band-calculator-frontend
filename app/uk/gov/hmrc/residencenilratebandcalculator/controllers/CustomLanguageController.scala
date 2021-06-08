/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, Lang, Messages}
import play.api.mvc.{Action, AnyContent, Call, _}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.residencenilratebandcalculator.AppConfig

@Singleton
class CustomLanguageController @Inject()(val cc: MessagesControllerComponents,
                                         val appConfig: AppConfig) extends FrontendController(cc) with I18nSupport {

  def langToCall(lang: String): Call = {
    if(appConfig.isWelshEnabled) {
      uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.CustomLanguageController.switchToLanguage(lang)
    } else {
      uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.CustomLanguageController.switchToLanguage("english")
    }
  }

  def loadedLanguageMessages(implicit request: Request[_]): Messages = {
    cc.messagesApi.preferred(request)
  }

  def switchToLanguage(language: String): Action[AnyContent] = Action { implicit request =>
    val lang =
      if(appConfig.isWelshEnabled) {
        CustomLanguageController.languageMap.getOrElse(language, Lang.defaultLang)
      } else {
        CustomLanguageController.englishLang
      }
    val redirectURL = request.headers.get(REFERER).getOrElse(CustomLanguageController.fallbackURL)

    Redirect(redirectURL).withLang(Lang.apply(lang.code)).flashing(Flash(Map("switching-language" -> "true")))
  }
}

object CustomLanguageController {
  def languageMap: Map[String, Lang] = Map("english" -> Lang("en"), "cymraeg" -> Lang("cy"))

  lazy val englishLang = Lang("en")

  protected def fallbackURL: String = uk.gov.hmrc.residencenilratebandcalculator.controllers.routes.CalculateThresholdIncreaseController.onPageLoad().url
}