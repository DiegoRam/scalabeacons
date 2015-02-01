package com.teracode.beacons.services

import java.util.UUID

/**
 * Created by diegoram on 2/1/15.
 */
object MemoryStorage {

  val beaconList = List(
    Beacon(UUID.randomUUID(), "Fravega", "Retailer", "Active", List(Signal("Fravega-Wifi", 3), Signal("MacDonalsW", 2))),
    Beacon(UUID.randomUUID(), "MacDonals", "FastFood", "Active", List(Signal("MacDonalsW", 3), Signal("Fravega-Wifi", 2)))
  )

  val userList = List()

}
