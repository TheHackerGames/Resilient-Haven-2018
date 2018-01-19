package com.resilient.model

import java.time.LocalDateTime

import com.resilient.model.RoomType.RoomType
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ChatRoom(id: Int, alias: String, types: Seq[RoomType], open: Option[LocalDateTime], close: Option[LocalDateTime])

object ChatRoom {
  implicit val decoder: Decoder[ChatRoom] = deriveDecoder
  implicit val encoder: Encoder[ChatRoom] = deriveEncoder
}