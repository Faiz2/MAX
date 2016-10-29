package com.pharbers.datacalc.manager.domestic

import akka.actor.ActorLogging
import akka.actor.Actor
import com.pharbers.datacalc.manager.maxmessage.excute
import com.pharbers.datacalc.manager.maxmessage.MarketMessageRoutes
import com.pharbers.datacalc.manager.maxmessage.resultModelRun
import akka.actor.ActorRef
import com.pharbers.datacalc.manager.maxmessage.timeout

class MarketRoutesActor extends Actor with ActorLogging{
     var originSender : ActorRef = null
     def receive = {
		case excute(maxObj) => {
            originSender = sender
            maxObj.list match {
                case Nil => {
                    log.info("=============maxObj.list存在Nil===========")
                    originSender ! Nil
                }
                case head :: tail => {
                    val handle = MarketCalcFilterActor(self, MarketMessageRoutes(tail,maxObj.hosp_data,maxObj.hosp_match_data,maxObj.admin_market,maxObj.user_market))
					handle ! head
                }
            }
        }
        case resultModelRun(rst) => {
            originSender ! rst
        }
        case timeout() => {
            log.error("timeout!!!!!!!!!!!!")
            originSender ! timeout()
        }
        case _ => ???
 	}
}