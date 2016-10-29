package com.pharbers.datacalc.pharmatrust.market

import com.pharbers.datacalc.manager.hospdatabase.readHospDataBase
import com.pharbers.datacalc.manager.market.readMarket
import com.pharbers.datacalc.manager.hospmatchingdata.readHospMatchData
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.algorithm.backWriterSumVolumFunction
import excel.model.PharmaTrust.PharmaTrustMarket
import com.pharbers.datacalc.manager.pharmatrust.readPhaMarketData
import com.pharbers.datacalc.manager.algorithm.maxUnionAlgorithm
import com.pharbers.util.StringOption
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
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminMarket
import com.pharbers.datacalc.cpa.market.cpaMarketCalc
import play.api.libs.concurrent.Akka

class phaMarketCalc{
    implicit val timeout = Timeout(Int.MaxValue)
    
    //phaMarketCalc()
    
    def apply() = {
        /**
         * 医院数据库
         */
//        val hospdatabase_future = DomesticActor() ? readHosp("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx""")
//        
//        /**
//         * 样本医院
//         */
//        val hospmatchingdata_future = DomesticActor() ? readHospMatch("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx""")
//        
//        /**
//         * 市场匹配
//         */
//        val market_future = DomesticActor() ? readAdminMarket("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_市场匹配表_2016_HTN.xlsx""")
//        
//        /**
//         * 用户上传phaMarket数据
//         */
//        val listPhaMarket_future = DomesticActor() ? readPhaMarket("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\201601-07-PharmaTrust-HTN市场数据待上传.xlsx""")
//        
//        val hospdatabase = Await.result(hospdatabase_future.mapTo[Stream[AdminHospitalDataBase]],timeout.duration)
//        val hospmatchingdata = Await.result(hospmatchingdata_future.mapTo[Stream[AdminHospitalMatchingData]], timeout.duration)
//        val market = Await.result(market_future.mapTo[Stream[AdminMarket]], timeout.duration)
//        val listPhaMarket = Await.result(listPhaMarket_future.mapTo[Stream[PharmaTrustMarket]], timeout.duration)
//        println(hospdatabase.size)
//        println(hospmatchingdata.size)
//        println(market.size)
//        println(listPhaMarket.size)
//            
//        /**
//         * 整合数据List
//         */
//        lazy val elem3 = market filter(_.getDatasource.equals("PharmaTrust")) sortBy(_.getMinMarket)
//        lazy val elem2 = hospmatchingdata sortBy (_.getHospNum)
//        lazy val elem1 = listPhaMarket sortBy (x => (x.getHospNum,x.getMarketname))
//        lazy val hospNum = elem1.map (_.getHospNum).distinct
//        val integratedData = maxUnionAlgorithm.market(elem1,elem2,elem3)((e1,e2) => StringOption.takeStringSpace(e1.getMarketname).equals(e2.getMinMarket))
//        
//        /**
//         * 拼接计算表
//         */
//        lazy val data_max = maxCalcUnionAlgorithm(integratedData,hospdatabase)
//        
//        /**
//         * 回填sumvalue、volumeunit
//         */
//        lazy val data_max_new = backWriterSumVolumFunction(data_max.sortBy(x => x.sortConditions1), integratedData.sortBy(y => y.sortConditions1))(x => x.sortConditions1)(y => y.sortConditions1)
//        
//        /**
//         * 开始计算
//         */
//        maxCalcAlgorithm(data_max_new)
//        println(data_max_new.size)
//        println(data_max_new.map(_.finalResultsValue).sum)
//        DomesticActor.sys.shutdown()
    }
}