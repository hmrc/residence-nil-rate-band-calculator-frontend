import sbt._

object FrontendBuild extends Build with MicroService {

  val appName = "residence-nil-rate-band-calculator-frontend"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
}

private object AppDependencies {
  import play.sbt.PlayImport._

  private val bootstrapVersion = "1.3.0"
  private val hmrcTestVersion = "3.9.0-play-26"
  private val jsonJodaVersion = "2.7.4"
  private val scalaTestVersion = "3.0.8"
  private val pegdownVersion = "1.6.0"
  private val mockitoCoreVersion = "3.2.4"
  private val httpCachingClientVersion = "9.0.0-play-26"
  private val playReactivemongoVersion = "7.22.0-play-26"
  private val pdfBoxVersion = "2.0.18"
  private val playLanguageVersion = "3.4.0"
  private val scalatestplus = "2.0.1"

  val compile = Seq(
    "uk.gov.hmrc" %% "simple-reactivemongo" % playReactivemongoVersion,
    ws,
    "uk.gov.hmrc" %% "govuk-template" % "5.48.0-play-26",
    "uk.gov.hmrc" %% "play-ui" % "8.6.0-play-26",
    "com.typesafe.play" %% "play-json-joda" % jsonJodaVersion,
    "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapVersion,
    "uk.gov.hmrc" %% "http-caching-client" % httpCachingClientVersion,
    "org.apache.pdfbox" % "pdfbox" % pdfBoxVersion,
    "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
    "uk.gov.hmrc" %% "http-verbs" % "10.2.0-play-27",
    nettyServer
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
        "org.jsoup" % "jsoup" % "1.12.1" % scope,
        "org.mockito" % "mockito-core" % mockitoCoreVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
