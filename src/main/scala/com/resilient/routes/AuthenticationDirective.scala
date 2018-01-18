package com.resilient.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.resilient.model.JsonWebToken
import com.resilient.providers.ConfigProvider
import io.circe.syntax.EncoderOps
import pdi.jwt.{Jwt, JwtAlgorithm}

trait AuthenticationDirective {
  self: ConfigProvider =>

  import Directives._
  import io.circe.generic.auto._

  private case class AuthenticationRequest(militaryId: String, firstName: String, lastName: String)

  private case class AuthenticationResponse(token: String)

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  val authRoute = pathPrefix("auth") {
    (post & formFieldMap) { auth =>
      val instanceId = config.getString("chatkit.instanceLocator")
      val secretKey = config.getString("chatkit.secret")
      val keyId = config.getString("chatkit.keyId")

      val jwt = JsonWebToken(instanceId, s"api_keys/$keyId", "Test")

      val headers = List(
        RawHeader("Authorization", s"Bearer ${Jwt.encode(jwt.asJson.noSpaces, secretKey, JwtAlgorithm.HS256)}")
      )
      respondWithHeaders(headers) {
        complete(StatusCodes.OK)
      }
    }
  }
}
