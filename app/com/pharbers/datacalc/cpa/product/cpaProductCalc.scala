package com.pharbers.datacalc.cpa.product

import scala.collection.mutable.ArrayBuffer
import java.util.List
import java.util.Date
import scala.collection.{ JavaConversions }
import java.io.PrintWriter
import com.pharbers.util.RunDate
import com.pharbers.util.StringOption
import excel.core.ReadExcel2007
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminProduct
import excel.model.CPA.CpaProduct
import excel.model.modelRunData
import com.pharbers.datacalc.manager.product.readProduct
import com.pharbers.datacalc.manager.hospdatabase.readHospDataBase
import com.pharbers.datacalc.manager.hospmatchingdata.readHospMatchData
import com.pharbers.datacalc.common.backWriterSumVolumFunction
import excel.model.integratedData
import com.pharbers.datacalc.common.CalcData

object cpaProductCalc extends App {
    val start = RunDate.startDate()
    /**
     *
     * 医院数据库
     */
    var time = RunDate.startDate()
    val hospdatabase = readHospDataBase("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx""")
    println("hospdatabase size:"+hospdatabase.size.toString)
    RunDate.endDate("医院数据库", time)
    
    /**
     * 样本医院
     */
    time = RunDate.startDate()
    val hospmatchingdata = readHospMatchData("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx""")
    println("hospmatchingdata size:"+hospmatchingdata.size.toString)
    RunDate.endDate("样本医院", time)
    
    /**
     * CPA产品匹配
     */
    time = RunDate.startDate()
    val product = readProduct("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\产品匹配表汇总.xlsx""")
    println("product size:"+product.size.toString)
    RunDate.endDate("CPA产品匹配", time)

    /**
     * 用户上传CPA产品数据
     */
    time = RunDate.startDate()
    val titleCpaProduct = Array("省", "城市", "地区", "年", "月", "医院编码", "ATC码", "药品名称", "商品名", "包装单位", "药品规格", "包装数量", "金额(元)", "数量(支/片)", "剂型", "给药途径", "生产企业")
    val fieldNamesCpaProduct = Array("province", "city", "area", "uploadYear", "uploadMonth", "hospNum", "atcNum", "drugsname", "tradename", "packingunits", "drugspecifications", "numberPackaging", "sumValue", "volumeUnit", "dosageforms", "routeAdministration", "manufacturer")
    val objCpaProduct = new ReadExcel2007("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\2016 01-07-CPA-Taxol产品待上传.xlsx""")
    val listCpaProduct = objCpaProduct.readExcel(objCpaProduct, new CpaProduct().getClass, 1, false, false, fieldNamesCpaProduct, titleCpaProduct)

    println(listCpaProduct.size)
    RunDate.endDate("用户上传CPA产品数据", time)

    /**
     * 整合数据List
     */
    lazy val elem3 = product filter (_.getDatasource.equals("CPA")) sortBy (_.getMinimumUnitCh)
    lazy val elem2 = hospmatchingdata sortBy (_.getHospNum)
    lazy val elem1 = JavaConversions.asScalaBuffer(listCpaProduct).toStream sortBy (x => (x.getHospNum, CpaProduct2MinUnitEnString(x)))

    time = RunDate.startDate()
    def CpaProduct2MinUnitEnString(element: CpaProduct): String = StringOption.takeStringSpace(element.getTradename +
        element.getManufacturer +
        element.getDosageforms +
        element.getDrugspecifications +
        element.getNumberPackaging)

    lazy val hospNum = elem1.map (_.getHospNum).distinct

    val integratedData = ((elem1 filter (x => hospNum.contains(x.getHospNum))) map { x =>
        val cpaProduct = x
        val hospMatch_opt = elem2.find(_.getHospNum == x.getHospNum)
        val product_opt = elem3.find(y => StringOption.takeStringSpace(y.getMinimumUnit).equals(CpaProduct2MinUnitEnString(x)))
        (hospMatch_opt, product_opt) match {
            case (Some(hospMatch), Some(product)) =>
                Some(new integratedData(cpaProduct.getUploadYear, cpaProduct.getUploadMonth, hospMatch.getDatasource, hospMatch.getHospNum, cpaProduct.getSumValue, cpaProduct.getVolumeUnit, product.getMinimumUnit, product.getMinimumUnitCh, product.getMinimumUnitEn, product.getManufacturerCh, product.getManufacturerEn, product.getGeneralnameCh, product.getGeneralnameEn, product.getTradenameCh, product.getTradenameEn, product.getDosageformsCh, product.getDosageformsEn, product.getDrugspecificationsCh, product.getDrugspecificationsEn, product.getNumberPackagingCh, product.getNumberPackagingEn, product.getSkuCh, product.getSkuEn, product.getMarket1Ch, product.getMarket1En, hospMatch.getHospNameCh, hospMatch.getHospNameEn, hospMatch.getHospLevelCh, hospMatch.getHospLevelEn, hospMatch.getAreaCh, hospMatch.getAreaEn, hospMatch.getProvinceCh, hospMatch.getProvinceEn, hospMatch.getCityCh, hospMatch.getCityEn))
            case _ => None
        }
    }).filter(x => x != None).map(x => x match {
        case Some(y) => y
        case None    => ???
    })
    println(integratedData.size)
    RunDate.endDate("integratedData", time)

    /**
     * 2016-10-18
     * 整合表拼接
     * 钱鹏
     */

    time = RunDate.startDate()

    val output = integratedData.groupBy(x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh)).map(_._2.head).toStream
    lazy val hosp_data_base = hospdatabase
    val data_max = (output map { element2 =>
        hosp_data_base map { element =>
            new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
        }
    }).flatten

    RunDate.endDate("data_max", time)


    time = RunDate.startDate()
    val data_max_new = CalcData(data_max,integratedData)
    println(data_max_new.size)
    val aa = new PrintWriter("""D:/123.txt""")
    data_max_new filter (x => x.finalResultsValue != 0 && x.finalResultsUnit != 0) foreach (x => aa.println(x))
    aa.close()
    RunDate.endDate("max_calc_data", time)
    RunDate.endDate("读取", start)
}