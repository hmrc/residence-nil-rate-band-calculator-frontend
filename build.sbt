import AppDependencies.integrationTestDependencies
import sbt.*
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, itSettings, scalaSettings}

lazy val appName                        = "residence-nil-rate-band-calculator-frontend"
lazy val appDependencies: Seq[ModuleID] = AppDependencies()
lazy val plugins: Seq[Plugins]          = Seq.empty
lazy val playSettings: Seq[Setting[_]]  = Seq.empty
val silencerVersion                     = "1.7.0"

ThisBuild / scalaVersion := "3.6.4"

lazy val IntegrationTest = config("it").extend(Test)

lazy val microservice = Project(appName, file("."))
  .enablePlugins((Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins) *)
  .disablePlugins(JUnitXmlReportPlugin) // this is an experimental plugin that is (currently) enabled by default and prevents deployment to QA environment
  .settings(playSettings: _*)
  .settings(
    ScoverageKeys.coverageExcludedFiles :=
      "<empty>;Reverse.*;.*AuthService.*;.*CustomLanguageController.*;models/.data/..*;.*filters.*;.*handlers.*;.*components.*;"
        + ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*SessionConnector;.*frontendAppConfig;"
        + ".*ControllerConfiguration;.*RnrbConnector.*;.*StyleGuide;.*main_template.*;.*basic_template.*;.*views.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum    := false,
    ScoverageKeys.coverageHighlighting     := true,
    Test / parallelExecution               := false
  )
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(libraryDependencies ++= appDependencies)
  .settings(
    Test / Keys.fork := true,
    scalacOptions ++= Seq("-feature", "-source:3.4-migration", "-rewrite"),
    retrieveManaged := true,
    // only required for frontends
    scalacOptions := Seq(
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-release",
      "11",
      "-feature",
      "-source:3.4-migration",
      "-rewrite",
      "-Wunused:all",
      "-Wconf:msg=unused import&src=html/.*:s",
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-Wconf:msg=.*-Wunused.*:s"
    )
  )
  .settings(majorVersion := 0)
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "uk.gov.hmrc.govukfrontend.views.html.components.implicits._"
    )
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .configs(IntegrationTest)
  .dependsOn(microservice)
  .settings(itSettings(): _*)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= Seq(
      "com.github.tomakehurst" % "wiremock-jre8" % "2.35.0",
      "org.scalatest"         %% "scalatest"     % "3.2.17",
      "com.vladsch.flexmark"   % "flexmark-all"  % "0.64.0"
    )
  )

PlayKeys.playDefaultPort := 7111
