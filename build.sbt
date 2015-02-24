name := "scalabeacons"

version := "1.0"

scalaVersion := "2.11.5"

//Check this out : http://alvinalexander.com/scala/scala-execute-exec-external-system-commands-in-scala

val compileJade = taskKey[Int]("Compile all jade templates")
val cleanHtml = taskKey[Int]("Cleaning all html")

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
    "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.11",
    "org.codehaus.groovy" % "groovy-all" % "2.3.2"
  )
}

cleanHtml := {
  import sys.process._
  val s: TaskStreams = streams.value
  s.log.info("Cleaning old html files...")
  "rm src/main/resources/public/index.html" !
}

cleanHtml := {
  val s: TaskStreams = streams.value
  cleanHtml.result.value match {
    case Inc(inc: Incomplete) => {
      s.log.error("Error deleting files!")
      1
    }
    case Value(v) => {
      v match {
        case 0 => {
          s.log.info("Files deleted.")
          0
        }
        case 1 => {
          s.log.error("Files not deleted")
          1
        }
      }
    }
  }
}

compileJade := {
  import sys.process._
  cleanHtml.value
  val s = streams.value
  s.log.info("Compiling jade...")
  "jade src/main/resources/public/source/index.jade --out src/main/resources/public" !
}

compileJade := {
  val s = streams.value
  compileJade.result.value match {
    case Inc(inc: Incomplete) => {
      s.log.error("Error compiling!")
      1
    }
    case Value(v) => {
      v match {
        case 0 => {
          s.log.info("Compile done.")
          0
        }
        case 1 => {
          s.log.error("Error compiling jade files!")
          1
        }
      }
    }
  }
}

