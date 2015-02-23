package com.teracode.beacons.domain

import java.util.UUID

/* Model */
trait Entity {
  def id: UUID
}

trait Location {
  def name: String
  def description: String
  def status: String
  def signals: List[BeaconData]
}

trait Beacon {
  def ssid: String
  def level: Double
}

/* Data */
case class LocationData(name: String, description: String, status: String, signals: List[BeaconData]) extends Location
case class BeaconData(ssid: String, level: Double) extends Beacon

/* Entity */
case class LocationEntity(id: UUID, name: String, description: String, status: String, signals: List[BeaconData]) extends Location with Entity

object ImplicitEntityConverters {

  implicit def locationDataToLocationEntity(location: LocationData): LocationEntity = {
    LocationEntity(
      UUID.randomUUID(),
      location.name,
      location.description,
      location.status,
      location.signals
    )
  }
}
