package com.resilient.model

import com.resilient.model.RoomType.RoomType

case class Room(roomId: Int, types: Seq[RoomType])
