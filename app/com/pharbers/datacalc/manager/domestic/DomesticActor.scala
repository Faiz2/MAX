package com.pharbers.datacalc.manager.domestic

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

import com.pharbers.datacalc.manager.domestic.MessageDefines.admin._
import com.pharbers.datacalc.manager.hospdatabase._
import com.pharbers.datacalc.manager.hospmatchingdata._
import com.pharbers.datacalc.manager.market._
import com.pharbers.datacalc.manager.product._
import com.pharbers.util.RunDate

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminMarket
import excel.model.Manage.AdminProduct
import com.pharbers.datacalc.manager.maxmessage._

case class admin_market(hosp : String, hosp_match : String, admin_market : String)
case class admin_product(hosp : String, hosp_match : String, admin_product : String)

/********************Admin Market********************/
//case class admin_market_hospdata(hosp_data: String)
//case class admin_market_hospmatchdata(hosp_match: String)
//case class admin_marketdata(admin_market: String)

/********************Admin Product********************/
//case class admin_product_hospdata(hosp_data: String)
//case class admin_product_hospmatchdata(hosp_match: String)
//case class admin_productdata(admin_product: String)

case class DomesticTimeOut()
case class ExcelMakertResult(hosp_data : Stream[AdminHospitalDataBase], hosp_data_match : Stream[AdminHospitalMatchingData], market_data : Stream[AdminMarket])
case class ExcelProductResult(hosp_data : Stream[AdminHospitalDataBase], hosp_data_match : Stream[AdminHospitalMatchingData], product_data : Stream[AdminProduct])

object DomesticActor {
//    def apply()(implicit app: Application) =  Akka.system(app).actorOf(Props[DomesticActor])
    lazy val arf = sys.actorOf(Props[DomesticActor])
    lazy val sys = currentActorSystem()
    def apply() =  arf
}

class DomesticActor extends Actor with ActorLogging {
        /***
         * Admin Maket
         */
//        case admin_market_hospdata(hospdata) => {
//            val tmp = sender
//            val impl = context.actorOf(Props(DomesticActorImpl(tmp)))
//            impl.tell(msgReadHosp(hospdata), sender = impl)
//            log.info("read market hospdatabase")
//        }
//        case admin_market_hospmatchdata(hospmatchdata) => {
//            val tmp = sender
//            val impl = context.actorOf(Props(DomesticActorImpl(tmp)))
//            impl.tell(msgReadHospMatch(hospmatchdata), impl)
//            log.info("read market hospmatchdata")
//        }
//        case admin_marketdata(market) => {
//            val tmp = sender
//            val impl = context.actorOf(Props(DomesticActorImpl(tmp)))
//            impl.tell(msgReadAdminMarket(market), impl)
//            log.info("read marketdata")
//        }
        
        var originSender : ActorRef = null
    def receive = {
        
        case admin_market(hosp, hosp_match, admin_market) => {
            val tmp = sender
            val impl = context.actorOf(Props(DomesticActorImpl(tmp)))
            
            log.info("read admin market")
            impl.tell(msgReadHosp(hosp), sender = impl)
            impl.tell(msgReadHospMatch(hosp_match), impl)
            impl.tell(msgReadAdminMarket(admin_market), impl)
        }
        case admin_product(hosp, hosp_match, admin_product) => {
            val tmp = sender
            val impl = context.actorOf(Props(DomesticActorImpl(tmp)))
            
            log.info("read admin product")
            impl.tell(msgReadHosp(hosp), impl)
            impl.tell(msgReadHospMatch(hosp_match), impl)
            impl.tell(msgReadAdminProduct(admin_product), impl)
        }
    }
}


object DomesticActorImpl {
    def apply(originSender : ActorRef) = new DomesticActorImpl(originSender)
}

class DomesticActorImpl(originSender : ActorRef) extends Actor with ActorLogging {
    var hosp_data : Option[Stream[AdminHospitalDataBase]] = None
    var hosp_data_match : Option[Stream[AdminHospitalMatchingData]] = None
    var admin_market_data : Option[Stream[AdminMarket]] = None
    var admin_product_data : Option[Stream[AdminProduct]] =None
    var time = RunDate.startDate()
    def receive = {
        case msgReadHosp(file) => {
            log.info("msgReadHosp")
            hosp_data = Some(readHospDataBase(file).listHospDataBase)
            log.debug("耗时："+RunDate.endDate("读取msgReadHosp", time).toString)
            ExcelMakertResultSub
            ExcelProductResultSub
        }
        case msgReadHospMatch(file) => {
            log.info("msgReadHospMatch")
            hosp_data_match = Some(readHospMatchData(file).listHospMatchDataBase)
            log.debug("耗时："+RunDate.endDate("读取msgReadHospMatch", time).toString)
            ExcelMakertResultSub
            ExcelProductResultSub
        }
        case msgReadAdminMarket(file) => {
            log.info("msgReadAdminMarket")
            admin_market_data = Some(readMarket(file).listMarket)
            log.debug("耗时："+RunDate.endDate("读取msgReadAdminMarket", time).toString)
            ExcelMakertResultSub

        }
        case msgReadAdminProduct(file) => {
            log.info("msgReadAdminProduct")
            admin_product_data = Some(readProduct(file).listProduct)
            log.debug("耗时："+RunDate.endDate("读取msgReadAdminProduct", time).toString)
            ExcelProductResultSub
        }

        case DomesticTimeOut =>  responseAndShutDown(DomesticTimeOut)
        case x : Any=> {
            log.error(s"Error $x")
            ???
        }
    }

    def ExcelMakertResultSub = {
        (hosp_data, hosp_data_match, admin_market_data) match {
            case (Some(e1), Some(e2), Some(e3)) => 
                responseAndShutDown(ExcelMakertResult(e1, e2, e3))
            case _ => Unit
        }
    }
    
    def ExcelProductResultSub = {
        (hosp_data, hosp_data_match, admin_product_data) match {
          case (Some(e1), Some(e2), Some(e3)) =>
              responseAndShutDown(ExcelProductResult(e1, e2, e3))
          case _ => Unit
        }
    }
    
    def pushTimeOut = context.system.scheduler.scheduleOnce(Int.MaxValue second, self, DomesticTimeOut)
    def responseAndShutDown(msg : Any) = {
        originSender ! msg
        msg match{
            case DomesticTimeOut => log.error("管理员读取错误！"+RunDate.endDate("管理员总体", time))
            case _ => log.info("管理员读取成功！"+RunDate.endDate("管理员总体", time))
        }
        context.stop(self)
    }
}
