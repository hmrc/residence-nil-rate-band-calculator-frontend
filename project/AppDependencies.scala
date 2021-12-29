import sbt._
import play.sbt.PlayImport._

private object AppDependencies {

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "simple-reactivemongo"       % "8.0.0-play-28",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"         % "0.88.0-play-28",
    "uk.gov.hmrc"       %% "govuk-template"             % "5.68.0-play-28",
    "uk.gov.hmrc"       %% "play-ui"                    % "9.6.0-play-28",
    "com.typesafe.play" %% "play-json-joda"             % "2.9.2",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % "5.3.0",
    "uk.gov.hmrc"       %% "http-caching-client"        % "9.5.0-play-28",
    "org.apache.pdfbox" % "pdfbox"                      % "2.0.24",
    "uk.gov.hmrc"       %% "play-language"              % "5.1.0-play-28",
    nettyServer
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.pegdown"             %   "pegdown"                     % "1.6.0"               % scope,
        "org.jsoup"               %   "jsoup"                       % "1.13.1"              % scope,
        "org.mockito"             %   "mockito-core"                % "3.7.7"               % scope,
        "com.vladsch.flexmark"    %   "flexmark-all"                % "0.35.10"             % scope,
        "org.scalatestplus"       %%  "scalatestplus-mockito"       % "1.0.0-M2"            % scope,
        "org.scalatestplus.play"  %%  "scalatestplus-play"          % "5.1.0"               % scope,
        "org.scalatestplus"       %%  "scalatestplus-scalacheck"    % "3.1.0.0-RC2"         % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
