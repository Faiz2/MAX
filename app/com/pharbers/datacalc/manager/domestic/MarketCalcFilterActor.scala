package com.pharbers.datacalc.manager.domestic

import akka.actor.ActorLogging
import akka.actor.Actor
import com.pharbers.datacalc.manager.maxmessage._
import akka.actor.ActorRef
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import akka.actor.Props
import excel.model.Manage.AdminHospitalDataBase
import excel.model.modelRunData
import com.pharbers.datacalc.manager.maxmessage.DataMessage._
import com.pharbers.datacalc.manager.algorithm.backWriterSumVolumFunction
import com.pharbers.datacalc.manager.algorithm.maxUnionAlgorithm
import com.pharbers.datacalc.manager.algorithm.maxCalcUnionAlgorithm
import com.pharbers.util.StringOption
import com.pharbers.datacalc.manager.algorithm.maxCalcAlgorithm
import com.pharbers.util.RunDate

object MarketCalcFilterActor {
    def apply(originSender : ActorRef, msr : MarketMessageRoutes) = {
        currentActorSystem().actorOf(Props(new MarketCalcFilterActor(originSender,msr)),"market")
    }
}

class MarketCalcFilterActor(originSender : ActorRef, msr : MarketMessageRoutes) extends Actor with ActorLogging {
    var temp : Option[Boolean] = None
    var rstHospData = msr.hosp_data
    var rstHospMarchData = msr.hosp_match_data
    var rstMarket = msr.admin_market
    var rstUserMarket = msr.user_market
    var modelRun: Option[Stream[modelRunData]] = None
    
//    msr.data match {
//        case Some(hospdata) => hospdata match { 
//            case tmp : Stream[Any] => {
//                 tmp map { x => 
//                     val y = x.asInstanceOf[AdminHospitalDataBase]
//                 }
//            }
//            case _ => Unit
//        }
//        case _ => Unit
//    }
    def receive = {
        case Madmin_hosp_data(hosp_data) => {
            temp = Some(true)
            rstHospData = hosp_data
            log.info("rstHospData进入")
//            (hosp_data) match {
//                case (Some(e1)) => {
//                    log.info(s"e1 size: ${e1.size}")
//                }
//                case _ => Unit
//            }
            rstReturn
        }
        case Madmin_hosp_match(hosp_match_data) => {
            temp = Some(true)
            rstHospMarchData = hosp_match_data
            log.info("rstHospMarchData进入")
            rstReturn
        }
        case Madmin_markets(admin_market) => {
            temp = Some(true)
            rstMarket = admin_market
            log.info("rstMarket进入")
            rstReturn
        }
        case Muser_market(user_market) => {
            temp = Some(true)
            rstUserMarket = user_market
            (rstHospData,rstHospMarchData,rstMarket,rstUserMarket) match {
                case (Some(hospdatabase), Some(hospmatchingdata), Some(market), Some(listCpaMarket)) => {
                    val time = RunDate.startDate()
                    log.info(s"aaaaaaaaaaaaa =================== /////////${hospdatabase.size}")
                    log.info(s"bbbbbbbbbbbbb =================== /////////${hospmatchingdata.size}")
                    log.info(s"ccccccccccccc =================== /////////${market.size}")
                    log.info(s"ddddddddddddd =================== /////////${listCpaMarket.size}")
                    
                    lazy val elem3 = market filter(_.getDatasource.equals("CPA")) sortBy(_.getMinMarket)
                    lazy val elem2 = hospmatchingdata sortBy (_.getHospNum)
                    lazy val elem1 = listCpaMarket sortBy (x => (x.getHospNum,x.getMarketname))
                    lazy val hospNum = elem1.map (_.getHospNum).distinct
                    val integratedData = maxUnionAlgorithm.market(elem1,elem2,elem3)((e1,e2) => StringOption.takeStringSpace(e1.getMarketname).equals(e2.getMinMarket))
                
                    /**
                     * 拼接计算表
                     */
                    lazy val data_max = maxCalcUnionAlgorithm(integratedData,hospdatabase)
                
                    /**
                     * 回填sumvalue、volumeunit
                     */
                    val data_max_new = backWriterSumVolumFunction(data_max.sortBy(x => x.sortConditions1), integratedData.sortBy(y => y.sortConditions1))(x => x.sortConditions1)(y => y.sortConditions1)
                
                    /**
                     * 开始计算
                     */
                    maxCalcAlgorithm(data_max_new)
                    
                    modelRun  = Some(data_max_new)
                    log.info(s"data_max_new data size : ${data_max_new.size}")
                    log.info(s"data_max_new data sum value : ${data_max_new.map(_.finalResultsValue).sum}")
                    RunDate.endDate("最终计算时间", time)
                }
                case _ => Unit
            }
            
            log.info("user_market进入")
            rstReturn
        }
        case timeout() => {
            log.info("timeout超时")
            originSender ! new timeout
            timeOutSchdule.cancel
			context.stop(self)
        }
        case _ => ???
    }
    
