package webservice

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import events.Events

/**
  * Created by Phyrex on 2016/2/28.
  */
trait ChatHandler {
  def chatFlow(sender : String) : Flow[String, Events.Message, Any]

  // 可以建置 robot 用來 boardcast 一些資訊
  def boardcastMessage(message : Events.ChatMessage) : Unit
}

object ChatHandler {
  def create(system : ActorSystem): ChatHandler = {
    val chatActor = system.actorOf(Props[ChatClient])

    def chatInSink(sender: String) = Sink.actorRef[ChatEvent](chatActor, Left(sender))

    new ChatHandler {
      // Flow => 1 input, 1 output   Source => 0 input, 1 output    Sink => 1 input, 0 output
      // 這邊使用 chatFlow 來表示使用者收到訊息、登入與登出的整個串流
      def chatFlow(sender : String) : Flow[String, Events.Message, Any] = {
        val in = Flow[String]
          .map(ReceivedMessage(sender, _))
          .to(chatInSink(sender))

        val out = Source.actorRef[Events.ChatMessage](1, OverflowStrategy.fail)
          .mapMaterializedValue(chatActor ! Join(sender, _))

        Flow.fromSinkAndSource(in, out)
      }

      def boardcastMessage(message : Events.ChatMessage) : Unit = {
        chatActor ! message
      }
    }
  }

  sealed trait ChatEvent
  case class Join(name: String, subscriber: ActorRef) extends ChatEvent
  case class Left(name: String) extends ChatEvent
  case class ReceivedMessage(sender: String, message: String) extends ChatEvent {
    def toChatMessage: Events.ChatMessage = Events.ChatMessage(sender, message)
  }
}


