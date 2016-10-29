package com.pharbers.datacalc.manager.cpa

import com.pharbers.datacalc.manager.common.LoadEnum
import excel.core.ReadExcel2007
import com.pharbers.datacalc.manager.common.dataAdapter
import com.pharbers.datacalc.manager.common.xmlOpt
import scala.collection.JavaConversions
import excel.model.CPA.CpaMarket

object readCpaMarketData {
    def apply(fileDir: String) = new readCpaMarketData(new readCpaMarketDataAdapter,fileDir) 
}

class readCpaMarketData(adapter: dataAdapter,file: String) {
    lazy val listCpaMarket = {
        val objCpaMarket = new ReadExcel2007(file)
        val listObjCpaMarket = objCpaMarket.readExcel(objCpaMarket, new CpaMarket().getClass, 1, false, false, adapter.fieldNamesCpaMraketData, adapter.titleCpaMarketData)
        JavaConversions.asScalaBuffer(listObjCpaMarket).toStream
    }
}

sealed class readCpaMarketDataAdapter extends dataAdapter {
    override def titleCpaMarketData = xmlOpt(LoadEnum.title_cpa_market_data.t)
    override def fieldNamesCpaMraketData = xmlOpt(LoadEnum.field_cpa_market_data.t)
}