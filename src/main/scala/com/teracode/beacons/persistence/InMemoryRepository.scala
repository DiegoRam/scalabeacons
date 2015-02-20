package com.teracode.beacons.persistence

import java.util.UUID

import com.teracode.beacons.domain.{LocationEntity, LocationData}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{jvalue2extractable, string2JsonInput}

import scala.collection.parallel.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

object LocationInMemoryRepository extends LocationRepository {
  
  implicit val formats = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer
  private val defaultLocationsString = Source.fromFile("src/main/resources/data/Locations.json").getLines().mkString
  private val defaultLocations = parse(defaultLocationsString).extract[List[LocationEntity]]

  private val Locations = mutable.ParHashMap[UUID, LocationEntity](defaultLocations.map(m => (m.id, m)): _*)

  def create(location: LocationEntity): Future[UUID] = Future {
    Locations += (location.id -> location)
    location.id
  }

  def retrieve(id: UUID): Future[Option[LocationEntity]] = Future {
    Locations.get(id)
  }

  def delete(id: UUID): Future[Boolean] = Future {
    Locations.remove(id) match {
      case Some(_)  => true
      case None     => false
    }
  }

  def retrieveAll(): Future[Seq[LocationEntity]] = Future {
    Locations.values.toList
  }

  def search(des: String): Future[Seq[Hit[LocationEntity]]] = Future {
    Locations.values.toList map (Hit[LocationEntity](1.0, _))
  }

  def search(ss: LocationSearchByBeacons): Future[Seq[Hit[LocationEntity]]] = Future {
    Locations.values.toList map (Hit[LocationEntity](1.0, _))
  }
}