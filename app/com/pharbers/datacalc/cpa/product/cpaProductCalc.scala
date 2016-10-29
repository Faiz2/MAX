package com.pharbers.datacalc.cpa.product

import scala.collection.mutable.ArrayBuffer
import java.util.List
import java.util.Date
import scala.collection.{ JavaConversions }
import java.io.PrintWriter
import com.pharbers.util.StringOption
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminProduct
import excel.model.CPA.CpaProduct
import com.pharbers.datacalc.manager.algorithm.backWriterSumVolumFunction
import com.pharbers.datacalc.manager.algorithm._

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

case class start()

object cpaProductCalc extends App{
    implicit val timeout = Timeout(Int.MaxValue)
    
    class ExcelActorRead extends Actor with ActorLogging{
         var admin_hosp_data : Option[Stream[AdminHospitalDataBase]] = None
         var admin_hosp_match_data : Option[Stream[AdminHospitalMatchingData]] = None
         var admin_product_data : Option[Stream[AdminProduct]] = None
         var user_product_data : Option[Stream[CpaProduct]] = None
         def receive = {
            case start() => {
                val temp = context.actorOf(Props(new ExcelActorRead),"cpaproducttmp")
                val adminActor = currentActorSystem().actorOf(Props[DomesticActor])
                val userActor = currentActorSystem().actorOf(Props[DomesticUserActor])
                adminActor.tell(admin_product(
                    """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx""",
                    """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx""",
                    """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\产品匹配表汇总.xlsx"""
                    ), temp)
                 userActor.tell(cpaProduct("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\2016 01-07-CPA-Taxol产品待上传.xlsx"""),temp)
            }
            
            case DomesticTimeOut => log.error("读取文件错误或者文件不存在")
            
            case ExcelProductResult(hosp_data,hosp_match_data,admin_product) => {
               log.info("ExcelProductResult 进入")
               admin_hosp_data = Some(hosp_data)
               admin_hosp_match_data = Some(hosp_match_data)
               admin_product_data = Some(admin_product)
               startCalcProduct
            }
            case ExcelCpaProductResult(user_data) => {
                log.info("ExcelCpaProductResult 进入")
                user_product_data = Some(user_data)
                startCalcProduct
            }
        }
         
        def startCalcProduct = (admin_hosp_data, admin_hosp_match_data, admin_product_data, user_product_data) match {
            case (Some(hospdatabase),Some(hospmatchingdata),Some(product),Some(listCpaProduct)) => {
                log.info(s"hospdatabase size: ${hospdatabase.size}")
                log.info(s"hospmatchingdata size: ${hospmatchingdata.size}")
                log.info(s"product size: ${product.size}")
                log.info(s"listCpaProduct size: ${listCpaProduct.size}")
                log.debug("start calc")
                lazy val elem1 = listCpaProduct sortBy (x => (x.getHospNum, x.commonObjectCondition))
                lazy val elem2 = product filter (_.getDatasource.equals("CPA")) sortBy (_.getMinimumUnitCh)
                lazy val elem3 = hospmatchingdata sortBy (_.getHospNum)
                val integratedData = maxUnionAlgorithm.product(elem1, elem2, elem3)((e1, e2) => StringOption.takeStringSpace(e2.getMinimumUnit).equals(e1.commonObjectCondition))
                
                /**
                 * 整合表拼接
                 * 钱鹏
                 */
                val data_max = maxCalcUnionAlgorithm(integratedData, hospdatabase)
            
                /**
                 * 回填sumvalue、volumeunit
                 */
                lazy val data_max_new = backWriterSumVolumFunction(data_max.sortBy(x => x.sortConditions1), integratedData.sortBy(y => y.sortConditions1))(x => x.sortConditions1)(y => y.sortConditions1)
                maxCalcAlgorithm(data_max_new)
                
                log.debug("end calc")
                context.stop(self)
            }
            case _ => Unit
        }
    }
    
    def apply() = currentActorSystem().actorOf(Props(new ExcelActorRead),"cpaproduct") ! (new start)
    cpaProductCalc()
}