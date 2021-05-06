import sbt._
import play.sbt.PlayImport._

private object AppDependencies {

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "simple-reactivemongo"       % "7.31.0-play-27",
    "uk.gov.hmrc"       %% "govuk-template"             % "5.63.0-play-27",
    "uk.gov.hmrc"       %% "play-ui"                    % "8.21.0-play-27",
    "com.typesafe.play" %% "play-json-joda"             % "2.9.2",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27" % "4.2.0" ,
    "uk.gov.hmrc"       %% "http-caching-client"        % "9.2.0-play-27",
    "org.apache.pdfbox" % "pdfbox"                      % "2.0.20",
    "uk.gov.hmrc"       %% "play-language"              % "4.10.0-play-27",
    nettyServer
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest"           %% "scalatest"          % "3.0.9"               % scope,
        "org.pegdown"             % "pegdown"             % "1.6.0"               % scope,
        "org.jsoup"               % "jsoup"               % "1.13.1"              % scope,
        "org.mockito"             % "mockito-core"        % "3.7.7"               % scope,
        "org.scalatestplus.play"  %% "scalatestplus-play" % "4.0.3"               % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
