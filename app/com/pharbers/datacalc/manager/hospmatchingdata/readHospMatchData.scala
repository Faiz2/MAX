package com.pharbers.datacalc.manager.hospmatchingdata

import excel.model.Manage.AdminHospitalMatchingData
import excel.core.ReadExcel2007
import scala.collection.JavaConversions

object readHospMatchData extends App{
    //"""E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\管理员维护_样本医院匹配表_2016_HTN_bpeng.xlsx"""
    def apply(fileDir: String): Stream[AdminHospitalMatchingData] = {
        val titleHospMatchingData = Array("样本医院来源", "样本医院编码", "样本医院名称_中文", "样本医院名称_英文", "样本医院级别_中文", "样本医院级别_英文", "区域_中文", "区域_英文", "省份_中文", "省份_英文", "城市_中文", "城市_英文")
        val fieldNamesHospMatchingData = Array("datasource", "hospNum", "hospNameCh", "hospNameEn", "hospLevelCh", "hospLevelEn", "areaCh", "areaEn", "provinceCh", "provinceEn", "cityCh", "cityEn")
        val objHospMatchingData = new ReadExcel2007(fileDir)
        val listHospMatchingData = objHospMatchingData.readExcel(objHospMatchingData, new AdminHospitalMatchingData().getClass, 1, false, false, fieldNamesHospMatchingData, titleHospMatchingData)
        JavaConversions.asScalaBuffer(listHospMatchingData).toStream
    }
}