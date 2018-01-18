package com.resilient.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.resilient.model.{AuthenticationRequest, JsonWebToken}
import com.typesafe.config.Config
import io.circe.generic.auto._
import io.circe.syntax._
import pdi.jwt.{Jwt, JwtAlgorithm}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContextExecutor

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
        val instanceId = config.getString("chatkit.instanceLocator")
        val secretKey = config.getString("chatkit.secret")
        val keyId = config.getString("chatkit.keyId")

        val jwt = JsonWebToken(instanceId, s"api_keys/$keyId", "Test")

        Jwt.encode(jwt.asJson.noSpaces, secretKey, JwtAlgorithm.HS256)
      }
    }
  }
}
