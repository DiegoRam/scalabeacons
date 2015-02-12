package com.teracode.beacons.services

import java.util.UUID

import com.teracode.beacons.services.utils.Json4sSupport
import com.wordnik.swagger.annotations._
import spray.http.HttpHeaders
import spray.http.StatusCodes._
import spray.http.{HttpEntity, HttpResponse, StatusCodes}
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

case class Location(id: UUID, name: String, description: String, status: String, signals: List[Beacon] = List[Beacon]())
case class Beacon(ssid: String, level: Int)

@Api(value = "/locations", description = "Operations about location.", produces="application/json", position=1)
trait LocationService extends HttpService {

  import Json4sSupport._
  import com.teracode.beacons.storage.{LocationESStorage => storage}

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
            onSuccess(storage.add(location)) { id =>
              complete(HttpResponse(StatusCodes.Created, HttpEntity.Empty, List(HttpHeaders.Location(s"${uri}/${id.toString}"))))
            }
          }
        }
      }
    }
  }

  @ApiOperation(value = "Get Location by Id", notes = "", response=classOf[Location], nickname = "getLocationById", httpMethod = "GET", produces = "application/json, application/vnd.custom.node")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Id of Location", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Location not found.")
  ))
  def getRoute = get {
    path(LocationsPath / JavaUUID) { locationId =>
      onSuccess(storage.get(locationId)) {
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
      onSuccess(storage.delete(locationId)) {
        case true   => complete(NoContent)
        case false  => complete(NotFound)
      }
    }
  }

  @ApiOperation(value = "Searches for a Location", nickname = "searchLocation", httpMethod = "GET", produces = "application/json, application/xml")
  def searchRoute = get {
    path(LocationsPath) {
      onSuccess(storage.search()) { result =>
        complete(result)
      }
    }
  }

}

