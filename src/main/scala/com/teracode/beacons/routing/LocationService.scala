package com.teracode.beacons.routing

import akka.actor.ActorRefFactory
import com.teracode.beacons.domain.Location
import com.teracode.beacons.storage.LocationESStorage
import com.wordnik.swagger.annotations._
import spray.http.StatusCodes._
import spray.http.{HttpEntity, HttpHeaders, HttpResponse, StatusCodes}
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

class BaseLocationService(val locationESStorage: LocationESStorage)(implicit val actorRefFactory: ActorRefFactory) extends LocationService

object LocationService {
  def apply(locationESStorage: LocationESStorage)(implicit actorRefFactory: ActorRefFactory): LocationService = {
    new BaseLocationService(locationESStorage)(actorRefFactory)
  }
}

@Api(value = "/locations", description = "Operations about location.", produces="application/json", position=1)
trait LocationService extends HttpService {

  import com.teracode.beacons.routing.utils.Json4sSupport._

  val locationESStorage: LocationESStorage

  val LocationsPath = "locations"

  val routes = addRoute ~ getRoute ~ deleteRoute ~ searchRoute

  @ApiOperation(value = "Add a new Location", nickname = "addLocation", httpMethod = "POST", consumes = "application/json, application/vnd.custom.beacon")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Location object to be added", dataType = "Location", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 405, message = "Invalid input")
  ))
  def addRoute = post {
    path(LocationsPath) {
      requestUri { uri =>
        decompressRequest() {
          entity(as[Location]) { location =>
            onSuccess(locationESStorage.add(location)) { id =>
              complete(HttpResponse(StatusCodes.Created, HttpEntity.Empty, List(HttpHeaders.Location(s"${uri}/${id.toString}"))))
            }
          }
        }
      }
    }
  }

  @ApiOperation(value = "Gets a Location by Id", notes = "", response=classOf[Location], nickname = "getLocationById", httpMethod = "GET", produces = "application/json, application/vnd.custom.node")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Id of Location", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Location not found.")
  ))
  def getRoute = get {
    path(LocationsPath / JavaUUID) { locationId =>
      onSuccess(locationESStorage.get(locationId)) {
        case Some(location)   => complete(location)
        case None             => complete(NotFound)
      }
    }
  }

  @ApiOperation(value = "Deletes a Location", nickname = "deleteLocation", httpMethod = "DELETE")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Location Id to delete", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Location for the given ID not found.")
  ))
  def deleteRoute = delete {
    path(LocationsPath / JavaUUID) { locationId =>
      onSuccess(locationESStorage.delete(locationId)) {
        case true   => complete(NoContent)
        case false  => complete(NotFound)
      }
    }
  }

  @ApiOperation(value = "Searches for a Location", nickname = "searchLocation", httpMethod = "GET", produces = "application/json, application/xml")
  def searchRoute = get {
    path(LocationsPath) {
      onSuccess(locationESStorage.search()) { result =>
        complete(result)
      }
    }
  }

}

