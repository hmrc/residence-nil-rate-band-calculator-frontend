import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

private object AppDependencies {

  val bootstrapPlayVersion = "8.2.0"
  val hmrcMongoVersion = "1.4.0"
  val mockitoCoreVersion = "5.2.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "8.2.0",
    "org.mockito"        % "mockito-core"               % mockitoCoreVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "org.apache.pdfbox"  % "pdfbox"                     % "2.0.28",
    nettyServer
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"   % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30"  % hmrcMongoVersion,
    "org.playframework" %% "play-pekko-http-server"   % "3.0.0",
    "org.playframework" %% "play-pekko-http2-support" % "3.0.0"
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID]
  = Seq(
    "org.playframework" %% "play-test"              % PlayVersion.current,
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapPlayVersion,
    "org.mockito"        % "mockito-core"           % mockitoCoreVersion
  ).map(_ % Test)

  def apply(): Seq[sbt.ModuleID] = compile ++ testDependencies ++ itDependencies
}
