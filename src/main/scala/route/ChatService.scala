package route

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import events._
import webservice._
import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow

/**
  * Created by Phyrex on 2016/2/28.
  */
object ChatService {
  def route(chatroom: ChatHandler) : Route = path("chat") {
    parameter('name) { name =>
      handleWebSocketMessages(websocketChatFlow(chatroom, sender = name))
    }
  }

  def websocketChatFlow(chatroom : ChatHandler, sender: String): Flow[Message, Message, Any] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(msg) => msg
      }
      .via(chatroom.chatFlow(sender))
      .map {
        case msg : Events.Message => {
          TextMessage.Strict(Events parse msg)
        } // FIXME change this format
      }

}