    val timeOutSchdule = context.system.scheduler.scheduleOnce(10*60 second, self, new timeout)
    
    def rstReturn = temp match {
        case Some(_) => {
            println("msr.list ==================++++++++++++++++====================="+msr.list.size)
            log.info("rstReturn===================================进入 ")
            (rstHospData) match {
                case (Some(e1)) => 
                    msr.list match {
                        case Nil => {
                            (modelRun) match {
                                case (Some(e2)) => {
                                    log.info(s"modelRun data sum value : ${e2.map(_.finalResultsValue).sum}")
                                }
                                case _ => ???
                            }
                            log.info("没有了最后一个返回")
                            originSender ! resultModelRun(modelRun)
                        }
                        case head :: tail => {
                            val handle = MarketCalcFilterActor(originSender,MarketMessageRoutes(tail,rstHospData,rstHospMarchData,rstMarket,rstUserMarket))
                            handle ! head
                        }
                    }
                    timeOutSchdule.cancel
				    context.stop(self)
				    case _ => Unit
            }
//            (rstHospMarchData) match {
//                case (Some(e2)) => 
////                    println("===============++++++++++e2+++++++++++=====================")
////                    println(e2.size)
//                    msr.list match {
//                        case Nil => originSender ! resultModelRun(modelRun)
//                        case head :: tail => {
//                            val handle = MarketCalcFilterActor(originSender,MarketMessageRoutes(tail,rstHospData,rstHospMarchData,rstMarket,rstUserMarket))
//                            handle ! head
//                        }
//                    }
//                    timeOutSchdule.cancel
//				    context.stop(self)
//                    case _ => Unit
//            }
//            (rstMarket) match {
//                case (Some(e3)) => {
////                    println("===============+++++++++++e3++++++++++=====================")
////                    println(e3.size)
//                    msr.list match {
//                        case Nil => originSender ! resultModelRun(modelRun)
//                        case head :: tail => {
//                            val handle = MarketCalcFilterActor(originSender,MarketMessageRoutes(tail,rstHospData,rstHospMarchData,rstMarket,rstUserMarket))
//                            handle ! head
//                        }
//                    }
//                    timeOutSchdule.cancel
//				    context.stop(self)
//                }
//                case _ => Unit
//            }
//            (rstUserMarket) match {
//                case (Some(e4)) => {
////                    println("===============+++++++++++e4++++++++++=====================")
////                    println(e4.size)
//                    msr.list match {
//                        case Nil => originSender ! resultModelRun(modelRun)
//                        case head :: tail => {
//                            val handle = MarketCalcFilterActor(originSender,MarketMessageRoutes(tail,rstHospData,rstHospMarchData,rstMarket,rstUserMarket))
//                            handle ! head
//                        }
//                    }
//                    timeOutSchdule.cancel
//				    context.stop(self)
//                }
//                case _ => Unit
//            }
        }
        case _ => Unit
    }
}
