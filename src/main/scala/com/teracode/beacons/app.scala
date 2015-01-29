package com.teracode.beacons

import akka.actor.{Actor, ActorSystem, Props, ActorLogging}
import akka.actor.ActorDSL._
import akka.io.IO
import spray.can.Http
import spray.routing._
import spray.util._
import akka.io.Tcp._

object BeaconApp extends App {
  implicit val system = ActorSystem("beacons-system")

  val service= system.actorOf(Props[ServiceActor], "beacon-service")

  val ioListener = actor("ioListener")(new Act with ActorLogging {
    become {
      case b @ Bound(connection) => log.info(b.toString)
    }
  })

  IO(Http).tell(Http.Bind(service, AppConfig.HttpConfig.interface, AppConfig.HttpConfig.port), ioListener)

}