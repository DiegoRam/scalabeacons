name := "scalabeacons"

version := "1.0"

scalaVersion := "2.11.5"

val akka = "2.3.6"
val spray = "1.3.2"

libraryDependencies ++= Seq("com.gettyimages" %% "spray-swagger" % "0.5.0",
  "com.github.nscala-time" %% "nscala-time" % "1.2.0",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "com.typesafe.akka" %% "akka-testkit" % akka % "test",
  "com.typesafe.akka" %% "akka-actor" % akka,
  "com.typesafe.akka" %% "akka-slf4j" % akka,
  "io.spray" %% "spray-routing" % spray,
  "io.spray" %% "spray-client" % spray,
  "io.spray" %% "spray-testkit" % spray % "test",
  "com.gettyimages" %% "spray-swagger" % "0.5.0",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "com.typesafe" % "config" % "1.2.1",
  "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.11"
)