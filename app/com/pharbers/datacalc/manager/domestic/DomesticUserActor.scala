package com.pharbers.datacalc.manager.domestic

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import akka.actor.Actor
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import akka.actor.ActorRef
import scala.concurrent.Await
import play.api.Application
import play.api.libs.concurrent._

import com.pharbers.datacalc.manager.cpa._
import com.pharbers.datacalc.manager.pharmatrust._
import com.pharbers.datacalc.manager.domestic.MessageDefines.consumer._
import excel.model.CPA.CpaProduct
import excel.model.CPA.CpaMarket
import excel.model.PharmaTrust.PharmaTrustPorduct
import excel.model.PharmaTrust.PharmaTrustMarket
import com.pharbers.util.RunDate
import akka.actor.ActorLogging


case class cpaProduct(cpaProduct: String)
case class cpaMarket(cpaMarket: String)
case class phaProduct(phaProduct: String)
case class phaMarket(phaMarket: String)

case class ExcelCpaProductResultSub()
case class ExcelCpaProductResult(cpaProduct: Stream[CpaProduct])
//case class ExcelCpaMarketResultSub()
case class ExcelCpaMarketResult(cpaMarket: Stream[CpaMarket])

case class ExcelPhaProductResultSub()
case class ExcelPhaProductResult(phaProduct: Stream[PharmaTrustPorduct])
case class ExcelPhaMarketResultSub()
case class ExcelPhaMarketResult(phaMarket: Stream[PharmaTrustMarket])
case class DomesticUserTimeOut()

object DomesticUserActor {
    def apply(app: Application) = Akka.system(app).actorOf(Props[DomesticUserActor])
}

class DomesticUserActor extends Actor with ActorLogging{
    def receive = {
        case cpaProduct(cpaProduct) => {
            val calc = context.actorOf(Props(DomesticUserActorImpl(sender)))
            log.info("read cpa product")
            calc.tell(msgReadCpaProduct(cpaProduct), calc)
        }
        case cpaMarket(cpaMarket) => {
            val tmp = sender
            val calc = context.actorOf(Props(DomesticUserActorImpl(tmp)))
            log.info("read cpa market")
            calc.tell(msgReadCpaMarket(cpaMarket), calc)
        }
        case phaProduct(phaProduct) => {
            val calc = context.actorOf(Props(DomesticUserActorImpl(sender)))
            log.info("read pha product")
            calc.tell(msgReadPhaProduct(phaProduct), calc)
        }
        case phaMarket(phaMarket) => {
            val calc = context.actorOf(Props(DomesticUserActorImpl(sender)))
            log.info("read pha market")
            calc.tell(msgReadPhaMarket(phaMarket), calc)
        }
    }
}

object DomesticUserActorImpl {
    def apply(originSender: ActorRef) = new DomesticUserActorImpl(originSender)
}

class DomesticUserActorImpl(originSender: ActorRef) extends Actor with ActorLogging{
    var cpa_product : Option[Stream[CpaProduct]] = None
    var cpa_market : Option[Stream[CpaMarket]] = None
    var pha_product : Option[Stream[PharmaTrustPorduct]] = None
    var pha_market : Option[Stream[PharmaTrustMarket]] = None
    var time = RunDate.startDate()
    def receive = {
        case msgReadCpaProduct(file) => {
            log.info("msgReadCpaProduct")
            cpa_product = Some(readCpaProductData(file).listCpaProduct)
            log.debug("耗时："+RunDate.endDate("读取msgReadCpaProduct", time).toString)
            ExcelCpaProductResultSub
        }
        case msgReadCpaMarket(file) => {
            log.info("msgReadCpaMarket")
            cpa_market = Some(readCpaMarketData(file).listCpaMarket)
            log.debug("耗时："+RunDate.endDate("读取msgReadCpaMarket", time).toString)
            ExcelCpaMarketResultSub
        }
        case msgReadPhaProduct(file) => {
            log.info("msgReadPhaProduct")
            pha_product = Some(readPhaProductData(file).listPharmaTrustProduct)
            log.debug("耗时："+RunDate.endDate("读取msgReadPhaProduct", time).toString)
//            self ! ExcelPhaProductResultSub
        }
        case msgReadPhaMarket(file) => {
            log.info("msgReadPhaMarket")
            pha_market = Some(readPhaMarketData(file).listPharmaTrustProduct)
            log.debug("耗时："+RunDate.endDate("读取msgReadPhaMarket", time).toString)
//            self ! ExcelPhaMarketResultSub
        }
        case ExcelPhaProductResultSub => {
            (pha_product) match {
                case Some(pha_product_file) => shutDown(ExcelPhaProductResult(pha_product_file))
                case _ => Unit
            }
        }
        case ExcelPhaMarketResultSub => {
            (pha_market) match {
                case Some(pha_market_file) => shutDown(ExcelPhaMarketResult(pha_market_file))
                case _ => Unit
            }
        }
        case DomesticUserTimeOut => shutDown(DomesticUserTimeOut)
        case x: Any => {
            log.error(s"Error $x")
            ???
        }
    }
    
    def ExcelCpaProductResultSub = {
        (cpa_product) match {
            case Some(cpa_product_file) => shutDown(ExcelCpaProductResult(cpa_product_file))
            case _ => Unit
        }
    }
    
    def ExcelCpaMarketResultSub = {
        (cpa_market) match {
            case Some(cpa_markt_file) => shutDown(ExcelCpaMarketResult(cpa_markt_file))
            case _ => Unit
        }
    }
    
    def pushTimeOut = context.system.scheduler.scheduleOnce(Int.MaxValue second, self, DomesticUserTimeOut)
    
    def shutDown(msg: Any) = {
        originSender ! msg
        msg match{
            case DomesticUserTimeOut => log.error("用户读取错误！"+RunDate.endDate("用户总体", time))
            case _ => log.info("用户读取成功！"+RunDate.endDate("用户总体", time))
        }
        context.stop(self)
    }
}