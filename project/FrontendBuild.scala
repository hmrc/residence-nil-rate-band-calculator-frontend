import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object FrontendBuild extends Build with MicroService {

  val appName = "residence-nil-rate-band-calculator-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val frontendBootstrapVersion = "8.8.0"
  private val playPartialsVersion = "6.1.0"
  private val hmrcTestVersion = "2.4.0"
  private val scalaTestVersion = "2.2.6"
  private val pegdownVersion = "1.6.0"
  private val mockitoAllVersion = "1.10.19"
  private val whitelistVersion = "2.0.0"
  private val httpCachingClientVersion = "7.0.0"
  private val playJsonValidatorVersion = "0.8.6"
  private val playReactivemongoVersion = "5.1.0"
  private val pdfBoxVersion = "2.0.4"
  private val metricsGraphiteVersion = "3.0.2"
  private val playLanguageVersion = "3.0.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
    ws,
    "uk.gov.hmrc" %% "frontend-bootstrap" % frontendBootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-whitelist-filter" % whitelistVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
    "com.eclipsesource" %% "play-json-schema-validator" % playJsonValidatorVersion,
     "com.codahale.metrics" % "metrics-graphite" % metricsGraphiteVersion,
    "org.apache.pdfbox" % "pdfbox" % pdfBoxVersion,
    "uk.gov.hmrc" %% "play-language" % playLanguageVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "org.jsoup" % "jsoup" % "1.8.1" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-all" % mockitoAllVersion % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
