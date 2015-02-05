package com.teracode.beacons.services


import java.util.UUID

import com.wordnik.swagger.annotations._
import spray.routing.HttpService
import spray.http.StatusCodes._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class Location(id: UUID, name: String, description: String, status: String, signals: List[Beacon] = List[Beacon]())
case class Beacon(ssid: String, level: Int)

@Api(value = "/location", description = "Operations about location.", produces="application/json", position=1)
trait LocationService extends HttpService {

  import com.teracode.beacons.Json4sSupport._
  import com.teracode.beacons.storage.LocationMemoryStorage

  val routes = getRoute ~ searchRoute ~ addRoute ~ deleteRoute

  @ApiOperation(value = "Get location by Id", notes = "", response=classOf[Location], nickname = "getLocationByID", httpMethod = "GET", produces = "application/json, application/vnd.custom.node")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "ID of Location", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Location does not exist.")
  ))
  def getRoute = get {
    path("location" / JavaUUID) { reqId =>
      onComplete(LocationMemoryStorage.get(reqId)) {
        case Success(x) => complete(x)
        case Failure(y) => complete(InternalServerError, s"$y")
      }
    }
  }

  @ApiOperation(value = "Add a new Location", nickname = "addLocation", httpMethod = "POST", consumes = "application/json, application/vnd.custom.beacon")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Location object to be added", dataType = "Location", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 405, message = "Invalid input")
  ))
  def addRoute = post {
    path("location") {
      decompressRequest() {
        entity(as[Location]) {
          location => {
            onComplete(LocationMemoryStorage.add(location)) {
              case Success(x) => complete(x)
              case Failure(y) => complete(InternalServerError, s"$y")
            }
          }
        }
      }
    }
  }

  @ApiOperation(value = "Searches for a Location", nickname = "searchLocation", httpMethod = "GET", produces = "application/json, application/xml")
  def searchRoute = get {
    path("location" / "search" ) {
      onComplete(LocationMemoryStorage.search()) {
        case Success(x) => complete(x)
        case Failure(y) => complete(InternalServerError, s"$y")
      }
    }
  }

  @ApiOperation(value = "Deletes a Location", nickname = "deleteLocation", httpMethod = "DELETE")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Location id to delete", required = true, dataType = "JavaUUID", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid Location id")
  ))
  def deleteRoute = delete {
    path("location" / JavaUUID) { reqId =>
      onComplete(LocationMemoryStorage.delete(reqId)) {
        case Success(x) => complete("")
        case Failure(y) => complete(BadRequest, s"$y")
      }
    }
  }
}