package integration

import org.specs2.mutable.Specification
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes._

class LocationServiceTest extends Specification with Specs2RouteTest with HttpService{
  def actorRefFactory = system

  val dummieRoute = {
    get {
      pathSingleSlash {
        complete {
          <html>
            <body>
              <h1>
                Hello Teracode
              </h1>
            </body>
          </html>
        }
      } ~
      path("ping"){
        complete("pong!")
      }
    }
  }

  "Location service" should {
    "say hello from dummie route" in {
      Get() ~> dummieRoute ~> check {
        status === OK
        responseAs[String] must contain("Hello Teracode")
      }
    }
  }
}