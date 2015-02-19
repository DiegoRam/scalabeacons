package com.teracode.beacons.persistence

import com.teracode.beacons.domain.{LocationEntity, BeaconData, LocationData}
import java.util.UUID
import scala.concurrent.Future

case class LocationSearchByBeacons(beacons: List[BeaconData])
case class Hit[A](score: Double, source: A)

trait CRUDOps[A] {
  def create(element: A): Future[UUID]
  def retrieve(id: UUID): Future[Option[A]]
  def retrieveAll(): Future[Seq[A]]
  // def update(element: A): Future[Boolean]
  def delete(id: UUID): Future[Boolean]

}

trait SearchOps[A] {
  def search(query: String): Future[Seq[Hit[A]]]
  def search(ss: LocationSearchByBeacons): Future[Seq[Hit[A]]]
}

trait LocationRepository extends CRUDOps[LocationEntity] with SearchOps[LocationEntity]


