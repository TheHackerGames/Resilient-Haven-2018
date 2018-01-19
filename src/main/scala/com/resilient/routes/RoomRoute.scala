package com.resilient.routes

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.resilient.actors.ChatRooms
import com.resilient.model.{ChatRoom, RoomType}
import com.typesafe.config.Config

import scala.concurrent.duration.DurationLong

private[routes] trait RoomRoute {

  import Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  val config: Config

  private val rooms = system.actorOf(Props[ChatRooms], "rooms")
  private implicit val askTimeout: Timeout = 3 seconds // and a timeout

  val roomRoute: Route = pathPrefix("rooms") {
    (get & parameters("type")) { (roomType) =>
      complete {
        (rooms ? ChatRooms.Rooms(RoomType.withName(roomType))).mapTo[Seq[ChatRoom]]
      }
    } ~
      get {
        complete {
          (rooms ? ChatRooms.Rooms).mapTo[Seq[ChatRoom]]
        }
      } ~
      (post & entity(as[ChatRoom])) { room =>
        complete {
          StatusCodes.Created -> {
            rooms ! ChatRooms.CreateRoom(room)
          }
        }
      } ~
      path(IntNumber) { roomId =>
        delete {
          complete {
            rooms ! ChatRooms.DeleteRoom(roomId)
          }
        }
      }
  }

}
