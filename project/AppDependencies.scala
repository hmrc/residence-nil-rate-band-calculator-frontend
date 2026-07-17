import play.core.PlayVersion
import play.sbt.PlayImport.*
import sbt.*

private object AppDependencies {

  val bootstrapPlayVersion = "10.7.1"
  val playFrontendHmrcVersion = "13.9.0"
  val hmrcMongoVersion     = "2.12.0"
  val pdfboxVersion = "2.0.28"
  val scalatestVersion = "3.2.20"
  val wiremockVersion = "2.35.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % playFrontendHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "org.apache.pdfbox"  % "pdfbox"                     % pdfboxVersion
  )

  val unitTestDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "org.playframework" %% "play-test"               % PlayVersion.current,
    "org.scalatest"     %% "scalatest"               % scalatestVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion
  ).map(_ % Test)

  val integrationTestDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-test-play-30" % hmrcMongoVersion,
    "org.scalatest"         %% "scalatest"               % scalatestVersion,
    "com.github.tomakehurst" % "wiremock-jre8"           % wiremockVersion
  )

  def apply(): Seq[sbt.ModuleID] = compile ++ unitTestDependencies
}
