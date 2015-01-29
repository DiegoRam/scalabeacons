package com.teracode.beacons.services


import com.wordnik.swagger.annotations._
import javax.ws.rs.Path
import spray.routing.HttpService
import spray.httpx.Json4sSupport

@Api(value = "/beacon", description = "Operations about beacon.", produces="application/json", position=1)
trait BeaconService extends HttpService {

  val routes = readRoute ~ getBeacon

  @ApiOperation(value = "Updated beacon", notes = "This can only be done by the logged in user.", nickname = "updateUser", httpMethod = "PUT")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "ID of the Node-Beacon be updated", required = true, dataType = "string", paramType = "path"),
    new ApiImplicitParam(name = "body", value = "Updated node object.", required = false, dataType = "string", paramType = "form")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Node not found"),
    new ApiResponse(code = 400, message = "Invalid Node supplied")
  ))
  def readRoute = put { path("beacon" / Segment) { id =>
    complete(s"Put ${id}")
  }}

  @ApiOperation(value = "Get Noe by Id", notes = "", response=classOf[User], nickname = "getNodeByID", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Id", value = "ID of Node that needs to be updated", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Node does not exist.")
  ))
  def getBeacon = post { path("beacon" / Segment) { id => { formFields('name, 'status) { (name, status) =>
    complete(s"Posted $name, $status")
  }}}}

}

case class Beacon(id: String, description: String, status: String)