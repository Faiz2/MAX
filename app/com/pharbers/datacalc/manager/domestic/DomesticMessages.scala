package com.pharbers.datacalc.manager.domestic

object MessageDefines {
    
    object admin {
        case class msgReadHosp(val file : String)
        case class msgReadHospMatch(val file : String)
        case class msgReadAdminProduct(val file : String)
        case class msgReadAdminMarket(val file: String)
    }
    
    object consumer{
        case class msgReadCpaProduct(val file : String)
        case class msgReadCpaMarket(val file: String)
        case class msgReadPhaProduct(val file: String)
        case class msgReadPhaMarket(val file: String)
    }
}
