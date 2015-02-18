package com.teracode.beacons.domain

import java.util.UUID

case class User(username: String, status: String)
case class Location(id: UUID, name: String, description: String, status: String, signals: List[Beacon] = List[Beacon]())
case class Beacon(ssid: String, level: Int)

case class LocationSearchByBeacons(beacons: List[Beacon])

