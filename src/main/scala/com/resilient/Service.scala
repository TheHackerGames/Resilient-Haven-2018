package com.resilient

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.resilient.providers.ConfigProvider
import com.resilient.routes.Routes
import com.typesafe.config.Config

trait Service extends Routes {
  self: ConfigProvider =>

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  val config: Config

  val heroHaven: Route = logRequestResult("akka-http-microservice") {
    routes
  }

}