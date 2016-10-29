package com.pharbers.datacalc.pharmatrust.product

import com.pharbers.util.RunDate
import excel.core.ReadExcel2007
import excel.model.Manage.AdminHospitalMatchingData
import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminProduct
import com.pharbers.datacalc.manager.product.readProduct
import com.pharbers.datacalc.manager.hospdatabase.readHospDataBase
import com.pharbers.datacalc.manager.hospmatchingdata.readHospMatchData
import excel.model.PharmaTrust.PharmaTrustPorduct
import scala.collection.JavaConversions
import com.pharbers.util.StringOption
import excel.model.modelRunData
import com.pharbers.datacalc.common.backWriterSumVolumFunction
import excel.model.integratedData
import java.io.PrintWriter

object phaProductCalc extends App {
    val start = RunDate.startDate()
    
    /**
     *
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
     * Pharmatrus产品匹配
     */
    time = RunDate.startDate()
    val product = readProduct("""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\产品匹配表汇总.xlsx""")
    println(product.size)
    RunDate.endDate("Pharmatrus", start)

    /**
     * 用户上传PharmaTrust产品数据
     */
    time = RunDate.startDate()
    val titlePharmaTrustProduct = Array("省份", "城市", "年", "月份", "医院编码", "医院等级", "通用名", "商品名", "规格", "剂型", "包装规格", "给药途径", "最小制剂单位数量", "金额", "生产企业")
    val fieldNamesPharmaTrustProduct = Array("province", "city", "uploadYear", "uploadMonth", "hospNum", "hospLevel", "generalname", "tradename", "drugspecifications", "dosageforms", "numberPackaging", "routeAdministration", "volumeUnit", "sumValue", "manufacturer")
    val objPharmaTrustProduct = new ReadExcel2007("""E:\文件\法伯相关\MAX改建\程序测试数据\客户上传\2016 01-07-PharmaTrust-Taxol产品待上传.xlsx""")
    val listPharmaTrustProduct = objPharmaTrustProduct.readExcel(objPharmaTrustProduct, new PharmaTrustPorduct().getClass, 1, false, false, fieldNamesPharmaTrustProduct, titlePharmaTrustProduct)
    println(listPharmaTrustProduct.size)
    RunDate.endDate("用户上传PharmaTrust产品数据", time)

    /**
     * 拼接整合表
     */
    lazy val elem1 = JavaConversions.asScalaBuffer(listPharmaTrustProduct).toStream.sortBy(x => (x.getHospNum, PharmaTrustProductMinunitString(x)))
    lazy val elem2 = product.filter(_.getDatasource.equals("PharmaTrust")).sortBy(_.getMinimumUnitCh)
    lazy val elem3 = hospmatchingdata.sortBy(_.getHospNum)

    def PharmaTrustProductMinunitString(obj: PharmaTrustPorduct): String = {
        StringOption.takeStringSpace(
            obj.getTradename + obj.getManufacturer + obj.getDosageforms + obj.getDrugspecifications + obj.getNumberPackaging)
    }

    time = RunDate.startDate()
    lazy val hospNum = elem1.map(_.getHospNum.asInstanceOf[Number].longValue()).distinct
    val integratedData = (elem1.filter(x => hospNum.contains(x.getHospNum)).map { x =>
        val phaProduct = x
        val product_opt = elem2.find(x_opt => StringOption.takeStringSpace(x_opt.getMinimumUnit).equals(PharmaTrustProductMinunitString(x)))
        val hospMatch_opt = elem3.find(_.getHospNum == x.getHospNum)
        (hospMatch_opt, product_opt) match {
            case (Some(hospMatch), Some(product)) =>
                Some(new integratedData(phaProduct.getUploadYear, phaProduct.getUploadMonth, hospMatch.getDatasource, hospMatch.getHospNum, phaProduct.getSumValue, phaProduct.getVolumeUnit, product.getMinimumUnit, product.getMinimumUnitCh, product.getMinimumUnitEn, product.getManufacturerCh, product.getManufacturerEn, product.getGeneralnameCh, product.getGeneralnameEn, product.getTradenameCh, product.getTradenameEn, product.getDosageformsCh, product.getDosageformsEn, product.getDrugspecificationsCh, product.getDrugspecificationsEn, product.getNumberPackagingCh, product.getNumberPackagingEn, product.getSkuCh, product.getSkuEn, product.getMarket1Ch, product.getMarket1En, hospMatch.getHospNameCh, hospMatch.getHospNameEn, hospMatch.getHospLevelCh, hospMatch.getHospLevelEn, hospMatch.getAreaCh, hospMatch.getAreaEn, hospMatch.getProvinceCh, hospMatch.getProvinceEn, hospMatch.getCityCh, hospMatch.getCityEn))
            case _ => None
        }
    }).filter(x => x != None).map(x => x match {
        case Some(x) => x
        case None    => ???
    })
    println(integratedData.size)
    RunDate.endDate("integratedData数据", time)

