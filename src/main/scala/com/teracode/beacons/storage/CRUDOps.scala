package com.teracode.beacons.storage

import com.teracode.beacons.domain.SignalSearch
import java.util.UUID
import scala.concurrent.Future

/**
 * Created by mdtealdi on 05/02/15.
 */
trait CRUDOps[A] {
  def add(element: A): Future[UUID]
  def delete(id: UUID): Future[Boolean]
  def get(id: UUID): Future[Option[A]]
  def search(): Future[Seq[A]]
  def search(desc: String): Future[Seq[A]]
  def search(ss: SignalSearch): Future[Seq[A]]
}