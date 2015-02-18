package com.teracode.beacons.routing

import akka.actor.{Actor, ActorLogging, Props}
import com.gettyimages.spray.swagger.SwaggerHttpService
import com.wordnik.swagger.model.ApiInfo
import spray.http.StatusCodes._
import spray.http.{StatusCodes, HttpResponse, HttpEntity, StatusCode}
import spray.routing._
import spray.util.LoggingContext
import scala.reflect.runtime.universe._

import scala.util.control.NonFatal

/**
 * Holds potential error response with the HTTP status and optional body
 *
 * @param responseStatus the status code
 * @param response the optional body
 */
case class ErrorResponseException(responseStatus: StatusCode, response: Option[HttpEntity]) extends Exception

/**
 * Allows you to construct Spray ``HttpService`` from a concatenation of routes; and wires in the error handler.
 * It also logs all internal server errors using ``SprayActorLogging``.
 *
 * @param routes the (concatenated) route
 */
class RoutedHttpService(routes: Route) extends HttpServiceActor with ActorLogging {

  override def actorRefFactory = context

  implicit val handler = ExceptionHandler {
    case NonFatal(ErrorResponseException(statusCode, entity)) => ctx =>
      ctx.complete(statusCode, entity)

    case NonFatal(e) => ctx => {
      log.error(e, InternalServerError.defaultMessage)
      ctx.complete(InternalServerError)
    }
  }

  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[LocationService])
    override def apiVersion = "0.1"
    override def baseUrl = "/"
    override def docsPath = "api-docs"
    override def actorRefFactory = context
    override def apiInfo = Some(new ApiInfo("Beacons API",
      "Beacons",
      "TOC Url", "Diego Ramirez", "Apache V2", "http://www.apache.org/licenses/LICENSE-2.0"))
  }

  val route =
    routes ~
    swaggerService.routes ~
    get {
      pathPrefix("") {
        pathEndOrSingleSlash {
          getFromResource("swagger-ui/index.html")
        }
      } ~ getFromResourceDirectory("swagger-ui")
    }

  def receive: Receive =
    runRoute(route)(handler, RejectionHandler.Default, context, RoutingSettings.default, LoggingContext.fromActorRefFactory)

}

object RoutedHttpService {
  def props(servicesRoutes: Route): Props = Props(classOf[RoutedHttpService], servicesRoutes)
}