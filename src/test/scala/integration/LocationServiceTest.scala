package integration

import java.util.UUID

import com.teracode.beacons.core.ElasticSearchCore
import com.teracode.beacons.domain.{BeaconData, LocationData, LocationEntity}
import com.teracode.beacons.persistence.Hit
import com.teracode.beacons.routing.LocationService
import org.specs2.mutable.Specification
import spray.http.Uri.Query
import spray.http._
import spray.httpx.Json4sJacksonSupport
import spray.testkit.Specs2RouteTest

trait LocationFixture {

  def aLocation(): LocationData = {
    LocationData(
      "Best Buy Test",
      "Best Buy at Scala Mall, electronics mobiles computers",
      "Active",
      List(BeaconData("AP3", 4), BeaconData("AP4", 2))
    )
  }
}

class LocationServiceTest extends Specification with Specs2RouteTest with Json4sJacksonSupport with ElasticSearchCore with LocationFixture {

  implicit val json4sJacksonFormats = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer
  val service = LocationService(locationESStorage)

  sequential
  "the Location service" should {

    "create a new Location" in {
      val newLocation = aLocation
      Post("/locations", newLocation) ~> service.routes ~> check {
        status should be(StatusCodes.Created)
        val locationHeader = header(HttpHeaders.Location.name)
        locationHeader should not be empty
      }
    }

    "retrieve a created Location" in {
      val locationId = UUID.fromString("e9fecbb4-11de-4e34-b61a-04e119626535")
      val newLocation = aLocation

      Put(s"/locations/$locationId", newLocation) ~> service.routes ~> check {
        status should be(StatusCodes.Created)
      }

      Get(s"/locations/$locationId") ~> service.routes ~> check {
        status should be(StatusCodes.OK)
        val retrievedLocation = responseAs[LocationEntity]
        retrievedLocation.id mustEqual(locationId)
      }
    }

    "retrieve a list of Locations" in {

      Post("/locations", aLocation) ~> service.routes ~> check {
        status should be(StatusCodes.Created)
      }

      Post("/locations", aLocation) ~> service.routes ~> check {
        status should be(StatusCodes.Created)
      }

      Get("/locations") ~> service.routes ~> check {
        status should be(StatusCodes.OK)
        val locations = responseAs[List[LocationEntity]]
        locations.size must beGreaterThanOrEqualTo(2)
      }
    }

    "search a Location by its description" in {

      val newLocation = aLocation.copy(description = "Find me by my description.")
      Post("/locations", newLocation) ~> service.routes ~> check {
        status should be(StatusCodes.Created)
      }

      Get(Uri("/locations/search").withQuery(Query("description" -> newLocation.description))) ~> service.routes ~> check {
        status should be(StatusCodes.OK)
        val locations = responseAs[List[Hit[LocationEntity]]]
        locations.exists(_.source.description == newLocation.description) must beTrue
        //locations.size must beGreaterThanOrEqualTo(2)
      }
    }

    // TODO
    /*
    "retrieve a payload by a given json query" in {
      Post("/locations/signal-search", HttpEntity(MediaTypes.`application/json`, queryPayload)) ~> service.signalSearchRoute ~> check {
        handled must beTrue
        status === OK
        val unmarshalled = parse(body.asString)
        val sourceList = for {
          JObject(child) <- unmarshalled
          JField("score", JDouble(source)) <- child
        } yield source

        sourceList must have size(10)
      }
    }
    */
  }

}