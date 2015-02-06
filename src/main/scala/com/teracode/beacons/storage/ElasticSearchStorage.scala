package com.teracode.beacons.storage

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

import java.util.UUID

import com.teracode.beacons.services.{Beacon, Location}

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._

import org.json4s.jackson.JsonMethods.parse
import org.json4s.jvalue2extractable
import org.json4s.string2JsonInput


/**
 * Created by mdtealdi on 05/02/15.
 */
abstract class ElasticSearchStorage[A] extends Storage[A] {

  val client: ElasticClient
  val indexName: String
  val clusterName: String

  def add(element: A): Future[UUID]
  def delete(id: UUID): Future[Boolean]
  def get(id: UUID): Future[Option[A]]
  def search(): Future[Seq[A]]
}

object LocationESStorage extends ElasticSearchStorage[Location] {
  implicit val format1 = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer

  val client = ElasticClient.local
  val indexName = "location"
  val clusterName = ""

  client.execute {
    create index indexName mappings (
      "location" as(
        "id" typed StringType index NotAnalyzed,
        "name" typed StringType index NotAnalyzed,
        "description" typed StringType,
        "status" typed StringType index NotAnalyzed,
        "signals" typed NestedType as(
          "ssid" typed StringType index NotAnalyzed,
          "level" typed IntegerType index NotAnalyzed
          )
        )
      )
  }

  val defaultLocationsString = Source.fromFile("src/main/resources/data/Locations.json").getLines().mkString
  val defaultLocations = parse(defaultLocationsString).extract[List[Location]]

  //TODO Set settings and mappings

  def add(location: Location): Future[UUID] = Future {
    client.execute(
      index into indexName doc location
    )
    location.id
  }

  def delete(id: UUID): Future[Boolean] = Future {
    // Implement function
    true
  }

  def get(id: UUID): Future[Option[Location]] = Future {
    Some(
      Location(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"),
      "Fravega",
      "Retailer",
      "Active",
      List(Beacon("Fravega-Wifi", 3), Beacon("MacDonalsW", 2)))
    )
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