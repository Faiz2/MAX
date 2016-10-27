package com.pharbers.datacalc.manager.hospdatabase

import excel.model.Manage.AdminHospitalDataBase
import excel.core.ReadExcel2007
import scala.collection.JavaConversions

object readHospDataBase extends App{
//    """E:\文件\法伯相关\MAX改建\程序测试数据\管理员上传\8000家taxol医院数据库表.xlsx"""
    def apply(fileDri: String): Stream[AdminHospitalDataBase] = {
        val titleHospDataBase = Array("公司", "年", "市场", "Segment", "Factor", "If Panel_All", "If Panel_To Use",
            "样本医院编码", "PHA医院名称", "PHA ID", "If County", "Hosp_level", "Region", "Province", "Prefecture", "City Tier 2010", "Specialty_1",
            "Specialty_2", "Re-Speialty", "Specialty 3", "西药收入", "医生数", "床位数", "全科床位数", "内科床位数", "外科床位数", "眼科床位数", "年诊疗人次",
            "门诊诊次", "内科诊次", "外科诊次", "入院人数", "住院病人手术人次数", "医疗收入", "门诊收入", "门诊治疗收入", "门诊手术收入", "住院收入", "住院床位收入",
            "住院治疗收入", "住院手术收入", "药品收入", "门诊药品收入", "门诊西药收入", "住院药品收入", "住院西药收入")
        val fieldNamesHospDataBase = Array("company", "uploadYear", "market", "segment", "factor", "ifPanelAll", "ifPanelTouse", "hospId", "hospName", "phaid", "ifCounty", "hospLevel",
            "region", "province", "prefecture", "cityTier", "specialty1", "specialty2", "reSpecialty", "specialty3", "westMedicineIncome", "doctorNum", "bedNum", "generalBedNum", "medicineBedNum",
            "surgeryBedNum", "ophthalmologyBedNum", "yearDiagnosisNum", "clinicNum", "medicineNum", "surgeryNum", "hospitalizedNum", "hospitalizedOpsNum", "income", "clinicIncome", "climicCureIncome",
            "climicSurgicalIncome", "hospitalizedIncome", "hospitalizedBeiIncome", "hospitalizedCireIncom", "hospitalizedOpsIncome", "drugIncome", "climicDrugIncome", "climicWestenIncome",
            "hospitalizedDrugIncome", "hospitalizedWestenIncome")
        val objHospDataBase = new ReadExcel2007(fileDri)
        val listHospDataBase = objHospDataBase.readExcel(objHospDataBase, new AdminHospitalDataBase().getClass, 1, false, false, fieldNamesHospDataBase, titleHospDataBase)
        JavaConversions.asScalaBuffer(listHospDataBase).toStream
    }
}