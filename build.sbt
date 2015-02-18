name := "scalabeacons"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= {
  val akkaVersion = "2.3.6"
  val sprayVersion = "1.3.2"
  val json4sVersion = "3.2.11"
  Seq(
    "com.gettyimages" %% "spray-swagger" % "0.5.0",
    "com.github.nscala-time" %% "nscala-time" % "1.2.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test",
    "org.json4s" %% "json4s-jackson" % json4sVersion,
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.json4s" %% "json4s-ext" % json4sVersion,
    "com.typesafe" % "config" % "1.2.1",
    "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.11"
  )
}