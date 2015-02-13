package com.teracode.beacons.storage

import java.util.UUID

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.{ElasticClient, ElasticDsl}
import com.teracode.beacons.domain.Location
import org.json4s.jackson.JsonMethods.parse
import org.json4s.{jvalue2extractable, string2JsonInput}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

trait ElasticSearchStorage {
  val client: ElasticClient
  val indexName: String
  val doctype: String
}

class BaseLocationESStorage(val client: ElasticClient) extends LocationESStorage {

  override implicit val formats = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer

  init
  loadDefaultDoc

  def init(): Unit = {
    client.execute {
      deleteIndex(indexName)
    }.await

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
}

object LocationESStorage {
  def apply(client: ElasticClient): LocationESStorage = new BaseLocationESStorage(client)
}

trait LocationESStorage extends ElasticSearchStorage with CRUDOps[Location] {
  implicit val formats = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer

  val indexName = "beacon"
  val doctype = "location"

  def add(location: Location): Future[UUID] = Future {
    client.execute(
      index into indexName -> doctype doc location
    )
    location.id
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

  def delete(reqId: UUID): Future[Boolean] = {
    client.execute(
      ElasticDsl.delete id reqId from indexName -> doctype
    ) map (r => r.isFound)
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