package com.pharbers.datacalc.cpa.market

import com.pharbers.util.RunDate
import com.pharbers.datacalc.manager.hospdatabase.readHospDataBase
import com.pharbers.datacalc.manager.hospmatchingdata.readHospMatchData
import com.pharbers.datacalc.manager.market.readMarket
import excel.model.CPA.CpaMarket
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminMarket
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.algorithm.backWriterSumVolumFunction
import com.pharbers.datacalc.manager.cpa.readCpaMarketData
import com.pharbers.util.StringOption
import com.pharbers.datacalc.manager.algorithm.maxUnionAlgorithm
import com.pharbers.datacalc.manager.algorithm.maxCalcUnionAlgorithm
import com.pharbers.datacalc.manager.algorithm.maxCalcAlgorithm

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import akka.actor.Actor
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import akka.actor.ActorRef
import scala.concurrent.Await
import com.pharbers.datacalc.manager.domestic._
import akka.actor.ActorSystem
import play.api.Play.current
import play.api.libs.concurrent.Akka
import akka.actor.ActorLogging
import com.pharbers.datacalc.manager.maxmessage.excute
import com.pharbers.datacalc.manager.maxmessage.MarketMessageRoutes
import com.pharbers.datacalc.manager.maxmessage.DataMessage._

case class start()

object cpaMarketCalc extends App {
    
    implicit val timeout = Timeout(10*60 second)
    
//     val act = currentActorSystem().actorOf(Props[MarketRoutesActor], "main")
//     val r = act ? excute(MarketMessageRoutes)
    
    class ExcelReadActor extends Actor with ActorLogging {
        var admin_hosp_data: Option[Stream[AdminHospitalDataBase]] = None
        var admin_hsop_match_data : Option[Stream[AdminHospitalMatchingData]] = None
        var admin_market_data : Option[Stream[AdminMarket]] = None
        var user_market : Option[Stream[CpaMarket]] = None

        def receive = {
            case start() => {
                val tmp = context.actorOf(Props(new ExcelReadActor), "cpamarkettemp")
                val adminActor = currentActorSystem().actorOf(Props[DomesticActor])
                val userActor = currentActorSystem().actorOf(Props[DomesticUserActor])
                adminActor.tell(admin_market(
                        """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx""",
                        """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx""",
                        """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_市场匹配表_2016_HTN.xlsx"""
                        ), tmp)
                userActor.tell(cpaMarket("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\201601-07-CPA-HTN市场数据待上传.xlsx"""), tmp)
            }
            
            case DomesticTimeOut => log.error("读取文件错误或者文件不存在")
            case ExcelMakertResult(hosp_data, hosp_data_match, market_data) => {
                log.info("XXX.pronhub.com")
                admin_hosp_data = Some(hosp_data)
                admin_hsop_match_data = Some(hosp_data_match)
                admin_market_data = Some(market_data)
                startCalcMarket
            }
            case ExcelCpaMarketResult(data) => {
                log.info("用户上传数据读取成功......")
                user_market = Some(data)
                startCalcMarket
            }
            case _ => Unit
        }
        
        def startCalcMarket = (admin_hosp_data, admin_hsop_match_data, admin_market_data, user_market) match {
            case (Some(hospdatabase), Some(hospmatchingdata), Some(market), Some(listCpaMarket)) => {
//                log.info(s"hospdatabase size: ${hospdatabase.size}")
//                log.info(s"hospmatchingdata size: ${hospmatchingdata.size}")
//                log.info(s"market size: ${market.size}")
//                log.info(s"listCpaMarket size: ${listCpaMarket.size}")
                
                log.info("start calc")
//                log.info(s"admin_hosp_data size: ${admin_hosp_data.size}")
                val act = currentActorSystem().actorOf(Props[MarketRoutesActor], "main")
                
                val r = act ? excute(MarketMessageRoutes((Madmin_hosp_data(admin_hosp_data) :: 
                                                             Madmin_hosp_match(admin_hsop_match_data) ::
                                                             Madmin_markets(admin_market_data) ::
                                                             Muser_market(user_market) ::
                                                             Nil),None,None,None,None))
//                val r = act ? excute(MarketMessageRoutes((Madmin_hosp_data(admin_hosp_data) :: 
//                                                             Madmin_hosp_match(admin_hsop_match_data) ::
//                                                             Madmin_markets(admin_market_data) ::
//                                                             Muser_market(user_market) ::
//                                                             Nil),None))

                log.info("end calc")
                context.stop(self)
            }
            case _ => Unit
        }
    }
    
    def apply() = currentActorSystem().actorOf(Props(new ExcelReadActor), "cpamarket") ! (new start)
    cpaMarketCalc()
}