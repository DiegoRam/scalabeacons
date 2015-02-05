package com.teracode.beacons.storage

import java.util.UUID
import scala.collection.parallel.mutable
import com.teracode.beacons.services.{Beacon, Location}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by diegoram on 2/1/15.
 */
object LocationMemoryStorage extends Storage[Location]{

  private val Locations = mutable.ParHashMap[UUID, Location](
    UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00") -> Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), "Fravega", "Retailer", "Active", List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2))),
    UUID.fromString("167e6162-3b6f-4ae2-a171-2470b63dff01") -> Location(UUID.fromString("167e6162-3b6f-4ae2-a171-2470b63dff01"), "MacDonals", "FastFood", "Active", List(Beacon("MacDonalsW", 3), Beacon("Fravega-Wifi", 2)))
  )

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