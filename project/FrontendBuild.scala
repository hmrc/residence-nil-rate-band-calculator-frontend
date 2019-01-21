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

  private val bootstrapVersion = "4.7.0"
  private val playPartialsVersion = "6.3.0"
  private val hmrcTestVersion = "3.4.0-play-25"
  private val scalaTestVersion = "3.0.0"
  private val pegdownVersion = "1.6.0"
  private val mockitoCoreVersion = "2.13.0"
  private val whitelistVersion = "2.0.0"
  private val httpCachingClientVersion = "8.0.0"
  private val playReactivemongoVersion = "6.2.0"
  private val pdfBoxVersion = "2.0.4"
  private val playLanguageVersion = "3.4.0"
  private val scalatestplus = "2.0.1"

  val compile = Seq(
    "uk.gov.hmrc" %% "play-reactivemongo" % playReactivemongoVersion,
    ws,
    "uk.gov.hmrc" %% "govuk-template" % "5.27.0-play-25",
    "uk.gov.hmrc" %% "play-ui" % "7.27.0-play-25",
    "uk.gov.hmrc" %% "bootstrap-play-25" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-partials" % playPartialsVersion,
    "uk.gov.hmrc" %% "play-whitelist-filter" % whitelistVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
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
        "org.jsoup" % "jsoup" % "1.10.2" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % mockitoCoreVersion % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
