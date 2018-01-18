package com.resilient.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
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
