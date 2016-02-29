package route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

/**
  * Created by Phyrex on 2016/2/28.
  */
object MainService {
  def route : Route = pathSingleSlash {
    getFromResource("web/index.html")
  }
}
