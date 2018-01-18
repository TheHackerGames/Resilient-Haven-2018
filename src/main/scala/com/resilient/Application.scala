package com.resilient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import com.resilient.providers.ConfigProvider
import com.resilient.routes.{AuthenticationDirective, RoomDirective}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

trait Service extends AuthenticationDirective with RoomDirective {
  self: ConfigProvider =>

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  val config: Config

  val routes: Route = logRequestResult("akka-http-microservice") {
    authRoute ~ roomRoute
  }

}

object Application extends App {

  implicit val system$: ActorSystem = ActorSystem()
  implicit val executor$: ExecutionContextExecutor = system$.dispatcher
  implicit val materializer$: ActorMaterializer = ActorMaterializer()
  val config$: Config = ConfigFactory.load()

  val service: Service = new Service with ConfigProvider {
    lazy implicit val system: ActorSystem = system$
    lazy implicit val executor: ExecutionContextExecutor = executor$
    lazy implicit val materializer: ActorMaterializer = materializer$
    lazy val config: Config = config$
  }



  Http().bindAndHandle(service.routes, config$.getString("http.interface"), config$.getInt("http.port"))
}