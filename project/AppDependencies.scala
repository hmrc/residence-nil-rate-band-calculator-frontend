import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

private object AppDependencies {

  val bootstrapPlayVersion = "9.13.0"
  val hmrcMongoVersion     = "2.6.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "12.4.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "org.apache.pdfbox"  % "pdfbox"                     % "2.0.28"
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion,
    "org.playframework" %% "play-test"               % PlayVersion.current,
    "uk.gov.hmrc" %% "http-verbs-play-26" % "12.3.0",
  ).map(_ % Test)

  def apply(): Seq[sbt.ModuleID] = compile ++ testDependencies
}
