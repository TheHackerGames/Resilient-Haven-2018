package com.resilient.model

import io.circe.{Decoder, Encoder}

object RoomType extends Enumeration {
  type RoomType = Value
  val Financial, MentalHealth = Value
  implicit val decoder: Decoder[RoomType.Value] = Decoder.enumDecoder(RoomType)
  implicit val encoder: Encoder[RoomType.Value] = Encoder.enumEncoder(RoomType)
}