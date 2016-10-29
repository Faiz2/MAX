package com.pharbers.datacalc.manager.pharmatrust

import com.pharbers.datacalc.manager.common.LoadEnum
import excel.core.ReadExcel2007
import com.pharbers.datacalc.manager.common.dataAdapter
import com.pharbers.datacalc.manager.common.xmlOpt
import scala.collection.JavaConversions
import excel.model.CPA.CpaProduct
import excel.model.PharmaTrust.PharmaTrustPorduct

object readPhaProductData {
     def apply(fileDir: String) = new readPhaProductData(new readPhaProductDataAdapter,fileDir)
}

class readPhaProductData(adapter: dataAdapter,file: String) {
    lazy val listPharmaTrustProduct = {
        val objPharmaTrustProduct = new ReadExcel2007(file)
        val listObjPharmaTrustProduct = objPharmaTrustProduct.readExcel(objPharmaTrustProduct, new PharmaTrustPorduct().getClass, 1, false, false, adapter.fieldNamesPhaProductData, adapter.titlePhaProductData)
        JavaConversions.asScalaBuffer(listObjPharmaTrustProduct).toStream
    }
}

sealed class readPhaProductDataAdapter extends dataAdapter {
    override def titlePhaProductData = xmlOpt(LoadEnum.title_pha_product_data.t)
    override def fieldNamesPhaProductData = xmlOpt(LoadEnum.field_pha_product_data.t)
}