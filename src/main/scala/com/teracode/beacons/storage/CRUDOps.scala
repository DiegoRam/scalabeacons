package com.teracode.beacons.storage

import scala.concurrent.Future
import com.sksamuel.elastic4s.ElasticClient
import java.util.UUID

/**
 * Created by mdtealdi on 05/02/15.
 */
trait CRUDOps[A] {
  def add(element: A): Future[UUID]
  def delete(id: UUID): Future[Boolean]
  def get(id: UUID): Future[Option[A]]
  def search(): Future[Seq[A]]
}