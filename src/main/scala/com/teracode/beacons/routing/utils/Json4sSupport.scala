package com.teracode.beacons.routing.utils

import java.util.UUID

import org.json4s._
import spray.http.Uri.Path
import spray.httpx.Json4sJacksonSupport
import spray.routing.PathMatcher1

import shapeless.{HList, HNil}
import spray.http.{StatusCodes, HttpResponse}
import spray.http.Uri.Path
import spray.httpx.unmarshalling.{Deserializer, MalformedContent}
import spray.routing.PathMatcher.{Matched, Unmatched}
import spray.routing._
import spray.routing.directives.{OnFailureFutureMagnet, OnSuccessFutureMagnet, OnCompleteFutureMagnet}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object UUIDPath extends PathMatcher1[UUID] {
  def apply(path: Path) = path match {
    case Path.Segment(segment, tail) ⇒ {
      Try(UUID.fromString(segment)) match {
        case Success(x: UUID) => Matched(tail, x :: HNil)
        case Failure(e) => Unmatched
      }
    }
    case _ => Unmatched
  }
}

/*
object Json4sSupport extends Json4sJacksonSupport {
  implicit def json4sJacksonFormats: Formats = jackson.Serialization.formats(NoTypeHints) + new UUIDFormat

  val jsonMethods = org.json4s.jackson.JsonMethods

  class UUIDFormat extends Serializer[UUID] {
    val UUIDClass = classOf[UUID]

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
      case (TypeInfo(UUIDClass, _), JString(x)) => UUID.fromString(x)
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: UUID => JString(x.toString)
    }
  }

  def toJValue[T](value: T): JValue = {
    Extraction.decompose(value)
  }
}

*/