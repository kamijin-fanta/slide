name := "examples"

version := "1.0"

scalaVersion := "2.12.3"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.4",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.4" % Test,
  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)
