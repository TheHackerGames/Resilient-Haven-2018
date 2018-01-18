package com.resilient.routes

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.resilient.actors.Rooms
import com.resilient.model.{Room, RoomType}
import com.typesafe.config.Config

import scala.concurrent.duration.DurationLong

trait RoomDirective {

  import Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  implicit val system: ActorSystem

  implicit val materializer: Materializer
  val config: Config

  private val rooms = system.actorOf(Props[Rooms], "rooms")
  private implicit val askTimeout: Timeout = 3 seconds // and a timeout

  val roomRoute: Route = path("rooms") {
    rejectEmptyResponse {
      (get & parameters("type")) { (roomType) =>
        onSuccess((rooms ? Rooms.PopRoom(RoomType.withName(roomType))).mapTo[Option[Room]]) { maybeRoom =>
          complete(maybeRoom)
        }
      } ~
        get {
          onSuccess((rooms ? Rooms.PopRoom).mapTo[Option[Room]]) { maybeRoom =>
            complete(maybeRoom)
          }
        } ~
        (post & entity(as[Room])) { room =>
          complete {
            rooms ! Rooms.CreateRoom(room)
          }
        }
    }
  }

}
