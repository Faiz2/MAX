package com.pharbers.datacalc.manager.cpa

import com.pharbers.datacalc.manager.common.dataAdapter
import com.pharbers.datacalc.manager.common.LoadEnum.title_cpa_product_data
import com.pharbers.datacalc.manager.common.xmlOpt
import com.pharbers.datacalc.manager.common.LoadEnum.field_cpa_product_data
import com.pharbers.datacalc.manager.common.LoadEnum
import excel.core.ReadExcel2007
import excel.model.CPA.CpaProduct
import scala.collection.JavaConversions


object readCpaProductData {
    
    def apply(fileDir: String) = new readCpaProductData(new readCpaProductDataAdapter,fileDir)
}

class readCpaProductData(adapter: dataAdapter,file: String) {
    lazy val listCpaProduct = {
        val objCpaProduct = new ReadExcel2007(file)
        val listObjCpaProduct = objCpaProduct.readExcel(objCpaProduct, new CpaProduct().getClass, 1, false, false, adapter.fieldNamesCpaProductData, adapter.titleCpaProductData)
        JavaConversions.asScalaBuffer(listObjCpaProduct).toStream
    }
}

sealed class readCpaProductDataAdapter extends dataAdapter {
    override def titleCpaProductData = xmlOpt(LoadEnum.title_cpa_product_data.t)
    override def fieldNamesCpaProductData = xmlOpt(LoadEnum.field_cpa_product_data.t)
}