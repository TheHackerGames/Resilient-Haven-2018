//import ReleaseTransformations._

name := "encryption-api"

version := "0.0.3"

scalaVersion := "2.12.4"

mainClass in assembly := Some("com.resilient.Application")
organization := "com.resilient"

libraryDependencies ++= {
  val akkaV = "2.5.9"
  val akkaHttpV = "10.0.11"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.pauldijou" %% "jwt-core" % "0.14.1",
    "io.circe" %% "circe-core" % "0.9.0",
    "io.circe" %% "circe-generic" % "0.9.0",
    "io.circe" %% "circe-parser" % "0.9.0"
  )
}


//releaseProcess := Seq[ReleaseStep](
//  checkSnapshotDependencies,              // : ReleaseStep
//  inquireVersions,                        // : ReleaseStep
//  runClean,                               // : ReleaseStep
//  runTest,                                // : ReleaseStep
//  setReleaseVersion,                      // : ReleaseStep
//  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
//  tagRelease,                             // : ReleaseStep
//  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
//  setNextVersion,                         // : ReleaseStep
//  commitNextVersion,                      // : ReleaseStep
//  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
//)