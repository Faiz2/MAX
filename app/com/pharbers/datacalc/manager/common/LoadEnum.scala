package com.pharbers.datacalc.manager.common

object LoadEnum {
    object title_data extends LoadEnumDefines(0, "读取title中文")
    object field_data extends LoadEnumDefines(1, "读取title英文")
    
    object title_match extends LoadEnumDefines(2, "读取title中文")
    object field_match extends LoadEnumDefines(3, "读取title中文")
}

sealed case class LoadEnumDefines(val t : Int, val des : String)