package com.teracode.beacons.storage

import java.util.UUID

import com.teracode.beacons.domain.{LocationSearchByBeacons, Location}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{jvalue2extractable, string2JsonInput}

import scala.collection.parallel.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

/**
 * Created by diegoram on 2/1/15.
 */
object LocationMemoryStorage extends LocationStorage {
  private implicit val format1 = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer
  private val defaultLocationsString = Source.fromFile("src/main/resources/data/Locations.json").getLines().mkString
  private val defaultLocations = parse(defaultLocationsString).extract[List[Location]]

  private val Locations = mutable.ParHashMap[UUID, Location](defaultLocations.map(m => (m.id, m)): _*)

  def create(location: Location): Future[UUID] = Future {
    Locations += (location.id -> location)
    location.id
  }

  def retrieve(id: UUID): Future[Option[Location]] = Future {
    Locations.get(id)
  }

  def delete(id: UUID): Future[Boolean] = Future {
    Locations.remove(id) match {
      case Some(_)  => true
      case None     => false
    }
  }

  def retrieveAll(): Future[Seq[Location]] = Future {
    Locations.values.toList
  }

  def search(des: String): Future[Seq[Hit[Location]]] = Future {
    Locations.values.toList map (Hit[Location](1.0, _))
  }

  def search(ss: LocationSearchByBeacons): Future[Seq[Hit[Location]]] = Future {
    Locations.values.toList map (Hit[Location](1.0, _))
  }
}