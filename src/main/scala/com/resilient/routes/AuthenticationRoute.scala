package com.resilient.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.resilient.model.JsonWebToken
import com.resilient.providers.ConfigProvider
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import pdi.jwt.{Jwt, JwtAlgorithm}

private[routes] trait AuthenticationRoute extends FailFastCirceSupport {
  self: ConfigProvider =>

  private case class AuthenticationResponse(accessToken: String, refreshToken: String, userId: String, tokenType: String, expiresIn: Long)

  private object AuthenticationResponse {
    implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
    implicit val encoder: Encoder[AuthenticationResponse] = deriveEncoder
    implicit val decoder: Decoder[AuthenticationResponse] = deriveDecoder
  }

  implicit val system: ActorSystem
  implicit val materializer: Materializer

  private val instanceId: String = config.getString("chatkit.instanceLocator")
  private val secretKey: String = config.getString("chatkit.secret")
  private val keyId: String = config.getString("chatkit.keyId")

  val authenticationRoute: Route = path("auth") {
    (post & parameters("user_id")) { userId =>
      val jwt = JsonWebToken(instanceId, s"api_keys/$keyId", userId)
      val jwtString = Jwt.encode(jwt.asJson.noSpaces, secretKey, JwtAlgorithm.HS256)

      complete {
        AuthenticationResponse(jwtString, jwtString, jwt.sub, "access_token", jwt.exp)
      }
    }
  }
}
