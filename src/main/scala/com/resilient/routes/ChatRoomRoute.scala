package com.resilient.routes

import java.time.LocalDateTime

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

trait ChatRoomRoute {

  import Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  private implicit val askTimeout: Timeout = 3 seconds

  val config: Config

  private val rooms = system.actorOf(Props[ChatRooms], "rooms")

  val roomRoute: Route = pathPrefix("rooms") {
    (get & parameters('type.as[String].?, 'open.as[String].?, 'close.as[String].?)) { (maybeRoomType, maybeOpen, maybeClose) =>
      complete {
        val roomType = maybeRoomType.map(RoomType.withName)
        val open = maybeOpen.map(LocalDateTime.parse)
        val close = maybeClose.map(LocalDateTime.parse)

        val action = if (roomType.isDefined || open.isDefined || close.isDefined) ChatRooms.Rooms { room =>
          roomType.exists(room.types.contains) &&
            room.open == open &&
            room.close == close
        } else ChatRooms.Rooms

        (rooms ? action).mapTo[Seq[ChatRoom]]
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
