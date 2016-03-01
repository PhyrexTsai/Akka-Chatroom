package webservice

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit._

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import events.Events
import route._

import scala.concurrent.duration.Duration

/**
  * Created by Phyrex on 2016/2/28.
  */
class WebService(implicit fm: Materializer, system: ActorSystem) extends Directives {
  val chatroom = ChatHandler.create(system)
  val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
  // 這邊使用 system.dispather 來實作 implicit => ExcutionContextExcutor
  import system.dispatcher
  system.scheduler.schedule(Duration.create(60, SECONDS), Duration.create(60, SECONDS)) {
    chatroom.boardcastMessage(Events.ChatMessage(sender = "clock", s"Time is ${simpleDateFormat.format(new Date())}."))
  }

  def route = MainService.route ~ ChatService.route(chatroom) ~ getFromResourceDirectory("web")

}
