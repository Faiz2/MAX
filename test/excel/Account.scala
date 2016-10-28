package excel

import akka.actor.Actor
import akka.actor.Actor._

class Account extends Actor{
    def receive = {
        case msg => println("消息是："+msg.toString);Thread.sleep(100)
    }
}