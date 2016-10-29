package com.pharbers.datacalc.pharmatrust.product

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

import com.pharbers.datacalc.manager.algorithm._
import com.pharbers.datacalc.manager.algorithm.backWriterSumVolumFunction
import com.pharbers.datacalc.manager.domestic._
import com.pharbers.util.StringOption

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminProduct
import excel.model.PharmaTrust.PharmaTrustPorduct
import excel.model.common.commonProductObjectTrait
import play.api.Play.current
import play.api.libs.concurrent.Akka

class phaProductCalc {
    implicit val timeout = Timeout(Int.MaxValue)
    
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
//         * Pharmatrus产品匹配
//         */
//        val product_future = DomesticActor() ? readAdminProduct("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\产品匹配表汇总.xlsx""")
//    
//        /**
//         * 用户上传PharmaTrust产品数据
//         */
//        val listPharmaTrustProduct_future = DomesticActor() ? readPhaProduct("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\2016 01-07-PharmaTrust-Taxol产品待上传.xlsx""") 
//    
//        
//        val hospdatabase = Await.result(hospdatabase_future.mapTo[Stream[AdminHospitalDataBase]], timeout.duration)
//        val hospmatchingdata = Await.result(hospmatchingdata_future.mapTo[Stream[AdminHospitalMatchingData]], timeout.duration)
//        val product = Await.result(product_future.mapTo[Stream[AdminProduct]], timeout.duration)
//        val listPharmaTrustProduct = Await.result(listPharmaTrustProduct_future.mapTo[Stream[PharmaTrustPorduct]], timeout.duration)
//        println(hospdatabase.size)
//        println(hospmatchingdata.size)
//        println(product.size)
//        println(listPharmaTrustProduct.size)
//        
//        /**
//         * 拼接整合表
//         */
//        lazy val elem1 = listPharmaTrustProduct.sortBy(x => (x.getHospNum, x.commonObjectCondition))
//        lazy val elem2 = product.filter(_.getDatasource.equals("PharmaTrust")).sortBy(_.getMinimumUnitCh)
//        lazy val elem3 = hospmatchingdata.sortBy(_.getHospNum)
//    
//        val integratedData = maxUnionAlgorithm.product(elem1, elem2, elem3)((e1, e2) => StringOption.takeStringSpace(e2.getMinimumUnit).equals(e1.commonObjectCondition))
//    
//        /**
//         * 拼接计算表
//         */
//        val data_max = maxCalcUnionAlgorithm(integratedData, hospdatabase)
//    
//        /**
//         * 回填sumValue、volumeunit
//         */
//        val data_max_new = backWriterSumVolumFunction(data_max.sortBy(x => x.sortConditions1), integratedData.sortBy(y => y.sortConditions1))(x => x.sortConditions1)(y => y.sortConditions1)
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