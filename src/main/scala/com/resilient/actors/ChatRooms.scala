package com.resilient.actors

import akka.actor.{Actor, ActorLogging}
import com.resilient.actors.ChatRooms.{CreateRoom, DeleteRoom, Rooms}
import com.resilient.model.ChatRoom
import com.resilient.model.RoomType.RoomType

class ChatRooms extends Actor with ActorLogging {

  def receive: Receive = receive(Seq.empty)

  def receive(rooms: Seq[ChatRoom]): Receive = {
    case CreateRoom(room) => context.become(receive(if (!rooms.exists(_.id == room.id)) rooms :+ room else rooms))
    case Rooms(filter)  => sender ! rooms.filter(filter)
    case Rooms            => sender ! rooms
    case DeleteRoom(id)   => context.become(receive(rooms.filterNot(_.id == id)))
  }

}

object ChatRooms {

  case class CreateRoom(room: ChatRoom)

  case object Rooms

  case class Rooms(filter: ChatRoom => Boolean)

  case class DeleteRoom(id: Int)

}
