package com.resilient.model

import java.time.Instant
import java.time.temporal.ChronoUnit

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class JsonWebToken(app: String, iss: String, iat: Long, exp: Long, sub: String, su: Boolean = true)

object JsonWebToken {

  implicit val encoder: Encoder[JsonWebToken] = deriveEncoder
  implicit val decoder: Decoder[JsonWebToken] = deriveDecoder

  def apply(instanceId: String, identifier: String, userId: String): JsonWebToken = {
    val now = Instant.now()
    JsonWebToken(instanceId, identifier, now, now.plus(1, ChronoUnit.DAYS), userId)
  }

  private implicit def instantSeconds(instant: Instant): Long = instant.getEpochSecond
}