package com.teracode.beacons.services

import java.util.UUID
import scala.collection.parallel.mutable
import scala.util.{Try, Failure, Success}

/**
 * Created by diegoram on 2/1/15.
 */
object MemoryStorage {

  private val Locations = mutable.ParHashMap[UUID, Location](
    UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00") -> Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"), "Fravega", "Retailer", "Active", List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2))),
    UUID.fromString("167e6162-3b6f-4ae2-a171-2470b63dff01") -> Location(UUID.fromString("167e6162-3b6f-4ae2-a171-2470b63dff01"), "MacDonals", "FastFood", "Active", List(Beacon("MacDonalsW", 3), Beacon("Fravega-Wifi", 2)))
  )

  def deleteLocation(id: UUID): Try[Location] = {
    Locations.remove(id) match {
      case Some(l) => Success(l)
      case None => Failure(new Error("No such element"))
    }
  }
  def addLocation(location: Location): Try[Location] = {
    Try{
      Locations += (location.id -> location)
      location
    }
  }

  def getLocation(id: UUID): Try[Location] = {
    Locations.get(id) match {
      case Some(l) => Success(l)
      case None => Failure(new Error("No such element"))
    }
  }

  def searchLocation(): Try[List[Location]] = {
    Try {
      Locations.values.toList
    }
  }

  var userList = List()
}