    /**
     * 找出不重复数据
     */
    time = RunDate.startDate()
    val output = integratedData.groupBy(x => (x.getUploadYear, x.getUploadMonth, x.getMinimumUnitCh)).map(_._2.head).toStream
    println(output.size)
    RunDate.endDate("output数据", time)

    /**
     * 拼接计算表
     */
    time = RunDate.startDate()
    lazy val hosp_data_base = hospdatabase
    val data_max = (output map { element2 =>
        hosp_data_base map { element =>
            new modelRunData(element.getCompany, element2.uploadYear, element2.uploadMonth, 0.0, 0.0, element2.minimumUnit, element2.minimumUnitCh, element2.minimumUnitEn, element2.manufacturerCh, element2.manufacturerEn, element2.generalnameCh, element2.generalnameEn, element2.tradenameCh, element2.tradenameEn, element2.dosageformsCh, element2.dosageformsEn, element2.drugspecificationsCh, element2.drugspecificationsEn, element2.numberpackagingCh, element2.numberpackagingEn, element2.skuCh, element2.skuEn, element2.market1Ch, element2.market1En, element.getSegment, element.getFactor, element.getIfPanelAll, element.getIfPanelTouse, element.getHospId, element.getHospName, element.getPhaid, element.getIfCounty, element.getHospLevel, element.getRegion, element.getProvince, element.getPrefecture, element.getCityTier, element.getSpecialty1, element.getSpecialty2, element.getReSpecialty, element.getSpecialty3, element.getWestMedicineIncome, element.getDoctorNum, element.getBedNum, element.getGeneralBedNum, element.getMedicineBedNum, element.getSurgeryBedNum, element.getOphthalmologyBedNum, element.getYearDiagnosisNum, element.getClinicNum, element.getMedicineNum, element.getSurgeryNum, element.getHospitalizedNum, element.getHospitalizedOpsNum, element.getIncome, element.getClinicIncome, element.getClimicCureIncome, element.getHospitalizedIncome, element.getHospitalizedBeiIncome, element.getHospitalizedCireIncom, element.getHospitalizedOpsIncome, element.getDrugIncome, element.getClimicDrugIncome, element.getClimicWestenIncome, element.getHospitalizedDrugIncome, element.getHospitalizedWestenIncome, 0.0, 0.0)
        }
    }).flatten
    println(data_max.size)
    RunDate.endDate("data_max数据", time)

    /**
     * 回填sumValue、volumeunit
     */
    time = RunDate.startDate()
    val data_max_new = backWriterSumVolumFunction(data_max.sortBy(x => x.sortConditions1), integratedData.sortBy(y => y.sortConditions1))(x => x.sortConditions1)(y => y.sortConditions1)
    println(data_max_new.count(x => x.sumValue != 0 && x.volumeUnit != 0))
    println(data_max_new.size)
    RunDate.endDate("data_max_new数据", time)

    /**
     * 开始计算
     */
    time = RunDate.startDate()
    val data_calc = data_max_new.toStream

    lazy val max_filter_data = data_calc.filter(_.ifPanelTouse.equals("1")).sortBy(_.segment.toInt)

    lazy val max_calc_distinct = max_filter_data.map(_.segment).distinct

    val sum_data = max_calc_distinct map { x1 =>
        val max_filter = max_filter_data.filter(x => x.segment.equals(x1))
        (x1, (max_filter.map(_.sumValue).sum, max_filter.map(_.volumeUnit).sum, max_filter.map(_.westMedicineIncome).sum))
    }

    sum_data.foreach { x1 =>
        data_max_new filter (x2 => x2.segment.equals(x1._1)) foreach { iter =>
            if (iter.ifPanelAll.equals("1")) {
                iter.finalResultsValue = iter.sumValue
                iter.finalResultsUnit = iter.volumeUnit
            } else {
                iter.finalResultsValue = x1._2._1 / x1._2._3 * iter.westMedicineIncome * iter.factor.toDouble
                iter.finalResultsUnit = x1._2._2 / x1._2._3 * iter.westMedicineIncome * iter.factor.toDouble
            }
        }
    }
    println(data_max_new.size)
    RunDate.endDate("data_max_new数据", time)
    println(data_max_new.count(x => x.finalResultsValue != 0 && x.finalResultsUnit != 0))
    val aa = new PrintWriter("""D:/123.txt""")
    data_max_new filter (x => x.finalResultsValue != 0 && x.finalResultsUnit != 0) foreach (x => aa.println(x))
    aa.close()
    RunDate.endDate("max_calc_data", time)
    RunDate.endDate("读取", start)
}