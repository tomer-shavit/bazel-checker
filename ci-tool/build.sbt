ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "BazelChecker",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.3",
      "io.circe" %% "circe-parser" % "0.14.3"
    )
  )
