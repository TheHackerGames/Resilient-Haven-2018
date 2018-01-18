package com.resilient.actors

import akka.actor.Actor
import com.resilient.actors.Rooms.{CreateRoom, PopRoom}

import scala.collection.immutable.Queue

class Rooms extends Actor {

  def receive: Receive = receive(Queue.empty)

  def receive(rooms: Queue[Int]): Receive = {
    case CreateRoom(key) => context.become(receive(rooms :+ key))
    case PopRoom => rooms.dequeueOption match {
      case Some((room, rest)) =>
        sender ! Some(room)
        context.become(receive(rooms.tail))
      case None => sender ! None
    }
  }

}

object Rooms {

  case class CreateRoom(message: Int)

  case object PopRoom

}
