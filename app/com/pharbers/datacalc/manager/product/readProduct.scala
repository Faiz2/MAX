package com.pharbers.datacalc.manager.product

import excel.model.Manage.AdminProduct
import excel.core.ReadExcel2007
import scala.collection.JavaConversions
import com.pharbers.datacalc.manager.common.dataAdapter
import com.pharbers.datacalc.manager.common.xmlOpt
import com.pharbers.datacalc.manager.common.LoadEnum

object readProduct {
    def apply(fileDir: String) = new readProduct(new readProductAdapter,fileDir)
}

class readProduct(adapter: dataAdapter , fiel: String){
    lazy val listProduct = {
        val objProduct = new ReadExcel2007(fiel)
        val listObjProduct = objProduct.readExcel(objProduct, new AdminProduct().getClass, 1, false, false, adapter.fieldNamesProductData, adapter.titleProductData)
        JavaConversions.asScalaBuffer(listObjProduct).toStream
    }
}

sealed class readProductAdapter extends dataAdapter {
    override def titleProductData = xmlOpt(LoadEnum.title_product_data.t)
    override def fieldNamesProductData = xmlOpt(LoadEnum.field_product_data.t)
}
