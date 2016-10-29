package com.pharbers.datacalc.manager.pharmatrust

import com.pharbers.datacalc.manager.common.LoadEnum
import excel.core.ReadExcel2007
import com.pharbers.datacalc.manager.common.dataAdapter
import com.pharbers.datacalc.manager.common.xmlOpt
import scala.collection.JavaConversions
import excel.model.PharmaTrust.PharmaTrustPorduct
import excel.model.PharmaTrust.PharmaTrustMarket

object readPhaMarketData {
    def apply(fileDir: String) = new readPhaMarketData(new readPhaMarketDataAdapter,fileDir)
}

class readPhaMarketData(adapter: dataAdapter,file: String) {
    lazy val listPharmaTrustProduct = {
        val objPhaMarket = new ReadExcel2007(file)
        val listObjPhaMarket = objPhaMarket.readExcel(objPhaMarket, new PharmaTrustMarket().getClass, 1, false, false, adapter.fieldNamesPhaMarketData, adapter.titlePhaMarketData)
        JavaConversions.asScalaBuffer(listObjPhaMarket).toStream
    }
}

sealed class readPhaMarketDataAdapter extends dataAdapter {
    override def titlePhaMarketData = xmlOpt(LoadEnum.title_pha_market_data.t)
    override def fieldNamesPhaMarketData = xmlOpt(LoadEnum.field_pha_market_data.t)
}