ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "http4s-hello"
  )

val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.5"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion
)

libraryDependencies ++= Seq(

  // Start with this one
  "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1",

  // And add any of these as needed
  "org.tpolecat" %% "doobie-h2"        % "1.0.0-RC1",          // H2 driver 1.4.200 + type mappings.
  "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC1",          // HikariCP transactor.
  "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC1" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC1" % "test"  // ScalaTest support for typechecking statements.

)

libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.33"