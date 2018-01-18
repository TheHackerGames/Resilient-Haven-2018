package com.resilient.actors

import akka.actor.Actor
import com.resilient.actors.Rooms.{CreateRoom, PopRoom}
import com.resilient.model.Room
import com.resilient.model.RoomType.RoomType

class Rooms extends Actor {

  def receive: Receive = receive(Seq.empty)

  def receive(rooms: Seq[Room]): Receive = {
    case CreateRoom(room) => context.become(receive(if (!rooms.exists(_.roomId == room.roomId)) rooms :+ room else rooms))
    case PopRoom(roomType) =>
      val maybeRoom = rooms.find(_.types.contains(roomType))
      sender ! maybeRoom
      maybeRoom.foreach(ro => context.become(receive(rooms.filter(_ == ro))))
    case PopRoom =>
      val maybeRoom = rooms.headOption
      sender ! maybeRoom
      context.become(receive(if (rooms.isEmpty) Seq.empty else rooms.tail))
  }

}

object Rooms {

  case class CreateRoom(room: Room)

  case class PopRoom(roomType: RoomType)

  case object PopRoom

}
