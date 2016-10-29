package com.pharbers.datacalc.manager.hospmatchingdata

import excel.model.Manage.AdminHospitalDataBase
import excel.model.Manage.AdminHospitalMatchingData
import excel.core.ReadExcel2007
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.common.xmlOpt
import com.pharbers.datacalc.manager.common.LoadEnum
import com.pharbers.datacalc.manager.common.dataAdapter

object readHospMatchData {
      def apply(fileDir: String) = new readHospMatchData(new readHospMatchDataAdapter, fileDir)
}

class readHospMatchData(adapter : dataAdapter, file : String) {
    lazy val listHospMatchDataBase = {
        val objHospDataBase = new ReadExcel2007(file)
        val listHospDataBase = objHospDataBase.readExcel(objHospDataBase, new AdminHospitalMatchingData().getClass, 1, false, false, adapter.fieldNamesHospMatchingData, adapter.titleHospMatchingData)
        JavaConversions.asScalaBuffer(listHospDataBase).toStream
    }
}

sealed class readHospMatchDataAdapter extends dataAdapter {
    override def titleHospMatchingData = xmlOpt(LoadEnum.title_match.t)
    override def fieldNamesHospMatchingData = xmlOpt(LoadEnum.field_match.t)
}