package com.pharbers.datacalc.manager.market

import excel.model.Manage.AdminMarket
import excel.core.ReadExcel2007
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.common.dataAdapter
import com.pharbers.datacalc.manager.common.xmlOpt
import com.pharbers.datacalc.manager.common.LoadEnum
import com.pharbers.datacalc.manager.common.LoadEnum.title_market_data


object readMarket {
    def apply(fileDir: String) = new readMarket(new readMarketAdapter,fileDir)
}

class readMarket(adapter : dataAdapter, file : String) {
    lazy val listMarket = {
        val objMarket = new ReadExcel2007(file)
        val listObjMarket = objMarket.readExcel(objMarket, new AdminMarket().getClass, 1, false, false, adapter.fieldNamesMarkertData, adapter.titleMarketData)
        JavaConversions.asScalaBuffer(listObjMarket).toStream
    }
}


sealed class readMarketAdapter extends dataAdapter {
    override def titleMarketData = xmlOpt(LoadEnum.title_market_data.t)
    override def fieldNamesMarkertData = xmlOpt(LoadEnum.field_market_data.t)
}