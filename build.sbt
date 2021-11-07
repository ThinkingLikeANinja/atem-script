ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "atem-script",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
  )
