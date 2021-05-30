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
crossPaths := false // drop off Scala suffix from artifact names.
autoScalaLibrary := false // exclude scala-library from dependencies

libraryDependencies += "com.maxmind.geoip" % "geoip-api" % "1.3.1"
