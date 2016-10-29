package com.pharbers.datacalc.manager.hospdatabase

import excel.model.Manage.AdminHospitalDataBase
import excel.core.ReadExcel2007
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.common.xmlOpt
import com.pharbers.datacalc.manager.common.LoadEnum
import com.pharbers.datacalc.manager.common.dataAdapter

object readHospDataBase {
    def apply(fileDir: String) = new readHospDataBase(
                                    new readHospDataAdapter, fileDir)
}

class readHospDataBase(adapter : dataAdapter, file : String) {
    lazy val listHospDataBase = {
        val objHospDataBase = new ReadExcel2007(file)
        val listHospDataBase = objHospDataBase.readExcel(objHospDataBase, new AdminHospitalDataBase().getClass, 1, 
                                                            false, false, adapter.fieldNamesHospDataBase, adapter.titleHospDataBase)
        JavaConversions.asScalaBuffer(listHospDataBase).toStream
    }
}

sealed class readHospDataAdapter extends dataAdapter {
    override def titleHospDataBase = xmlOpt(LoadEnum.title_data.t)
    override def fieldNamesHospDataBase = xmlOpt(LoadEnum.field_data.t)
}