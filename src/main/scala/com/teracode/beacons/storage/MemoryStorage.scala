package com.teracode.beacons.storage

import java.util.UUID
import scala.collection.parallel.mutable
import com.teracode.beacons.services.{Beacon, Location}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.json4s.jackson.JsonMethods.parse
import org.json4s.jvalue2extractable
import org.json4s.string2JsonInput

import scala.io.Source

import scala.collection.immutable.HashMap

/**
 * Created by diegoram on 2/1/15.
 */
object LocationMemoryStorage extends Storage[Location] {
  private implicit val format1 = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer
  private val defaultLocationsString = Source.fromFile("src/main/resources/data/Locations.json").getLines().mkString
  private val defaultLocations = parse(defaultLocationsString).extract[List[Location]]

  private val Locations = mutable.ParHashMap[UUID, Location](defaultLocations.map(m => (m.id, m)): _*)

  def add(location: Location): Future[UUID] = Future {
    Locations += (location.id -> location)
    location.id
  }

  def get(id: UUID): Future[Option[Location]] = Future {
    Locations.get(id)
  }

  def delete(id: UUID): Future[Boolean] = Future {
    Locations.remove(id) match {
      case Some(_)  => true
      case None     => false
    }
  }

  def search(): Future[Seq[Location]] = Future {
    Locations.values.toList
  }
}