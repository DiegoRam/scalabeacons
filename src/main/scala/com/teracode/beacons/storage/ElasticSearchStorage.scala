package com.teracode.beacons.storage

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

import java.util.UUID

import com.teracode.beacons.services.{Beacon, Location}

import com.sksamuel.elastic4s.{ElasticDsl, ElasticClient}
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
  val doctype: String

  def add(element: A): Future[UUID]
  def delete(id: UUID): Future[Boolean]
  def get(id: UUID): Future[Option[A]]
  def search(): Future[Seq[A]]
}

object LocationESStorage extends ElasticSearchStorage[Location] {
  implicit val format1 = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer

  val client = ElasticClient.local
  val indexName = "beacon"
  val doctype = "location"

  def init() = {
    client.execute {
      create index indexName mappings (
        doctype as(
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
    }.await
  }

  def loadDefaultDoc(): Unit = {
    val defaultLocationsString = Source.fromFile("src/main/resources/data/Locations.json").getLines().mkString
    val defaultLocations = parse(defaultLocationsString).extract[List[Location]]

    client.execute(
      bulk(
        defaultLocations.map(l => index into indexName -> doctype doc l id l.id)
      )
    ).await

    client.refresh(indexName).await
  }

  def add(location: Location): Future[UUID] = Future {
    client.execute(
      index into indexName -> doctype doc location
    )
    location.id
  }

  def delete(reqId: UUID): Future[Boolean] = {
    client.execute(
      ElasticDsl.delete id reqId from indexName -> doctype
    ) map (r => r.isFound)
  }

  def get(reqId: UUID): Future[Option[Location]] = {
    val f = client.execute(
      ElasticDsl.get id reqId from indexName -> doctype
    )
    f map (r =>
      r.isSourceEmpty match {
        case true => None
        case false => Some(parse(r.getSourceAsString).extract[Location])
      }
    )
  }

  def search(): Future[Seq[Location]] = {
    val f = client.execute(
      ElasticDsl.search in indexName -> doctype query matchall
    )
    f map { sr =>
      sr.getHits.hits().toSeq map (l => parse(l.sourceAsString()).extract[Location])
    }
  }

}