package com.resilient.model

import java.time.Instant
import java.time.temporal.{ChronoUnit, TemporalUnit}

//app: Your instance ID (the last field of the service instance locator)
//iss: An identifier for the key used to sign the token, has the format “api_keys/<key ID>”
//iat: The unix timestamp when the token was issued (seconds)
//exp: The unix timestamp when the token expires; this should be later than iat
//sub: User ID

case class JsonWebToken(app: String, iss: String, iat: Long, exp: Long, sub: String, su: Boolean = true)

object JsonWebToken {
  def apply(instanceId: String, identifier: String, userId: String): JsonWebToken = {
    val now = Instant.now()
    JsonWebToken(instanceId, identifier, now, now.plus(1, ChronoUnit.DAYS), userId)
  }

  private implicit def instantSeconds(instant: Instant): Long = instant.getEpochSecond
}