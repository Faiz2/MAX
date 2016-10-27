package com.pharbers.datacalc.manager.product

import excel.model.Manage.AdminProduct
import excel.core.ReadExcel2007
import scala.collection.JavaConversions

object readProduct {
    //"""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\产品匹配表汇总.xlsx"""
    def apply(fileDir: String) :Stream[AdminProduct] = {
        val titleProduct = Array("数据来源", "最小产品单位", "最小产品单位（标准_中文）", "最小产品单位（标准_英文）", "生产厂家（标准_中文）", "生产厂家（标准_英文）",
            "通用名（标准_中文）", "通用名（标准_英文）", "商品名（标准_中文）", "商品名（标准_英文）",
            "剂型（标准_中文）", "剂型（标准_英文）", "药品规格（标准_中文）", "药品规格（标准_英文）", "包装数量（标准_中文）", "包装数量（标准_英文）", "SKU（标准_中文）", "SKU（标准_英文）",
            "市场I（标准_中文）", "市场I（标准_英文）", "市场II（标准_中文）", "市场II（标准_英文）", "市场III（标准_中文）", "市场III（标准_英文）", "市场IV（标准_中文）", "市场IV（标准_英文）")
        val fieldNamesProduct = Array("datasource", "minimumUnit", "minimumUnitCh", "minimumUnitEn", "manufacturerCh",
            "manufacturerEn", "generalnameCh", "generalnameEn", "tradenameCh", "tradenameEn", "dosageformsCh",
            "dosageformsEn", "drugspecificationsCh", "drugspecificationsEn", "numberPackagingCh", "numberPackagingEn", "skuCh", "skuEn",
            "market1Ch", "market1En", "market2Ch", "market2En", "market3Ch", "market3En", "market4Ch", "market4En")
        val objProduct = new ReadExcel2007(fileDir)
        val listProduct = objProduct.readExcel(objProduct, new AdminProduct().getClass, 1, false, false, fieldNamesProduct, titleProduct)
        JavaConversions.asScalaBuffer(listProduct).toStream
    }
}