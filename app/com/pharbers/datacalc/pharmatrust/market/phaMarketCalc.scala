package com.pharbers.datacalc.pharmatrust.market

import com.pharbers.datacalc.manager.hospdatabase.readHospDataBase
import excel.core.ReadExcel2007
import com.pharbers.datacalc.manager.market.readMarket
import com.pharbers.util.RunDate
import com.pharbers.datacalc.manager.hospmatchingdata.readHospMatchData
import scala.collection.JavaConversions
import excel.model.CPA.CpaMarket
import excel.model.integratedData
import com.pharbers.datacalc.common.backWriterSumVolumFunction
import excel.model.modelRunData
import java.io.PrintWriter
import excel.model.PharmaTrust.PharmaTrustMarket
import com.pharbers.datacalc.common.CalcData

object phaMarketCalc extends App{
    val start = RunDate.startDate()
    /**
     * 医院数据库
     */
    var time = RunDate.startDate()
    val hospdatabase = readHospDataBase("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx""").listHospDataBase
    println(hospdatabase.size)
    RunDate.endDate("医院数据库", time)
    
    /**
     * 样本医院
     */
    time = RunDate.startDate()
    val hospmatchingdata = readHospMatchData("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx""").listHospDataBase
    println(hospmatchingdata.size)
    RunDate.endDate("样本医院", time)
    
    /**
     * 市场匹配
     */
    time = RunDate.startDate()
    val market = readMarket("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_市场匹配表_2016_HTN.xlsx""")
    println(market.size)
    RunDate.endDate("市场匹配", time)
    
    /**
     * 用户上传phaMarket数据
     */
    time = RunDate.startDate()
    val titlePhaMarket = Array("省","城市","年","月份","医院编码","医院等级","市场名","商品名","规格","剂型","包装规格","给药途径","最小制剂单位","金额","生产企业")
    val fieldNamesPhaMarket = Array("province", "city", "uploadYear", "uploadMonth", "hospNum", "hospLevel", "marketname", "tradename", "drugspecifications", "dosageforms", "numberPackaging", "routeAdministration", "volumeUnit", "sumValue", "manufacturer")
    val objPhaMarket = new ReadExcel2007("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\201601-07-PharmaTrust-HTN市场数据待上传.xlsx""")
    val listPhaMarket = objPhaMarket.readExcel(objPhaMarket, new PharmaTrustMarket().getClass, 1, false, false, fieldNamesPhaMarket, titlePhaMarket)
    println(listPhaMarket.size)
    RunDate.endDate("用户上传phaMarket数据", time)
    
    /**
     * 整合数据List
     */
    lazy val elem3 = market filter(_.getDatasource.equals("PharmaTrust")) sortBy(_.getMinMarket)
    lazy val elem2 = hospmatchingdata sortBy (_.getHospNum)
    lazy val elem1 = JavaConversions.asScalaBuffer(listPhaMarket).toStream sortBy (x => (x.getHospNum,x.getMarketname))
    time = RunDate.startDate()
    lazy val hospNum = elem1.map (_.getHospNum).distinct
    val integratedData = (elem1.filter(x => hospNum.contains(x.getHospNum)).map{x =>
        val cpaMarket = x
        val market_opt = elem3.find(z => z.getMinMarket.equals(x.getMarketname))
        val hospMatch_opt = elem2.find(_.getHospNum == x.getHospNum)
        (market_opt,hospMatch_opt) match{
            case (Some(market),Some(hospMatch)) =>
                Some(new integratedData(cpaMarket.getUploadYear, cpaMarket.getUploadMonth, hospMatch.getDatasource, hospMatch.getHospNum, cpaMarket.getSumValue, cpaMarket.getVolumeUnit, market.getMinMarket, market.getMinMarketCh, market.getMinMarketEn, null, null, null, null, null, null, null, null, null, null, null, null, null, null, market.getMarket1Ch, market.getMarket1En, hospMatch.getHospNameCh, hospMatch.getHospNameEn, hospMatch.getHospLevelCh, hospMatch.getHospLevelEn, hospMatch.getAreaCh, hospMatch.getAreaEn, hospMatch.getProvinceCh, hospMatch.getProvinceEn, hospMatch.getCityCh, hospMatch.getCityEn))
            case _ =>None
        }
    }).filter(x => x != None).map(x => x match{
        case Some(y) => y
        case _ => ???
    })
    println(integratedData.size)
    RunDate.endDate("整合数据List", time)
    /**
     * 拼接计算表
     */
    time = RunDate.startDate()
    lazy val output = integratedData.groupBy(x =>(x.getUploadYear,x.getUploadMonth,x.getMinimumUnitCh)).map(_._2.head).toStream
    println("output==="+output.size.toString)
    lazy val data_max = (output map{element2 =>
        hospdatabase map{element =>
            new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
        }
    }).flatten
    println(data_max.size)
    RunDate.endDate("拼接计算表", time)
    
    /**
     * 开始计算
     */
    time = RunDate.startDate()
    val data_max_new = CalcData(data_max,integratedData)
    println(data_max_new.size)
    val aa = new PrintWriter("""D:/123.txt""")
    data_max_new filter (x => x.finalResultsValue != 0 && x.finalResultsUnit != 0) foreach (x => aa.println(x))
    aa.close()
    RunDate.endDate("开始计算", time)
    RunDate.endDate("读取", start)
}