package com.resilient.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

private[routes] trait HealthCheck {

  val healthCheckRoute: Route = path("ping") {
    get {
      complete {
        "pong"
      }
    }
  }

}
