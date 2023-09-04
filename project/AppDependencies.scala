import sbt._
import play.sbt.PlayImport._

private object AppDependencies {

  val bootstrapVersion = "7.15.0"
  val
  mongoPlayVersion = "1.2.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"         % mongoPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"         % "7.7.0-play-28",
    "com.typesafe.play" %% "play-json-joda"             % "2.9.4",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "org.apache.pdfbox" %  "pdfbox"                     % "2.0.28",
    "uk.gov.hmrc"       %% "play-language"              % "6.2.0-play-28",
    nettyServer
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc"             %%  "bootstrap-test-play-28"      % bootstrapVersion      % scope,
        "org.pegdown"             %   "pegdown"                     % "1.6.0"               % scope,
        "org.jsoup"               %   "jsoup"                       % "1.16.1"              % scope,
        "org.mockito"             %   "mockito-core"                % "5.3.1"               % scope,
        "com.vladsch.flexmark"    %   "flexmark-all"                % "0.35.10"             % scope,
        "org.scalatestplus"       %%  "scalatestplus-mockito"       % "1.0.0-M2"            % scope,
        "org.scalatestplus.play"  %%  "scalatestplus-play"          % "5.1.0"               % scope,
        "org.scalatestplus"       %%  "scalatestplus-scalacheck"    % "3.1.0.0-RC2"         % scope,
        "uk.gov.hmrc.mongo"       %%  "hmrc-mongo-test-play-28"     % mongoPlayVersion      % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}
