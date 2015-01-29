package com.teracode.beacons


import akka.actor.{Actor, ActorSystem, Props, ActorLogging}
import spray.routing._
import com.gettyimages.spray.swagger._
import scala.reflect.runtime.universe._
import com.wordnik.swagger.model.ApiInfo
import com.teracode.beacons.services._

class ServiceActor
  extends HttpServiceActor
  with ActorLogging {

  override def actorRefFactory = context

  val beacons = new BeaconService {
    def actorRefFactory = context
  }

  val users = new UserService {
    def actorRefFactory = context
  }

  def receive = runRoute(beacons.routes ~ users.routes ~ swaggerService.routes ~
    get {
      pathPrefix("") { pathEndOrSingleSlash {
        getFromResource("swagger-ui/index.html")
      }
      } ~
        getFromResourceDirectory("swagger-ui")
    })

  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[BeaconService], typeOf[UserService])
    override def apiVersion = "0.1"
    override def baseUrl = "/"
    override def docsPath = "api-docs"
    override def actorRefFactory = context
    override def apiInfo = Some(new ApiInfo("Beacons API",
      "Beacons",
      "TOC Url", "Diego Ramirez", "Apache V2", "http://www.apache.org/licenses/LICENSE-2.0"))
  }
}