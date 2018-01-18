package com.resilient.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.resilient.model.JsonWebToken
import com.typesafe.config.Config
import io.circe.syntax.EncoderOps
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.concurrent.ExecutionContextExecutor

trait AuthenticationDirective {

  import Directives._
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private case class AuthenticationRequest(militaryId: String, firstName: String, lastName: String)

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer
  val config: Config
  val logger: LoggingAdapter

  val authRoute = pathPrefix("auth") {
    post {
      entity(as[AuthenticationRequest]) { auth =>
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
}
