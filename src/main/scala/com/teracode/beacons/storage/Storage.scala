package com.teracode.beacons.storage

import scala.concurrent.Future
import com.sksamuel.elastic4s.ElasticClient
import java.util.UUID

/**
 * Created by mdtealdi on 05/02/15.
 */
trait Storage[A] {
  def add(elem: A): Future[A]
  def delete(id: UUID): Future[A]
  def get(id: UUID): Future[A]
  def search(): Future[Seq[A]]
}