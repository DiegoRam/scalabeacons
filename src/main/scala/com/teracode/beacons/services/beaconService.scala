package com.teracode.beacons.services


import java.util.UUID

import com.wordnik.swagger.annotations._
import spray.routing.HttpService

import scala.util.{Failure, Success}

@Api(value = "/beacon", description = "Operations about beacon.", produces="application/json", position=1)
trait BeaconService extends HttpService {

  import com.teracode.beacons.Json4sSupport._
  import MemoryStorage._

  val routes =  getRoute ~ deleteRoute

  @ApiOperation(value = "Get Node by Id", notes = "", response=classOf[User], nickname = "getNodeByID", httpMethod = "GET", produces = "application/json, application/vnd.custom.node")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "ID of Node that needs to be updated", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Node does not exist.")
  ))
  def getRoute = get { path("beacon" / JavaUUID) { id =>
      complete(beaconList.toList)
    }
  }

  @ApiOperation(value = "Add a new Node", nickname = "addNode", httpMethod = "POST", consumes = "application/json, application/vnd.custom.beacon")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Node object to be added", dataType = "Beacon", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 405, message = "Invalid input")
  ))
  def addRoute = post {
    path("/beacon") {
      decompressRequest() {
        entity(as[Beacon]) {
          beacon => {
            detach() {
              complete(beacon)
            }
          }
        }
      }
    }
  }

  @ApiOperation(value = "Searches for a node", nickname = "searchNode", httpMethod = "GET", produces = "application/json, application/xml")
  def searchRoute = get {
    path("beacon/search") {
      complete(beaconList)
    }
  }

  @ApiOperation(value = "Deletes a Node", nickname = "deleteNode", httpMethod = "DELETE")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Pet id to delete", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid node value")
  ))
  def deleteRoute = delete {
    path("beacon" / Segment) { id => complete(s"Deleted $id")}
  }
}

case class Beacon(id: UUID, location: String, description: String, status: String, signals: List[Signal] = List[Signal]())
case class Signal(ssid: String, level: Int)