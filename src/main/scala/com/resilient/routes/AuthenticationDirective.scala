package com.resilient.routes

import akka.actor.ActorSystem
import akka.event.Logging
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
    (post & formFieldMap & parameters("user_id")) { (auth, userId) =>

      println("Auth Payload: " + auth)
      println("User id: " + userId)

      val instanceId = config.getString("chatkit.instanceLocator")
      val secretKey = config.getString("chatkit.secret")
      val keyId = config.getString("chatkit.keyId")

      val jwt = JsonWebToken(instanceId, s"api_keys/$keyId", userId)
      val jwtString = Jwt.encode(jwt.asJson.noSpaces, secretKey, JwtAlgorithm.HS256)

      val headers = List(
        RawHeader("Authorization", s"Bearer $jwtString"),
        RawHeader("Content-Type", "application/json")
      )
      respondWithHeaders(headers) {
        complete(
          s"""{
             |  "access_token":"$jwtString",
             |  "refresh_token":"$jwtString",
             |  "user_id":"${jwt.sub}",
             |  "token_type":"access_token",
             |  "expires_in":"${jwt.exp}"
             |}""".stripMargin)
      }
    }
  }
}
