package com.pharbers.datacalc.manager.common

trait dataAdapter {
    /************************管理员***************************/
    def titleHospDataBase : Array[String] = Array.empty
    def fieldNamesHospDataBase : Array[String] = Array.empty
    
    def titleHospMatchingData : Array[String] = Array.empty
    def fieldNamesHospMatchingData : Array[String] = Array.empty
    
    def titleMarketData : Array[String] = Array.empty
    def fieldNamesMarkertData : Array[String] = Array.empty
    
    def titleProductData : Array[String] = Array.empty
    def fieldNamesProductData : Array[String] = Array.empty
    
    /************************用户***************************/
    def titleCpaProductData : Array[String] = Array.empty
    def fieldNamesCpaProductData : Array[String] = Array.empty
    def titleCpaMarketData : Array[String] = Array.empty
    def fieldNamesCpaMraketData : Array[String] = Array.empty
    
    def titlePhaProductData : Array[String] = Array.empty
    def fieldNamesPhaProductData : Array[String] = Array.empty
    def titlePhaMarketData : Array[String] = Array.empty
    def fieldNamesPhaMarketData : Array[String] = Array.empty
}