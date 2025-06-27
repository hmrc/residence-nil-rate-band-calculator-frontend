import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

private object AppDependencies {

  val bootstrapPlayVersion = "9.13.0"
  val hmrcMongoVersion     = "2.6.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "12.6.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "org.apache.pdfbox"  % "pdfbox"                     % "2.0.28"
  )

  val unitTestDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.playframework" %% "play-test"               % PlayVersion.current,
    "org.scalatest"     %% "scalatest"               % "3.2.17",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion
  ).map(_ % Test)

  val integrationTestDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-test-play-30" % hmrcMongoVersion % "it",
    "org.scalatest"         %% "scalatest"               % "3.2.17"         % "it",
    "com.github.tomakehurst" % "wiremock-jre8"           % "2.35.0"         % "it"
  )

  def apply(): Seq[sbt.ModuleID] = compile ++ unitTestDependencies
}
