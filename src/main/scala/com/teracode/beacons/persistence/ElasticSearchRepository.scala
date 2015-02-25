package com.teracode.beacons.persistence

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.{QueryDefinition, ElasticDsl, ElasticClient}

import com.teracode.beacons.domain.{LocationEntity, BeaconData}

import java.util.UUID

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

class BaseLocationESRepository(val client: ElasticClient) extends LocationESRepository {

  override implicit val formats = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer

  init()
  loadDefaultDoc()

  private def init(): Unit = {
    val b = client.exists( indexName ).map({ r => r.isExists }).await
    if (!b) createIndex()
  }

  private def createIndex() = {
    client.execute {
      ElasticDsl.create index indexName mappings (
        doctype as(
          "id" typed StringType index NotAnalyzed,
          "name" typed StringType index NotAnalyzed,
          "description" typed StringType,
          "status" typed StringType index NotAnalyzed,
          "signals" typed NestedType as(
            "ssid" typed StringType index NotAnalyzed,
            "level" typed DoubleType index NotAnalyzed
            )
          )
        )
    }.await
  }

  private def loadDefaultDoc(): Unit = {
    val defaultLocationsString = Source.fromFile("src/main/resources/data/Locations.json").getLines().mkString
    val defaultLocations = parse(defaultLocationsString).extract[List[LocationEntity]]

    client.execute(
      bulk(
        defaultLocations.map(l => index into indexName -> doctype doc l id l.id)
      )
    ).await

    client.refresh(indexName).await
  }
}

object LocationESRepository {
  def apply(client: ElasticClient): LocationRepository = new BaseLocationESRepository(client)
}

trait LocationESRepository extends ElasticSearchStorage with LocationRepository {
  implicit val formats = org.json4s.DefaultFormats + org.json4s.ext.UUIDSerializer

  val indexName = "beacon"
  val doctype = "location"

  def create(location: LocationEntity): Future[UUID] = Future {
    client.execute(
      index into indexName -> doctype doc location id location.id
    )
    location.id
  }

  def retrieve(reqId: UUID): Future[Option[LocationEntity]] = {
    val f = client.execute(
      ElasticDsl.get id reqId from indexName -> doctype
    )
    f map (r =>
      r.isSourceEmpty match {
        case true => None
        case false => Some(parse(r.getSourceAsString).extract[LocationEntity])
      })
  }

  def delete(reqId: UUID): Future[Boolean] = {
    client.execute(
      ElasticDsl.delete id reqId from indexName -> doctype
    ) map (r => r.isFound)
  }

  def retrieveAll(): Future[Seq[LocationEntity]] = {
    val f = client.execute(
      ElasticDsl.search in indexName -> doctype query matchall from 0 size 1000
    )
    f map { sr =>
      sr.getHits.hits().toSeq map (l => parse(l.sourceAsString()).extract[LocationEntity])
    }
  }

  def search(ss: LocationSearchByBeacons): Future[Seq[Hit[LocationEntity]]] = {

    def beaconToNestedDecayQueryDefinition(b: BeaconData): QueryDefinition = {
      nestedQuery("signals") scoreMode "sum" query {
        val q = functionScoreQuery(termFilter("ssid", b.ssid)) scorers exponentialScore("level", b.level.toString, "0.2").offset(0.1) boostMode "replace"
        functionScoreQuery(q) scorers scriptScore(s"log(_score*(1+${b.level}))") boostMode "replace"
      }
    }

//    Debug.
//    val q = should ( ss.beacons.map(b => beaconToNestedDecayQueryDefinition(b)): _*) disableCoord true
//    println(q.builder.toString)

    val f = client.execute(
      ElasticDsl.search in indexName -> doctype query {
        should ( ss.beacons.map(b => beaconToNestedDecayQueryDefinition(b)): _*) disableCoord true
      } from 0 size 10
    )
    f map { sr =>
      sr.getHits.hits().toSeq map (l => Hit[LocationEntity](l.getScore.toDouble, parse(l.sourceAsString()).extract[LocationEntity]))
    }
  }

  def search(searchString: String): Future[Seq[Hit[LocationEntity]]] = {
    val f = client.execute(
      ElasticDsl.search in indexName -> doctype query {
        matchQuery("description", searchString)
      } from 0 size 1000
    )
    f map { sr =>
      sr.getHits.hits().toSeq map (l => Hit[LocationEntity](l.getScore.toDouble, parse(l.sourceAsString()).extract[LocationEntity]))
    }
  }

}