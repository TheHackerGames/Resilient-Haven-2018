package com.resilient.routes

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.resilient.actors.Rooms
import com.typesafe.config.Config

import scala.concurrent.duration.DurationLong

trait RoomDirective {

  import Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private case class RoomRequest(roomId: Int)

  implicit val system: ActorSystem

  implicit val materializer: Materializer
  val config: Config

  private val rooms = system.actorOf(Props[Rooms], "rooms")

  val roomRoute = pathPrefix("rooms") {
    get {
      rejectEmptyResponse {
        implicit val askTimeout: Timeout = 3 seconds // and a timeout

        onSuccess((rooms ? Rooms.PopRoom).mapTo[Option[Int]]) { maybeRoom =>
          complete(maybeRoom)
        }
      }
    } ~
      post {
        (post & entity(as[RoomRequest])) { (room) =>
          rooms ! Rooms.CreateRoom(room.roomId)
          complete()
        }
      }
  }
}
