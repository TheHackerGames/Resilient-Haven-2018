package com.resilient.routes

import java.io.IOException

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.resilient.model.AuthenticationRequest
import pdi.jwt.{Jwt, JwtAlgorithm}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContextExecutor, Future}

trait AuthenticationDirective extends DefaultJsonProtocol {

  private implicit val ipPairSummaryRequestFormat = jsonFormat1(AuthenticationRequest.apply)
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer
  val config: Config
  val logger: LoggingAdapter

  val authRoute = pathPrefix("auth") {
      (post & entity(as[AuthenticationRequest])) { auth =>
        complete {
          Jwt.encode("""{"user":1}""", "secretKey", JwtAlgorithm.HS256)
        }
      }
  }
}
