import sbt._
import play.sbt.PlayImport._

private object AppDependencies {

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "simple-reactivemongo" % "7.26.0-play-26",
    "uk.gov.hmrc"       %% "govuk-template"       % "5.48.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"              % "8.10.0-play-26",
    "com.typesafe.play" %% "play-json-joda"       % "2.8.1",
    "uk.gov.hmrc"       %% "bootstrap-play-26"    % "1.8.0" ,
    "uk.gov.hmrc"       %% "http-caching-client"  % "9.0.0-play-26",
    "org.apache.pdfbox" % "pdfbox"                % "2.0.18",
    "uk.gov.hmrc"       %% "play-language"        % "4.2.0-play-26",
    "uk.gov.hmrc"       %% "http-verbs"           % "10.5.0-play-26",
    nettyServer
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc"             %% "hmrctest"           % "3.9.0-play-26"       % scope,
        "org.scalatest"           %% "scalatest"          % "3.0.8"               % scope,
        "org.pegdown"             % "pegdown"             % "1.6.0"               % scope,
        "org.jsoup"               % "jsoup"               % "1.13.1"              % scope,
        "org.mockito"             % "mockito-core"        % "3.2.4"               % scope,
        "org.scalatestplus.play"  %% "scalatestplus-play" % "3.1.3"               % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
