organization := "org.mule"
name := "jmule"

scalaVersion := "2.13.2"
parallelExecution in ThisBuild := false

scalacOptions ++= Seq(
 // "-encoding", "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-feature",
)

Compile / compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-g:lines")

libraryDependencies += "com.maxmind.geoip" % "geoip-api" % "1.3.1"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.9"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9"