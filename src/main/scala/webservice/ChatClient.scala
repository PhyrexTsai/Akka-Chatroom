package webservice

import akka.actor.{Terminated, Status, ActorRef, Actor}
import events.Events
import webservice.ChatHandler._

/**
  * Created by Phyrex on 2016/2/28.
  */
class ChatClient extends Actor {

  var subscribers = Set.empty[(String, ActorRef)]

  def members = subscribers.map(_._1).toSeq

  def receive = {
    case Join(name, subscriber) => {
      context.watch(subscriber)
      subscribers += (name -> subscriber)
      boardcast(Events.Joined(name, members))
    }
    case msg : ReceivedMessage => {
      boardcast(msg.toChatMessage)
    }
    case msg: Events.ChatMessage => {
      boardcast(msg)
    }
    case Left(name) => {
      val entry @ (username, ref) = subscribers.find(_._1 == name).get
      ref ! Status.Success(Unit)
      subscribers -= entry
      boardcast(Events.Leaved(name, members))
    }
    case Terminated(subscriber) => {
      subscribers = subscribers.filterNot(_._2 == subscriber)
    }
  }

  def boardcast(msg: Events.Message) {
    // boardcast to all client Actor and send message
    // _1 username
    // _2 ActorRef
    subscribers.foreach(_._2 ! msg)
  }
}
