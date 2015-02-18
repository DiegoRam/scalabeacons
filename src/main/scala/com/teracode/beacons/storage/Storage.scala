package com.teracode.beacons.storage

import com.teracode.beacons.domain.{Location, LocationSearchByBeacons}
import java.util.UUID
import scala.concurrent.Future

trait CRUDOps[A] {
  def create(element: A): Future[UUID]
  def retrieve(id: UUID): Future[Option[A]]
  def retrieveAll(): Future[Seq[A]]
  // def update(element: A): Future[Boolean]
  def delete(id: UUID): Future[Boolean]

}

trait SearchOps[A] {
  def search(query: String): Future[Seq[A]]
  def search(ss: LocationSearchByBeacons): Future[Seq[A]]
}

trait LocationStorage extends CRUDOps[Location] with SearchOps[Location]