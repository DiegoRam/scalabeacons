package integration

import com.teracode.beacons.core.ElasticSearchCore
import org.specs2.mutable.Specification
import spray.http.{MediaTypes, HttpEntity}
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes._
import com.teracode.beacons.routing.LocationService
import org.json4s._
import org.json4s.native.JsonMethods._

trait Test {

  object expected {
    val id = JString("46baa808-cf7f-40e5-ab74-f2b8593a516e")
    val name = JString("Best Buy Test")
    val description = JString("Best Buy at Scala Mall, electronics mobiles computers")
    val signals = JArray(List(JObject(List(("ssid",JString("AP3")), ("level",JInt(4)))), JObject(List(("ssid",JString("AP4")), ("level",JInt(2))))))
  }

  val payload =
    """
      |{
      |  "id" : "46baa808-cf7f-40e5-ab74-f2b8593a516e",
      |  "name" : "Best Buy Test",
      |  "description" : "Best Buy at Scala Mall, electronics mobiles computers",
      |  "status" : "Active",
      |  "signals" : [
      |    {"ssid" : "AP3", "level" : 4},
      |    {"ssid" : "AP4", "level" : 2}
      |  ]
      |}
    """.stripMargin
}


class LocationServiceTest extends Specification with Specs2RouteTest with HttpService
  with ElasticSearchCore with Test{

  def actorRefFactory = system
  val service = LocationService(locationESStorage)

  sequential
  "Location service" should {

    "reject a invalid URI" in {
      Get("/foo") ~> service.getRoute ~> check {
        status === NotFound
        handled must beFalse
      }
    }

    "retrieve a list of locations" in {
      Get("/locations") ~> service.getAllRoute ~> check {
        handled must beTrue
        status === OK
        val unmarshalled = parse(body.asString)
        (unmarshalled \\ "id").children.size must beGreaterThanOrEqualTo(0)
      }
    }


    "create a location entry via POST" in {
      Post("/locations", HttpEntity(MediaTypes.`application/json`,payload)) ~> service.addRoute ~> check {
        handled must beTrue
        status === Created
      }
    }

    "retrieve an element from a given id" in {
      Get("/locations/46baa808-cf7f-40e5-ab74-f2b8593a516e") ~> service.getRoute ~> check {
        handled must beTrue
        status === OK
        val unmarshalled = parse(body.asString)
        unmarshalled \ "id" === expected.id
        unmarshalled \ "name" === expected.name
        unmarshalled \ "description" === expected.description
        unmarshalled \ "signals" === expected.signals

      }
    }
  }
}