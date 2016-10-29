package com.pharbers.datacalc.manager.hospmatchingdata

import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminHospitalMatchingData
import excel.core.ReadExcel2007
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.common.xmlOpt
import com.pharbers.datacalc.manager.common.LoadEnum
import com.pharbers.datacalc.manager.common.dataAdapter

object readHospMatchData {
    //"""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx"""
      def apply(fileDri: String) = new readHospMatchData(
                                        new readHospMatchDataAdapter, fileDri)
}

class readHospMatchData(adapter : dataAdapter, file : String) {
    lazy val listHospDataBase = {
        val objHospDataBase = new ReadExcel2007(file)
        val listHospDataBase = objHospDataBase.readExcel(objHospDataBase, new AdminHospitalMatchingData().getClass, 1, 
                                                            false, false, adapter.titleHospMatchingData, adapter.fieldNamesHospMatchingData)
        JavaConversions.asScalaBuffer(listHospDataBase).toStream
    }
}

sealed class readHospMatchDataAdapter extends dataAdapter {
    override def titleHospMatchingData = xmlOpt(LoadEnum.title_match.t)
    override def fieldNamesHospMatchingData = xmlOpt(LoadEnum.field_match.t)
}