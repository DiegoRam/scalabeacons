package com.teracode.beacons.routing

import com.teracode.beacons.domain.User
import com.teracode.beacons.routing.utils.Json4sSupport
import com.wordnik.swagger.annotations._
import spray.routing.HttpService

@Api(value = "/user", description = "Operations about users.", produces="application/json", position=1)
trait UserService extends HttpService {

  import com.teracode.beacons.routing.utils.Json4sSupport._
  val routes = updateRoute ~ getRoute

  @ApiOperation(value = "Updated user", notes = "This can only be done by the logged in user.", nickname = "updateUser", httpMethod = "PUT")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "username", value = "Name of the user that needs to be updated", required = true, dataType = "string", paramType = "path"),
    new ApiImplicitParam(name = "body", value = "Updated user object.", required = false, dataType = "string", paramType = "form")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "User not found"),
    new ApiResponse(code = 400, message = "Invalid username supplied")
  ))
  def updateRoute = put { path("user" / Segment) { id =>
    complete(s"Put ${id}")
  }}

  @ApiOperation(value = "Get user by name", notes = "", response=classOf[User], nickname = "getUserByName", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Updated name of the user.", required = false, dataType = "string", paramType = "form"),
    new ApiImplicitParam(name = "status", value = "Updated status of the user.", required = false, dataType = "string", paramType = "form")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "User does not exist.")
  ))
  def getRoute = post { path("user" / IntNumber) { id =>
      complete("")
    }
  }

}
