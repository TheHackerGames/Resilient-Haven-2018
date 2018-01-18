package com.resilient.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor

trait RoomDirective {

  import Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

  private case class AuthenticationRequest(militaryId: String, firstName: String, lastName: String)

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer
  val config: Config
  val logger: LoggingAdapter

  val roomRoute = pathPrefix("rooms") {
    get {
      complete {

      }
    }
  }
}
