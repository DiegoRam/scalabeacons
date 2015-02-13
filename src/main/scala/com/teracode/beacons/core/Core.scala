package com.teracode.beacons.core

import akka.actor.ActorDSL._
import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.io.Tcp._
import com.sksamuel.elastic4s.ElasticClient
import com.teracode.beacons.routing.{LocationService, RoutedHttpService}
import com.teracode.beacons.storage.LocationESStorage
import spray.routing.RouteConcatenation

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests.
 */
trait Core {
  implicit def system: ActorSystem
}

/**
 * This trait implements ``Core`` by starting the required ``ActorSystem`` and registering the
 * termination handler to stop the system when the JVM exits.
 */
trait BootedCore extends Core {

  /**
   * Construct the ActorSystem we will use in our application
   */
  implicit lazy val system = ActorSystem("beacons-system")

  val ioListener = actor("io-listener")(new Act with ActorLogging {
    become {
      case b @ Bound(connection) => log.info(b.toString)
    }
  })

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())
}

trait SettingsCore {
  val settings = Settings()
}

trait ElasticSearchCore {
  // Load any settings if needed
  private val client = ElasticClient.local

  val locationESStorage = LocationESStorage(client)
}

/**
 * The REST API layer. It exposes the REST services, but does not provide any
 * web server interface.<br/>
 * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
 * to the top-level actors that make up the system.
 */
trait ApiCore extends RouteConcatenation {
  self: Core with ElasticSearchCore =>

  private implicit val _ = system.dispatcher

  val routes =
    LocationService(locationESStorage).route

  val serviceActor = system.actorOf(RoutedHttpService.props(routes))

}

