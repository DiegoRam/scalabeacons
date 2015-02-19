package com.teracode.beacons.routing

import akka.actor.ActorRefFactory
import com.teracode.beacons.domain.{ImplicitEntityConverters, LocationData}
import com.teracode.beacons.persistence.{LocationSearchByBeacons, LocationRepository, CRUDOps, LocationESRepository}
import com.wordnik.swagger.annotations._
import javax.ws.rs.Path
import spray.http.StatusCodes._
import spray.http.{HttpEntity, HttpHeaders, HttpResponse, StatusCodes}
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

class BaseLocationService(val storage: LocationRepository)(implicit val actorRefFactory: ActorRefFactory) extends LocationService

object LocationService {
  def apply(storage: LocationRepository)(implicit actorRefFactory: ActorRefFactory): LocationService = {
    new BaseLocationService(storage)(actorRefFactory)
  }
}

@Api(value = "/locations", description = "Operations about location.", produces="application/json", position=1)
trait LocationService extends HttpService {

  import com.teracode.beacons.routing.utils.Json4sSupport._
  import ImplicitEntityConverters._

  val storage: LocationRepository

  val LocationsPath = "locations"

  val routes = addRoute ~ getAllRoute ~ getRoute ~ getAllRoute ~ deleteRoute ~ signalSearchRoute ~ descriptionSearchRoute

  @ApiOperation(value = "Add a new Location", nickname = "addLocation", position = 2, httpMethod = "POST", consumes = "application/json, application/vnd.custom.beacon")
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
          entity(as[LocationData]) { location =>
            onSuccess(storage.create(location)) { id =>
              complete(HttpResponse(StatusCodes.Created, HttpEntity.Empty, List(HttpHeaders.Location(s"${uri}/${id.toString}"))))
            }
          }
        }
      }
    }
  }

  @ApiOperation(value = "Gets a Location by Id", notes = "", position = 1, response=classOf[LocationData], nickname = "getLocationById", httpMethod = "GET", produces = "application/json, application/vnd.custom.node")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Id of Location", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Location not found.")
  ))
  def getRoute = get {
    path(LocationsPath / JavaUUID) { locationId =>
      onSuccess(storage.retrieve(locationId)) {
        case Some(location)   => complete(location)
        case None             => complete(NotFound)
      }
    }
  }

  @ApiOperation(value = "Retrieves all Locations", nickname = "retrieves all Locations", position = 6, httpMethod = "GET", produces = "application/json, application/xml")
  def getAllRoute = get {
    path(LocationsPath) {
      onSuccess(storage.retrieveAll()) { result =>
        complete(result)
      }
    }
  }

  @ApiOperation(value = "Deletes a Location", nickname = "deleteLocation", position = 3, httpMethod = "DELETE")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Location Id to delete", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Location for the given ID not found.")
  ))
  def deleteRoute = delete {
    path(LocationsPath / JavaUUID) { locationId =>
      onSuccess(storage.delete(locationId)) {
        case true   => complete(NoContent)
        case false  => complete(NotFound)
      }
    }
  }

  @ApiOperation(value = "Searchs Locations by description", nickname = "DescriptionLocationSearch", position = 4, httpMethod = "GET", produces = "application/json, application/xml")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "description", value = "Description String", required = true, dataType = "String", paramType = "query")
  ))
  @Path("/search")
  def descriptionSearchRoute = get {
    path(LocationsPath / "search") {
      parameters('description) { description =>
        onSuccess(storage.search(description)) { result =>
          complete(result)
        }
      }
    }
  }

  @ApiOperation(value = "Searchs Locations by Signals", nickname = "SignalsLocationSearch", position = 5, httpMethod = "POST", consumes = "application/json", produces = "application/json, application/xml")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "signals", value = "List of Beacon", required = true, dataType = "LocationSearchByBeacons", paramType = "body")
  ))
  @Path("/signal-search")
  def signalSearchRoute = post {
    path(LocationsPath / "signal-search") {
      decompressRequest() {
        entity(as[LocationSearchByBeacons]) { signalSearch =>
          onSuccess(storage.search(signalSearch)) { result =>
            complete(result)
          }
        }
      }
    }
  }

  // Related: https://github.com/swagger-api/swagger-core/issues/606
  // Why is this still showing even though it's set to hidden? See https://github.com/martypitt/swagger-springmvc/issues/447
  @ApiOperation(value = "IGNORE", notes = "", position = 7, hidden = true, httpMethod = "GET", response = classOf[LocationSearchByBeacons])
  protected def showSignal = Unit
}

