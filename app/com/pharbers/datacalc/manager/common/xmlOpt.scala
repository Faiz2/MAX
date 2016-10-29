package com.pharbers.datacalc.manager.common

object xmlOpt {
    lazy val hospData = xml.XML.loadFile("config/HospDataStruct.xml")
    lazy val fieldHospData = xml.XML.loadFile("config/FieldNamesHospDataStruct.xml")
    
    def apply(e : Int) : Array[String] = e match {
        case LoadEnum.title_data.t => loadHospDataStruct.toArray
        case LoadEnum.field_data.t => loadFieldNamesHospDataStruct.toArray
        case LoadEnum.title_match.t => loadMatchDataStruct.toArray
        case LoadEnum.field_match.t => loadMatchFieldStruct.toArray
        case _ => ???
    }
    
    var loadHospDataStruct_val : List[String] = Nil
    def loadHospDataStruct = {
        if (loadHospDataStruct_val.isEmpty) 
            loadHospDataStruct_val = ((hospData \ "title").map (x => x.text)).toList
        
            loadHospDataStruct_val
    }

    var loadFieldNamesHospDataStruct_val : List[String] = Nil
    def loadFieldNamesHospDataStruct = {
        if (loadFieldNamesHospDataStruct_val.isEmpty) 
            loadFieldNamesHospDataStruct_val = ((fieldHospData \ "title").map (x => x.text)).toList
        
        loadFieldNamesHospDataStruct_val
    }
    
    var loadMatchDataStruct_val : List[String] = Nil
    def loadMatchDataStruct = {
        if (loadMatchDataStruct_val.isEmpty) 
            loadMatchDataStruct_val = ((hospData \ "title").map (x => x.text)).toList
        
            loadMatchDataStruct_val
    }

    var loadMatchFieldStruct_val : List[String] = Nil
    def loadMatchFieldStruct = {
        if (loadMatchFieldStruct_val.isEmpty) 
            loadMatchFieldStruct_val = ((fieldHospData \ "title").map (x => x.text)).toList
        
        loadMatchFieldStruct_val
    }
}