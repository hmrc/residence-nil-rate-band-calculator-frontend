import sbt._
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings


lazy val appName = "residence-nil-rate-band-calculator-frontend"
lazy val appDependencies: Seq[ModuleID] = AppDependencies()
lazy val plugins : Seq[Plugins] = Seq.empty
lazy val playSettings : Seq[Setting[_]] = Seq.empty
lazy val scalaversion = "2.12.12"
val silencerVersion = "1.7.1"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins : _*)
  .disablePlugins(JUnitXmlReportPlugin) // this is an experimental plugin that is (currently) enabled by default and prevents deployment to QA environment
  .settings(playSettings : _*)
  .settings(
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*AuthService.*;.*CustomLanguageController.*;models/.data/..*;.*filters.*;.*handlers.*;.*components.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*SessionConnector;.*frontendAppConfig;" +
      ".*ControllerConfiguration;.*RnrbConnector.*;.*StyleGuide;.*main_template.*;.*basic_template.*;.*views.*;",
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
  .settings(scalaSettings: _*)
  .settings(scalaVersion:=scalaversion)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(libraryDependencies ++= appDependencies)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(integrationTestSettings())
  .settings(
    scalacOptions ++= Seq("-feature"),
    dependencyOverrides += "commons-codec" % "commons-codec" % "1.12",
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers += Resolver.jcenterRepo,
    // ***************
    // Use the silencer plugin to suppress warnings from unused imports in compiled twirl templates
    scalacOptions += "-P:silencer:pathFilters=views;routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
  )
  .settings(
    // concatenate js
    Concat.groups := Seq(
      "javascripts/rnrb-app.js" -> group(Seq("javascripts/timeout-dialog.js", "javascripts/show-hide-content.js", "javascripts/rnrb.js"))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat, uglify, digest),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("rnrb-*.js")
  )
  .settings(majorVersion := 0)
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.govukfrontend.views.html.helpers._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "uk.gov.hmrc.govukfrontend.views.html.components.implicits._"
    )
  )
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.play.views.html.helpers._",
      "uk.gov.hmrc.play.views.html.layouts._"
    )
  )
  .disablePlugins(JUnitXmlReportPlugin)


