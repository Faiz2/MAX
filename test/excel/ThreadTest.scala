package excel

import scala.collection.mutable.ListBuffer
import akka.actor.ActorSystem
import akka.actor.Props

object ThreadTest extends App{
   var system = ActorSystem()
   var echoServer = system.actorOf(Props[Account])
   for(i <- 1 to 100){
//       system = ActorSystem()
//       echoServer = system.actorOf(Props[Account])
       echoServer ! i 
   }
//   system.shutdown()
}