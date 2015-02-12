package com.teracode.beacons

import akka.actor.{ActorSystem, Props, ActorLogging}
import akka.actor.ActorDSL._
import akka.io.IO
import com.teracode.beacons.conf.AppConfig
import com.teracode.beacons.storage.LocationESStorage
import spray.can.Http
import akka.io.Tcp._

object BeaconApp extends App {
  implicit val system = ActorSystem("beacons-system")

  val service = system.actorOf(Props[ServiceActor], "beacon-service")

  // ElasticSearch Client initialization. Index name should not exist. rm -rf ./data folder if error is raised
  LocationESStorage.init()
  LocationESStorage.loadDefaultDoc()

  val ioListener = actor("ioListener")(new Act with ActorLogging {
    become {
      case b @ Bound(connection) => log.info(b.toString)
    }
  })

  IO(Http).tell(Http.Bind(service, AppConfig.HttpConfig.host, AppConfig.HttpConfig.port), ioListener)
}