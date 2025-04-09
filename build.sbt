import sbt._
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}

lazy val appName                        = "residence-nil-rate-band-calculator-frontend"
lazy val appDependencies: Seq[ModuleID] = AppDependencies()
lazy val plugins: Seq[Plugins]          = Seq.empty
lazy val playSettings: Seq[Setting[_]]  = Seq.empty

ThisBuild / scalaVersion := "3.6.4"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins: _*)
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
    scalacOptions ++= Seq("-feature"),
    retrieveManaged := true,
    resolvers += Resolver.jcenterRepo,
    // only required for frontends
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    // for all services
    scalacOptions += "-Wconf:src=routes/.*:s"
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

PlayKeys.playDefaultPort := 7111
