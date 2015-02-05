package com.teracode.beacons.storage

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.sksamuel.elastic4s.ElasticClient
import java.util.UUID
import com.teracode.beacons.services.{Beacon, Location}
/**
 * Created by mdtealdi on 05/02/15.
 */
abstract class ElasticSearchStorage[A] extends Storage[A] {

  val client: ElasticClient
  val indexName: String
  val clusterName: String


  def add(elem: A): Future[A]
  def delete(id: UUID): Future[A]
  def get(id: UUID): Future[A]
  def search(): Future[Seq[A]]
}


object LocationESStorage extends ElasticSearchStorage[Location] {
  val client = ElasticClient.local
  val indexName = "location"
  val clusterName = ""

  //TODO Sett settings and mappings

  def add(elem: Location): Future[Location] = Future[Location] {
    Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"),
      "Fravega",
      "Retailer",
      "Active",
      List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2)))
  }

  def delete(id: UUID): Future[Location] = Future[Location] {
    Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"),
      "Fravega",
      "Retailer",
      "Active",
      List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2)))
  }

  def get(id: UUID): Future[Location] = Future[Location] {
    Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"),
      "Fravega",
      "Retailer",
      "Active",
      List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2)))
  }

  def search(): Future[Seq[Location]] = Future[Seq[Location]] {
    Seq(
      Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"),
      "Fravega",
      "Retailer",
      "Active",
      List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2)))
      )
  }

}