import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

private object AppDependencies {

  val bootstrapPlayVersion = "9.7.0"
  val hmrcMongoVersion = "2.4.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "11.11.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "org.apache.pdfbox"  % "pdfbox"                     % "2.0.28"
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"   % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30"  % hmrcMongoVersion,
    "org.playframework" %% "play-test"                % PlayVersion.current
  ).map(_ % Test)

  def apply(): Seq[sbt.ModuleID] = compile ++ testDependencies
}
