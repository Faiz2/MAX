package com.pharbers.datacalc.manager.market

import excel.model.Manage.AdminMarket
import excel.core.ReadExcel2007
import scala.collection.JavaConversions


object readMarket {
    
    def apply(fileDir: String): Stream[AdminMarket] = {
        val titleMarket = Array("数据来源","最小市场分类","最小产品单位（标准_中文）","最小产品单位（标准_英文）","市场I（标准_中文）","市场I（标准_英文）","市场II（标准_中文）","市场II（标准_英文）","市场III（标准_中文）","市场III（标准_英文）","市场IV（标准_中文）","市场IV（标准_英文）")
        val fieldNamesMarket = Array("datasource", "minMarket", "minMarketCh", "minMarketEn", "market1Ch", "market1En", "market2Ch", "market2En", "market3Ch", "market3En", "market4Ch", "market4En")
        val objMarket = new ReadExcel2007(fileDir)
        val listMarket = objMarket.readExcel(objMarket, new AdminMarket().getClass, 1, false, false, fieldNamesMarket, titleMarket)
        JavaConversions.asScalaBuffer(listMarket).toStream
    }
}