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

  def delete(id: UUID): Future[Location] = Future[Location] {
    Locations.remove(id) match {
      case Some(l) => l
      case None => throw new Error("No such element")
    }
  }

  def add(location: Location): Future[Location] = Future[Location] {
    Locations += (location.id -> location)
    location
  }

  def get(id: UUID): Future[Location] = Future[Location] {
    Locations.get(id) match {
      case Some(l) => l
      case None => throw new Error("No such element")
    }
  }

  def search(): Future[Seq[Location]] = Future[Seq[Location]] {
    Locations.values.toList
  }
}