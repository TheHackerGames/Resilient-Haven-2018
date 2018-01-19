package com.resilient.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._enhanceRouteWithConcatenation
import com.resilient.providers.ConfigProvider

trait Routes extends AuthenticationRoute with HealthCheck with ChatRoomRoute {
  self: ConfigProvider =>

  val routes: Route = authenticationRoute ~ healthCheckRoute ~ roomRoute

}
