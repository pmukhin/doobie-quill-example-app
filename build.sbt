name := "doobie-pp"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0-M1",
  "org.typelevel" %% "cats-effect" % "1.3.1",

  "org.tpolecat" %% "doobie-core" % "0.7.0",
  // And add any of these as needed

  "mysql" % "mysql-connector-java" % "5.1.16",

  "org.tpolecat" %% "doobie-hikari" % "0.7.0", // HikariCP transactor.
  "org.tpolecat" %% "doobie-quill" % "0.7.0", // Support for Quill 3.1.0

  "com.chuusai" %% "shapeless" % "2.3.3",

  "org.tpolecat" %% "doobie-specs2" % "0.7.0" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "0.7.0" % "test", // ScalaTest support for typechecking statements.
)

scalacOptions ++= Seq(
  "-Ypartial-unification",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
)